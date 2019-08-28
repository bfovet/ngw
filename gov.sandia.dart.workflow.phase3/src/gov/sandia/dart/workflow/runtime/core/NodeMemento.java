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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class NodeMemento {
	public enum Channel {PROPERTY, INPUT, OUTPUT };
	private static final String FILENAME = "_memento";
	private final String version = "1";
	private Map<String, String> properties = new HashMap<>(); 
	private Map<String, String> inputs = new HashMap<>(); 
	private Map<String, String> outputs = new HashMap<>();
	private String name;
	
	public NodeMemento(String name) {
		this.name = name;
	}

	public NodeMemento addObject(Channel channel, String label, Object data) {
		switch (channel) {
		case INPUT: {
			inputs.put(label, String.valueOf(data));

			break;
		}
		case OUTPUT: {
			Datum d = new Datum("default", data, data.getClass());
			outputs.put(label, (String) d.getAs(String.class));
			break;
		}
		case PROPERTY: {
			properties.put(label, String.valueOf(data));
			break;
		}
		}
		return this;
	}

	private byte[] getBytes(Object data) {
		return String.valueOf(data).getBytes();
	}
	
	public void save(File workdir) throws IOException {
		Properties p = new Properties();
		p.setProperty("memento.version", version);
		addMap(p, properties, Channel.PROPERTY);
		addMap(p, inputs, Channel.INPUT);
		addMap(p, outputs, Channel.OUTPUT);
		try (FileWriter writer = new FileWriter(new File(workdir, filename(name)))) {
			p.store(writer, "Workflow state memento -- do not edit or delete");
		}
	}
	
	private void addMap(Properties p, Map<String, String> map, Channel channel) {
		for (String key: map.keySet()) {
			p.setProperty(channel + "." + key, map.get(key));
		}		
	}
	
	public static boolean hasMemento(String name, File workdir) throws IOException {
		return new File(workdir, filename(name)).exists();
	}

	private static String filename(String name) {
		return name + FILENAME;
	}

	public static NodeMemento load(String nodeName, File workdir) throws IOException {
		try (FileReader reader = new FileReader(new File(workdir, filename(nodeName)))) {
			NodeMemento memento = new NodeMemento(nodeName);
			Properties p = new Properties();
			p.load(reader);
			for (String name: p.stringPropertyNames()) {
				if (name.startsWith(Channel.PROPERTY.toString())) {
					String tag = name.substring(Channel.PROPERTY.toString().length() + 1);
					memento.properties.put(tag, p.getProperty(name));
				} else if (name.startsWith(Channel.INPUT.toString())) {
					String tag = name.substring(Channel.INPUT.toString().length() + 1);
					memento.inputs.put(tag, p.getProperty(name));
				} else if (name.startsWith(Channel.OUTPUT.toString())) {
					String tag = name.substring(Channel.OUTPUT.toString().length() + 1);
					memento.outputs.put(tag, p.getProperty(name));
				}						
			}
			return memento;
		}
	}
	
	public static void delete(String nodeName, File workdir) {
		new File(workdir, filename(nodeName)).delete();
	}
	
	public String comparePropertiesAndInputs(NodeMemento other) {
		String result = compareMap(inputs, other.inputs);
		if (result != null)
			return "input " + result;
		
		result = compareMap(properties, other.properties);
		if (result != null)
			return "property " + result;
		return null;
	}

	private String compareMap(Map<String, String> map1, Map<String, String> map2) {
		if (!map1.keySet().equals(map2.keySet()))
			return "keyset";
		for (String key: map1.keySet()) {
			if (!Objects.equals(map1.get(key), map2.get(key)))
				return key + ", was " + map1.get(key) + ", is " + map2.get(key);
		}
		return null;
	}

	public void remove(File workdir) {
		new File(workdir, filename(name)).delete();
	}

	public void getOutputs(Map<String, Object> results) {
		for (Map.Entry<String, String> entry: outputs.entrySet()) {
			results.put(entry.getKey(), entry.getValue());
		}
	}
}
