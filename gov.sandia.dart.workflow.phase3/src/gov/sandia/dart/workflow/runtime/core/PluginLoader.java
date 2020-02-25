/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

public class PluginLoader {
	
	public static void loadPlugins(File[] plugins, SAWWorkflowLogger log) {
						
		// For each jar file
		for (File file: plugins) {
			try {
				loadOne(file,log);

			} catch (IOException e1){
				log.warn("I/O error \"{0}\" while processing plugin {1}", e1.getMessage(), file.getName());
			} 
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void loadOne(File file, SAWWorkflowLogger log) throws IOException {
		String urlText = FilenameUtils.isExtension(file.getName(), "jar") ?
				"jar:file:" + file.getCanonicalPath()+"!/" :
					file.isDirectory() ?
							file.toURI().toURL().toString() :
							null;
		if (urlText == null)
			return;
		URL[] urls = { new URL(urlText) };
		URLClassLoader cl = URLClassLoader.newInstance(urls, SAWCustomNode.class.getClassLoader());
		URL propsURL = cl.findResource("META-INF/iwf.properties");
		if (propsURL == null)
			propsURL = cl.findResource("resources/iwf.properties");
		if (propsURL == null) {
			log.warn("Plugin {0} does not contain iwf.properties file", file.getName());
			return;
		}
		InputStream stream = propsURL.openStream();
		Properties props = new Properties();
		props.load(stream);

		for (String key: props.stringPropertyNames()) {
			if (key.endsWith(".node")) {
				String typeName = key.substring(0, key.length() - ".node".length());
				String className = props.getProperty(key);
				try {
					Class c = cl.loadClass(className);
					if (SAWCustomNode.class.isAssignableFrom(c)) {
						log.info("Added node type {0} from plugin {1}", typeName, file.getName());
						NodeDatabase.addNodeType(typeName, c);
					} else {
						log.warn("Node class {0} in plugin {1} does not extend SAWCustomNode", className, file.getName());
						continue;
					}		
				} catch (ClassNotFoundException e1) {
					log.warn("Class not found error \"{0}\" while processing plugin {1}", e1.getMessage(), file.getName());
				}
			}
		}
		cl.close();		
	}
}
