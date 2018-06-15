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
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.googlecode.sarasvati.NodeToken;

public class RuntimeData implements AutoCloseable {

	public static final String DEFAULT_TYPE = "default";
	Map<String, Object> responses = Collections.synchronizedMap(new TreeMap<>());
		
	Map<String, Map<String, Datum>> data = new ConcurrentHashMap<>();
	File workDir;
	private File homeDir;
	private Object engine;
	private IWorkflowMonitor monitor;
	private String sampleId = "DEFAULT";
	
	private PrintWriter out = new PrintWriter(System.out, true);
	private PrintWriter err = new PrintWriter(System.err, true);
	private SAWWorkflowLogger log;
	Map<String, Marker> markers = new ConcurrentHashMap<>();
	private Map<String, String> envVars = new ConcurrentHashMap<>();
	private Map<String, Object> parameters = new ConcurrentHashMap<>();
	
	// TODO Need to decide best pool strategy
	private ExecutorService executor = Executors.newCachedThreadPool();
	private WorkflowProcess facade;
	private Set<ICancelationListener> listeners = ConcurrentHashMap.newKeySet();

	// Constructor is package-protected: only WorkflowProcess should create these.
	RuntimeData(IWorkflowMonitor monitor, File homeDir, File workDir, Object engine, SAWWorkflowLogger log) {
		this.homeDir = homeDir;
		this.workDir = workDir;
		this.engine = engine;
		this.monitor = monitor;
		this.log = log;
	}
		
	public synchronized Collection<String> getInputNames(String node) {
		Map<String, Datum> datums = data.get(node);
		if (datums == null)
			return Collections.emptyList();
		return Collections.unmodifiableCollection(datums.keySet());		
	}
	
	public synchronized Object getInput(String node, String port, Class<?> type) {
		Map<String, Datum> datums = data.get(node);
		if (datums == null)
			return null;
		
		Datum datum = datums.get(port);				
		if (datum == null)
			return null;
				
		Object value = datum.getAs(type);		
		return value;
	}

	synchronized void putInput(String node, String port, String outputType, Object value) {
		Map<String, Datum> datums =  data.get(node);
		if (datums == null)
			data.put(node, datums = new HashMap<>());
		
			
		datums.put(port, new Datum(outputType, value, classOf(value)));		
	}
			
	public Map<String, Object> getResponses() {
		return Collections.unmodifiableMap(responses);
	}
	
	private Class<?> classOf(Object value) {
		return value != null ? value.getClass() : Void.class;
	}
		
	public void setResponse(String name, Object value) {
		if (value instanceof byte[] && Array.getLength(value) < 1024)
			value = new String((byte[]) value).trim();
		responses.put(name,  value);
	}
	
	/**
	 * Returns the base working directory for this instantiation of the workflow.
	 * Individual components can create subdirectories inside it.
	 * @return
	 */
	public synchronized File getWorkDirectory() {
		return workDir;
	}

	public Object getWorkflowEngine() {
		return engine;
	}
	
	public IWorkflowMonitor getWorkflowMonitor() {
		return monitor;
	}

	public IResponseWriter openResponseWriter(WorkflowDefinition workflow) throws IOException {
		File sink = new File(workDir, "responses.csv");
		return new ResponsesOutWriter(sink, workflow, this);
	}

	public synchronized void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}
	
	public synchronized String getSampleId() {
		return sampleId;
	}

	static class Marker {
		NodeToken token;
		Object data;
		Marker(NodeToken token, Object data) {
			this.token = token;
			this.data = data;
		}
	}

	public synchronized void setWorkDirectory(File workDir) {
		this.workDir = workDir;		
	}
	
	public SAWWorkflowLogger log() {
		return log;
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		if (out != null)
			this.out = out;
	}

	public PrintWriter getErr() {
		return err;
	}

	public void setErr(PrintWriter err) {
		if (err != null)
			this.err = err;
	}
	
	@Override
	public void close() throws Exception {
		log.close();		
	}


	public synchronized File getHomeDir() {
		return homeDir;
	}

	public synchronized void setHomeDir(File homeDir) {
		this.homeDir = homeDir;
	}

	public synchronized void setenv(String key, String value) {
		envVars.put(key, value);
	}
	
	public synchronized String getenv(String key) {
		if (envVars.containsKey(key)) {
			return envVars.get(key);
		} else {
			return System.getenv(key);
		}
	}

	public synchronized Map<String, String> getenv() {
		return Collections.unmodifiableMap(envVars);
	}
	
	public Future<?> submit(Runnable r) {
		return executor.submit(r);
	}
	
	void awaitTermination() throws InterruptedException {
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.MINUTES);
	}

	void setWorkflowProcess(WorkflowProcess facade) {
		this.facade = facade;		
	}
	
	// TODO: A "CancellationHandler" would be nice?
	public boolean isCancelled() {
		return facade.isCancelled();
	}

	public void loadState() throws IOException {
		Persistor p = new Persistor(getWorkDirectory());
		p.loadState();
		parameters.putAll(p.getParameters());
		responses.putAll(p.getResponses());
		for (String name: p.getNodeNames()) {
			Map<String, Datum> nodeInputs = p.getInputs(name);
			for (String port: nodeInputs.keySet()) {
				Map<String, Datum> datums =  data.get(name);
				if (datums == null)
					data.put(name, datums = new HashMap<>());
				datums.put(port, nodeInputs.get(port));		
			}
		}
	}
	
	public void saveState() throws IOException {
		Persistor p = new Persistor(getWorkDirectory());
		p.getParameters().putAll(getParameters());
		p.getResponses().putAll(getResponses());
		for (String name: data.keySet()) {
			Map<String, Datum> datums = data.get(name);
			p.getInputs(name).putAll(datums);
			
		}
		p.saveState();
	}

	public void cancel() {
		facade.cancel();		
	}
		
	// Called by WorkflowProcess.cancel()
	void notifyCancelled() {
		for (ICancelationListener listener: listeners) {
			try {
				listener.workflowCanceled();
			} catch (Throwable t) {
				log().error("Error while canceling workflow", t);
			}
		}
	}
	
	public void addCancelationListener(ICancelationListener listener) {
		if (listener != null)
			listeners.add(listener);
	}
	
	public void removeCancelationListener(ICancelationListener listener) {
		if (listener != null)
			listeners.remove(listener);		
	}
	
	public File getWorkflowFile() {
		return facade.getWorkflowFile();
	}
	
	public String getUUID() {
		return facade.getUUID();
	}

	public static String getWFLIB() {
		String path = System.getenv("WFLIB"); 
		if (StringUtils.isEmpty(path)) {
			path = System.getProperty("user.home") + File.separator + "wflib" + File.separator;			
		}
		return path;
	}

	public void setParameter(String name, Object value) {
		parameters.put(name, value);
	}
	
	public Object getParameter(String name) {
		return parameters.get(name);
	}
	
	public Map<String, Object> getParameters() {
		return parameters;
	}
}
