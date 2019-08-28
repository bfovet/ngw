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
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.components.AbstractRestartableNode;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.NodeMemento;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class ApreproNode extends AbstractRestartableNode {

	private static final String INPUT_PARAMETERS_MAP = "inputParametersMap";
	private static final String TEMPLATE_FILE = "templateFile";

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		File componentWorkDir = getComponentWorkDir(runtime, properties);
		clearMemento(componentWorkDir);
		NodeMemento memento = createMemento(properties, workflow, runtime);
		File templateFile = getFileFromPortOrProperty(runtime,  properties, TEMPLATE_FILE, true, false);
		
		File outputFile = new File(componentWorkDir, getOutputFileName(properties, templateFile));
		
		String commentChar = getCommentChar(properties);
		
		try {
			Map<String, String> parameters = getParameters(workflow, runtime);
			File apreproParamsFile = ApreproUtil.createApreproParamsFile(new File(componentWorkDir, "aprepro.in"), commentChar, parameters);
			
			int result = ApreproUtil.doTransform(apreproParamsFile, templateFile, outputFile, componentWorkDir, commentChar, runtime);
			if (result != 0) {
				throw new SAWWorkflowException(getName() + " : error running aprepro");
			}
			Map<String, Object> outputs = Collections.singletonMap("outputFile", outputFile.getAbsolutePath());
			try {
				addOutputsToMemento(memento, outputs).save(componentWorkDir);
			} catch (IOException e) {
				// No memento, no problem
			}
			return outputs;
			
		} catch (IOException | InterruptedException e) {
			throw new SAWWorkflowException(getName() + " : error running aprepro", e);
		}
		
	}

	//
	// potential sources of parameters for aprepro:
	//   (1) a map fed into the INPUT_PARAMETERS_MAP port,
	//   (2) the name and value of any other connected ports,
	//   (3) global parameters of the workflow
	//
	public Map<String, String> getParameters(WorkflowDefinition workflow, RuntimeData runtime) {
		Map<String, String> parameters = new HashMap<>();
		if (isConnectedInput(INPUT_PARAMETERS_MAP, workflow)) {
			Map<?, ?> map = (Map<?, ?>) runtime.getInput(getName(), INPUT_PARAMETERS_MAP, Map.class);
			if (map != null) {
				for (Map.Entry<?, ?> entry: map.entrySet()) {
					parameters.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
				}
			}
		}
		
		List<InputPortInfo> defaults = getDefaultInputs();
		for (String name: runtime.getInputNames(getName())) {
			if (!propertiesContains(defaults, name)) {
				String value = (String) runtime.getInput(getName(), name, String.class);
				parameters.put(name, value);
			}
		}
		
		for (String parameterName: runtime.getParameterNames()) {
			if (runtime.isGlobal(parameterName)) {
				String value = String.valueOf(runtime.getParameter(parameterName).getValue());
				parameters.put(parameterName, value);
			}
		}
		return parameters.entrySet().stream().
				filter(e -> e.getKey().indexOf('.') == -1).
				collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));	
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

	@Override public List<InputPortInfo> getDefaultInputs() {
		return Arrays.asList(new InputPortInfo(TEMPLATE_FILE, "input_file"), new InputPortInfo(INPUT_PARAMETERS_MAP, "map"));
		}	
	@Override public List<OutputPortInfo> getDefaultOutputs() {
		return Collections.singletonList(new OutputPortInfo("outputFile", "output_file"));
		}	
	@Override public List<PropertyInfo> getDefaultProperties() {
		return Arrays.asList(
			new PropertyInfo(TEMPLATE_FILE, "home_file"),
			new PropertyInfo("outputFile", "default"),
			new PropertyInfo("commentChar", "default"),
			new PropertyInfo(PRIVATE_WORK_DIR, "boolean", "true")
		);
	}	
		
	public String getCommentChar(Map<String, String> properties) {
		String raw = properties.get("commentChar");
		if (StringUtils.isEmpty(raw))
			return "#";
		else
			return raw;
	}
	
	@Override
	public List<String> getCategories() {
		return Arrays.asList(NodeCategories.TEXT_DATA, NodeCategories.EXTERNAL_PROCESSES);
	}
	
	@Override
	protected boolean outputsAvailable(WorkflowDefinition workflow, RuntimeData runtime,
			Map<String, String> properties) {
		File componentWorkDir = getComponentWorkDir(runtime, properties);
		File templateFile = getFileFromPortOrProperty(runtime,  properties, TEMPLATE_FILE, true, false);		
		File outputFile = new File(componentWorkDir, getOutputFileName(properties, templateFile));
		return outputFile.exists();
	}
}
