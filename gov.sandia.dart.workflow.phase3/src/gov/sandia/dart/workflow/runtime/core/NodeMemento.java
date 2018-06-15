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
import java.util.Properties;
import java.util.zip.Adler32;

import org.apache.commons.io.FileUtils;

public class NodeMemento {
	enum Channel {PROPERTY, INPUT, OUTPUT };
	private static final String FILENAME = "_memento";
	private final String version = "1";
	private Map<String, String> properties = new HashMap<>(); 
	private Map<String, String> inputs = new HashMap<>(); 
	private Map<String, String> outputs = new HashMap<>();
	
	public NodeMemento() {
	}

	public void addObject(Channel channel, String label, Object data) {
		switch (channel) {
		case INPUT: {
			Adler32 algo = new Adler32();
			algo.update(getBytes(data));
			inputs.put(label, String.valueOf(algo.getValue()));
			break;
		}
		case OUTPUT: {
			outputs.put(label, String.valueOf(data));
			break;
		}
		case PROPERTY: {
			Adler32 algo = new Adler32();
			algo.update(getBytes(data));
			properties.put(label, String.valueOf(algo.getValue()));
			break;
		}
		}
	}

	private byte[] getBytes(Object data) {
		return String.valueOf(data).getBytes();
	}

	// TODO Handle spread files, will throw exception if file doesn't exist
	public void addFile(Channel channel, String label, File file) throws IOException {
		Adler32 algo = new Adler32();
		FileUtils.checksum(file, algo);
		switch (channel) {
		case INPUT: inputs.put(label, String.valueOf(algo.getValue())); break;
		case OUTPUT: outputs.put(label, String.valueOf(algo.getValue())); break;
		case PROPERTY: properties.put(label, String.valueOf(algo.getValue())); break;
		}
	}
	
	public void save(File workdir) throws IOException {
		Properties p = new Properties();
		p.setProperty("memento.version", version);
		addMap(p, properties, Channel.PROPERTY);
		addMap(p, inputs, Channel.INPUT);
		addMap(p, outputs, Channel.OUTPUT);
		try (FileWriter writer = new FileWriter(new File(workdir, FILENAME))) {
			p.store(writer, "Workflow state memento -- do not edit or delete");
		}
	}
	
	private void addMap(Properties p, Map<String, String> map, Channel channel) {
		for (String key: map.keySet()) {
			p.setProperty(channel + "." + key, map.get(key));
		}		
	}
	
	public static boolean hasMemento(File workdir) throws IOException {
		return new File(workdir, FILENAME).exists();
	}

	public static NodeMemento load(File workdir) throws IOException {
		try (FileReader reader = new FileReader(new File(workdir, FILENAME))) {
			NodeMemento memento = new NodeMemento();
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
	
	public boolean compareInputs(NodeMemento other) {
		return inputs.equals(other.inputs) && properties.equals(other.properties);
	}

	public boolean isValid() {
		// TODO Actually implement this!
		return true;
	}

	public void remove(File workdir) {
		new File(workdir, FILENAME).delete();
	}

	public void getOutputs(Map<String, Object> results) {
		for (Map.Entry<String, String> entry: outputs.entrySet()) {
			results.put(entry.getKey(), entry.getValue());
		}
	}
	
	
}
