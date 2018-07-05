/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;

public class PluginLoader {
	
	static void loadPlugins(File pluginDir, SAWWorkflowLogger log) {
				
		if  (!pluginDir.exists()) {
			log.info("Plugin dir {0} does not exist", pluginDir.getAbsolutePath());
			return;
		}
			
		File[] files = pluginDir.listFiles(f -> {return f.getName().endsWith(".jar");});
		
		if  (files.length == 0) {
			log.info("No extensions found in plugin dir {0}", pluginDir.getAbsolutePath());
			return;
		}
		
		// For each jar file
		for (File file: files) {
			JarFile jarFile = null;
			try {
				jarFile = new JarFile(file);
				URL[] urls = { new URL("jar:file:" + file.getAbsolutePath()+"!/") };
				URLClassLoader cl = URLClassLoader.newInstance(urls, SAWCustomNode.class.getClassLoader());

				InputStream stream = cl.getResourceAsStream("/META-INF/iwf.properties");
				if (stream == null) {
					log.warn("Jar file {0} does not contain /META-INF/iwf.properties file", file.getName());
					continue;
				}

				Properties props = new Properties();
				props.load(stream);

				for (String key: props.stringPropertyNames()) {
					if (key.endsWith(".node")) {
						String typeName = key.substring(0, key.length() - ".node".length());
						String className = props.getProperty(key);
						try {
							Class c = Class.forName(className);
							if (SAWCustomNode.class.isAssignableFrom(c)) {
								log.info("Added node type {0} from plugin jar {1}", typeName, file.getName());
								NodeDatabase.addNodeType(typeName, c);
							} else {
								log.warn("Node class {0} in jar file {1} does not extend SAWCustomNode", className, file.getName());
								continue;
							}		
						} catch (ClassNotFoundException e1) {
							log.warn("Class not found error \"{0}\" while processing jar file {1}", e1.getMessage(), file.getName());
						}
					}
				}
				cl.close();

			} catch (IOException e1){
				log.warn("I/O error \"{0}\" while processing jar file {1}", e1.getMessage(), file.getName());

			} finally {
				if (jarFile != null) {
					IOUtils.closeQuietly(jarFile);
				}
			}

		}
	}
}
