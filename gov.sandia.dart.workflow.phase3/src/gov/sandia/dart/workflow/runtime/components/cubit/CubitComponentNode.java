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

	@Override
	public String getCategory() {
		return "Engineering";
	}

	@Override public List<String> getDefaultInputNames() { return Arrays.asList(JOURNAL_FILE, PYTHON_FILE); }
	@Override public List<String> getDefaultInputTypes() { return Arrays.asList("input_file", "input_file"); }
	@Override public List<String> getDefaultOutputNames() { return Arrays.asList("logFile"); }
	@Override public List<String> getDefaultOutputTypes() { return Arrays.asList("output_file"); }
	@Override public List<String> getDefaultProperties() { return Arrays.asList(JOURNAL_FILE, PYTHON_FILE); }
	@Override public List<String> getDefaultPropertyTypes() { return Arrays.asList("home_file", "home_file"); }	

	public File getJournalFile(RuntimeData data,  Map<String, String> properties) {
		return getFileFromPortOrProperty(data, properties, JOURNAL_FILE, false, true);
	}
	
	public File getPythonFile(RuntimeData data, Map<String, String> properties) {
		return getFileFromPortOrProperty(data, properties, PYTHON_FILE, false, true);
	}


}
