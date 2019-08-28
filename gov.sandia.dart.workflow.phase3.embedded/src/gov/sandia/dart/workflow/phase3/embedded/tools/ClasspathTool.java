package gov.sandia.dart.workflow.phase3.embedded.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.strikewire.snl.apc.util.ExtensionPointUtils;

import gov.sandia.dart.aprepro.util.ApreproUtil;
import gov.sandia.dart.workflow.phase3.embedded.preferences.EmbeddedExecutionEnvironmentVariables;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.parser.ThrowingErrorHandler;

public class ClasspathTool {
	
	// Generate a UNIX script for running workflow.
	public static String generateBashScript() throws IOException {		
		String java = System.getProperty("java.home") + "/bin/java";
		StringBuilder builder = new StringBuilder("#!/bin/sh\n\n");
		builder.append("export APREPRO_PATH=").append(ApreproUtil.getApreproCommand()).append("\n");
		// TODO System properties substitution
		EmbeddedExecutionEnvironmentVariables.getInstance().
			getAllProperties(false).forEach(p-> builder.append("export ").append(p.getName()).append("=\"").append(p.getValue()).append("\"\n"));
		builder.append("\nCP=");
		builder.append(StringUtils.join(ClasspathTool.getClasspathEntries(), File.pathSeparatorChar));
		builder.append("\n\n");
		builder.append(java);
		builder.append(" \\\n");
		builder.append("  -XX:CICompilerCount=2 \\\n");
		builder.append("  -XX:+ReduceSignalUsage \\\n");
		builder.append("  -XX:+DisableAttachMechanism \\\n");
		builder.append("  -XX:+UseSerialGC \\\n");
		builder.append("  -cp $CP \\\n");
		builder.append("  gov.sandia.dart.workflow.runtime.Main -v \\\n");
		for (String plugin: getPluginDirs()) {
			builder.append("  -p ");
			builder.append(plugin);
			builder.append("  \\\n");
		}
		builder.append("  $*\n\n");
		builder.append("exit $?\n");
		return builder.toString();
	}	
		
	public static String generateBatchFile() throws IOException {		
		StringBuilder builder = new StringBuilder("@echo off\r\n\r\n");
		String java = System.getProperty("java.home") + "\\bin\\java.exe";

		builder.append("set APREPRO_PATH=").append(ApreproUtil.getApreproCommand()).append("\r\n");
		// TODO System properties substitution
		EmbeddedExecutionEnvironmentVariables.getInstance().
			getAllProperties(false).forEach(p-> builder.append("set ").append(p.getName()).append("=\"").append(p.getValue()).append("\"\r\n"));
		builder.append("\r\nset CP=");
		builder.append(StringUtils.join(ClasspathTool.getClasspathEntries(), File.pathSeparatorChar));
		builder.append("\r\n\r\n");		
		builder.append(java);
		builder.append(" ^\r\n");
		builder.append("  -XX:CICompilerCount=2 ^\r\n"); 
		builder.append("  -XX:+ReduceSignalUsage ^\r\n");
		builder.append("  -XX:+DisableAttachMechanism ^\r\n"); 
		builder.append("  -XX:+UseSerialGC ^\r\n");
		builder.append("  -cp %CP% gov.sandia.dart.workflow.runtime.Main -v ^\r\n");
		for (String plugin: getPluginDirs()) {
			builder.append("  -p ");
			builder.append(plugin);
			builder.append(" ^\r\n");
		}
		builder.append("  %1 %2 %3 %4 %5 %6 %7 %8 %9\r\n\r\n");
		builder.append("SET exitcode=%ERRORLEVEL%\r\n");
		builder.append("exit %exitcode%\r\n");
		return builder.toString();
	}	
	public static String[] listRuntimeLibraries() {
		Set<String> plugins = new HashSet<>();
		List<IConfigurationElement> elements = ExtensionPointUtils.getConfigurationElements("gov.sandia.dart.workflow.phase3.embedded", "nodeDefinitionContributor");
		for (IConfigurationElement element: elements) {
			// Anything that implements this extension point is loaded automatically. Does this make sense?
			plugins.add(element.getContributor().getName());

			// This is used for extra libraries that don't contain workflow nodes themselves
			if ("runtimeLibrary".equals(element.getName()))
				plugins.add(element.getAttribute("bundleId"));
		}
		
		
		return (String[]) plugins.toArray(new String[plugins.size()]);
	}
	
	private static Document parseDocument(InputSource source) throws IOException {
		Document doc = null;
		try {
			DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
			dfactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
			docBuilder.setErrorHandler(new ThrowingErrorHandler());
			doc = docBuilder.parse(source);            
		} catch (Exception e) {
			throw new SAWWorkflowException("Error parsing IWF file", e);
		} 
		return doc;
	}
	
	public static String[] getPluginDirs() throws IOException {
		Set<String> entries = new HashSet<>();

		String[] ids = listRuntimeLibraries(); 
		for (String id: ids) {
			Bundle bundle = Platform.getBundle(id);
			if (bundle != null) {
				URL url = FileLocator.find(bundle, new Path(".classpath"), null);
				if (url != null)
					url = FileLocator.resolve(url);
				if (url != null) {
					File file = new File(url.getPath());
					entries.add(file.getParentFile().getCanonicalPath() + File.separator);
				}
				
			}
		}
		return (String[]) entries.toArray(new String[entries.size()]);

	}

	public static String[] getClasspathEntries() throws IOException {
		Set<String> entries = new HashSet<>();

		String[] ids = listRuntimeLibraries(); 
		for (String id: ids) {
			Bundle bundle = Platform.getBundle(id);
			if (bundle != null) {
				URL url = FileLocator.find(bundle, new Path(".classpath"), null);
				if (url != null)
					url = FileLocator.resolve(url);
				if (url == null) {
					// Probably means the plugin is jarred-up. 
					continue;
				}
				// Always include plugin root
				entries.add(makeFolderEntry(url, ""));

				try (InputStream stream = url.openStream()) {
					String data = IOUtils.toString(stream);
					Document doc = parseDocument(new InputSource(new StringReader(data)));
					NodeList list = doc.getElementsByTagName("classpathentry");
					for (int i=0; i<list.getLength(); ++i) {
						Element item = (Element) list.item(i);
						String kind = item.getAttribute("kind");
						if (kind == null) continue;
						switch (kind) {
						case "src":
						case "output": {
							String path = item.getAttribute("path");
							if (path != null && !path.startsWith("/")) {
								String entry = makeFolderEntry(url, path);
								entries.add(entry);
							}
							break;
						}
						case "lib": {
							String path = item.getAttribute("path");
							if (path != null && !path.startsWith("/")) {
								String entry = makeJarEntry(url, path);
								entries.add(entry);		
							}
							break;
						}
						default:
							// Nothing yet							
						}
					}
				} 
			}
		}
		return (String[]) entries.toArray(new String[entries.size()]);
	}

	private static String makeJarEntry(URL url, String path) {
		String entry = new File(url.getPath()).getParent() + File.separator;
		entry = entry + new File(path).getParent() + File.separator + "*";
		return entry;
	}

	private static String makeFolderEntry(URL url, String path) throws IOException {
		String entry = new File(url.getPath()).getParent() + File.separator + path;
		return new File(entry).getCanonicalPath();
	}
}
