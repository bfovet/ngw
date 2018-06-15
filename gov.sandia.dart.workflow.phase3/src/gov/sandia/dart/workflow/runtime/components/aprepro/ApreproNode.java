/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.aprepro;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Parameter;

public class ApreproNode extends SAWCustomNode {

	private static final String TEMPLATE_FILE = "templateFile";

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		
		File templateFile = getFileFromPortOrProperty(runtime,  properties, TEMPLATE_FILE, true, false);

		File componentWorkDir = getComponentWorkDir(runtime, properties);
		
		File outputFile = new File(componentWorkDir, getOutputFileName(properties, templateFile));
		
		String commentChar = getCommentChar(properties);
		
		try {
			Map<String, String> parameters = getParameters(workflow, runtime);
			File apreproParamsFile = ApreproUtil.createApreproParamsFile(new File(componentWorkDir, "aprepro.in"), commentChar, parameters);
			
			int result = ApreproUtil.doTransform(apreproParamsFile, templateFile, outputFile, componentWorkDir, commentChar, runtime);
			if (result != 0) {
				throw new SAWWorkflowException(getName() + " : error running aprepro");
			}
		} catch (IOException | InterruptedException e) {
			throw new SAWWorkflowException(getName() + " : error running aprepro", e);
		}
		return Collections.singletonMap("outputFile", outputFile.getAbsolutePath());
	}

	public Map<String, String> getParameters(WorkflowDefinition workflow, RuntimeData runtime) {
		Map<String, String> parameters = new HashMap<>();
		List<String> defaults = getDefaultInputNames();
		for (String name: runtime.getInputNames(getName())) {
			if (!defaults.contains(name)) {
				String value = (String) runtime.getInput(getName(), name, String.class);
				parameters.put(name, value);
			}
		}
		for (Parameter p: workflow.getParameters().values()) {
			if (p.global) {
				String value = String.valueOf(runtime.getParameter(p.name));
				parameters.put(p.name, value);
			}
		}
		return parameters;		
	}

	private String getOutputFileName(Map<String, String> properties, File templateFile) {
		String outputFileName = properties.get("outputFile");
		if (!StringUtils.isEmpty(outputFileName))
			return outputFileName;
		else {
			String base = FilenameUtils.getBaseName(templateFile.getName());
			String extension = FilenameUtils.getExtension(templateFile.getName());
			return base + ".aprepro." + extension;
		}
	}

	@Override public List<String> getDefaultInputNames() { return Collections.singletonList(TEMPLATE_FILE); }	
	@Override public List<String> getDefaultInputTypes() { return Collections.singletonList("input_file"); }	
	@Override public List<String> getDefaultOutputNames() { return Collections.singletonList("outputFile"); }	
	@Override public List<String> getDefaultOutputTypes() { return Collections.singletonList("output_file"); }	
	@Override public List<String> getDefaultProperties() { return Arrays.asList(TEMPLATE_FILE, "outputFile", "commentChar"); }	
	@Override public List<String> getDefaultPropertyTypes() { return Arrays.asList("home_file", "default", "default"); }
		
	public String getCommentChar(Map<String, String> properties) {
		String raw = properties.get("commentChar");
		if (StringUtils.isEmpty(raw))
			return "#";
		else
			return raw;
	}
	
	@Override
	public String getCategory() {
		return "Engineering";
	}
}
