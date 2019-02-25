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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Parameter;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Response;

public class ResponsesOutWriter implements IResponseWriter {

	private PrintWriter writer;
	private RuntimeData aRuntime;
	private WorkflowDefinition workflow;
	
	public ResponsesOutWriter(File sink, WorkflowDefinition workflow, RuntimeData runtime) throws IOException {
		writer = new PrintWriter(new FileWriter(sink), true);
		this.aRuntime = runtime;
		this.workflow = workflow;
		Map<String, Response> responses = workflow.getResponses();
		Map<String, Parameter> parameters = workflow.getParameters();

		writer.print("Sample, ");
		for (String parameter: parameters.keySet()) {
			if (!RuntimeData.isBuiltIn(parameter)) {
				writer.print(parameter);
				writer.print(", ");
			}
		}
		for (Response response: responses.values()) {
			writer.print(response.name);
			writer.print(", ");
		}
		writer.println();
	}
	
	
	@Override
	public synchronized void writeRow() {
		Map<String, Response> responses = workflow.getResponses();
		Map<String, Object> responseValues = aRuntime.getResponses();

		Map<String, Parameter> parameters = workflow.getParameters();
		Map<String, Object> parameterValues = aRuntime.getParameters();

		writer.print(aRuntime.getSampleId());
		
		for (String parameter: parameters.keySet()) {
			if (!RuntimeData.isBuiltIn(parameter)) {

				writer.print(", ");
				writer.print(parameterValues.get(parameter));
			}
		}
		
		for (Response response: responses.values()) {
			writer.print(", ");
			writer.print(String.valueOf(responseValues.get(response.name)).trim());
		}
		writer.println();

	}
	@Override
	public synchronized void writeRow(RuntimeData runtime) {
		Map<String, Response> responses = workflow.getResponses();
		Map<String, Object> responseValues = runtime.getResponses();

		Map<String, Parameter> parameters = workflow.getParameters();
		Map<String, Object> parameterValues = runtime.getParameters();

		writer.print(runtime.getSampleId());
		
		for (String parameter: parameters.keySet()) {
			if (!RuntimeData.isBuiltIn(parameter)) {
				writer.print(", ");
				writer.print(parameterValues.get(parameter));
			}
		}
				
		for (Response response: responses.values()) {
			writer.print(", ");
			writer.print(String.valueOf(responseValues.get(response.name)).trim());
		}
		writer.println();

	}

	@Override
	public synchronized void close() {
		writer.close();
	}
	
	public static Object format(Object object) {
		if (object == null)
			return "null";		
		Datum datum = new Datum("text", object, object.getClass());
		return datum.getAs(String.class);
	}
}
