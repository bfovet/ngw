/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.cubit;

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class CubitComponentNode extends SAWCustomNode {

	private static final String PYTHON_FILE = "pythonFile";
	private static final String JOURNAL_FILE = "journalFile";

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		File journalFile = getJournalFile(runtime, properties);
		File pythonFile = getPythonFile(runtime, properties);

		if ((journalFile == null || !journalFile.exists()) && (pythonFile == null || !pythonFile.exists()))
			throw new SAWWorkflowException("Cubit: no inputs available");

		if (journalFile != null && !journalFile.exists())
			throw new SAWWorkflowException("Cubit: journal file does not exist: " + journalFile.getAbsolutePath());

		if (pythonFile != null && !pythonFile.exists())
			throw new SAWWorkflowException("Cubit: python file does not exist: " + pythonFile.getAbsolutePath());		


		try {
			String journalContents = journalFile != null ? FileUtils.readFileToString(journalFile) : "";
			String pythonContents = pythonFile != null ? FileUtils.readFileToString(pythonFile) : "";

			File componentWorkDir = getComponentWorkDir(runtime, properties);
			File logFile = new File(componentWorkDir, getName() + ".log");
			CubitClaroxWorkflowJob job = new CubitClaroxWorkflowJob(journalContents, pythonContents, componentWorkDir, runtime.getenv());
			try {
				boolean result = job.run(runtime);
				if (!result)
					throw new SAWWorkflowException("Cubit: error during execution");
			} finally {
				FileUtils.write(logFile, job.getOutput());
			}
			return  Collections.singletonMap("logFile", logFile.getAbsolutePath());

		} catch (SAWWorkflowException e) {
			throw e;		
		} catch (Exception e) {
			throw new SAWWorkflowException("Cubit: error while preparing input", e);
		}
	}

	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.MESHING, "Engineering", NodeCategories.EXTERNAL_PROCESSES); }

	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo(JOURNAL_FILE, "input_file"), new InputPortInfo(PYTHON_FILE, "input_file")); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo("logFile", "output_file")); }
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo(JOURNAL_FILE, "home_file"), new PropertyInfo(PYTHON_FILE, "home_file")); }

	public File getJournalFile(RuntimeData data,  Map<String, String> properties) {
		return getFileFromPortOrProperty(data, properties, JOURNAL_FILE, false, true);
	}
	
	public File getPythonFile(RuntimeData data, Map<String, String> properties) {
		return getFileFromPortOrProperty(data, properties, PYTHON_FILE, false, true);
	}


}
