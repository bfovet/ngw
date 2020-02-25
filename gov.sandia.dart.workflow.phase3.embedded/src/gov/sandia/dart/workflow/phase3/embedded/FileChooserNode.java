/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class FileChooserNode extends SAWCustomNode {
	static final String CANCELED = "canceled";
	static final String TITLE = "title";
	static final String FILE_FILTERS = "fileFilters";
	static final String INITIAL_PATH = "initialPath";
	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		File[] result =  new File[1];
		
		try {
			String title = properties.get(TITLE);
			String[] fileFilters = getFileFilters(properties);
			String rawPath = properties.get(INITIAL_PATH);
			String initialPath;
			if (!StringUtils.isEmpty(rawPath)) {
				File file = new File(rawPath);
				if (!file.isAbsolute())
					file = new File(runtime.getHomeDir(), rawPath);
				initialPath = file.getAbsolutePath();
			} else {
				initialPath = runtime.getHomeDir().getAbsolutePath();
			}
			
			try {
				String storedPath = FileUtils.readFileToString(getStateFile(runtime, properties), Charset.defaultCharset());
				File file = new File(storedPath);
				if (file.exists())
					initialPath = storedPath;
			} catch (Exception e) {
				// Ignore, not a fatal error
			}			
			String finalInitialPath = initialPath;
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					File chosenFile = browseForFile(title, finalInitialPath, fileFilters);
					
					if (chosenFile == null)
						throw new SAWWorkflowException(getName() + ": " + " user cancelled file selection"); 
		
					result[0] = chosenFile;
				}

			});
									
		} catch (Exception e) {
			throw new SAWWorkflowException("Error executing node", e);
		}
		
		if (result[0] == null) {		
			if (isConnectedOutput(CANCELED, workflow)) {
				return Collections.singletonMap(CANCELED, CANCELED);	
			} else {
				throw new SAWWorkflowException(getName() + ": user cancelled file selection");
			}
		} else {
			try {
				FileUtils.write(getStateFile(runtime, properties), result[0].getAbsolutePath(), Charset.defaultCharset());
			} catch (IOException e) {
				// Ignore, not a fatal error
			}
			runtime.log().info("File {0} was selected via FileChooserNode {1}", result[0].getAbsolutePath(), getName());
			return Collections.singletonMap("f", result[0].getAbsolutePath());
		}
	}

	private File getStateFile(RuntimeData runtime, Map<String, String> properties) {
		File workDir = getComponentWorkDir(runtime, properties);
		return new File(workDir, getName() + "_fcs.txt");
	}

	private String[] getFileFilters(Map<String, String> properties) {
		String rawFilters = properties.get(FILE_FILTERS);
		if (StringUtils.isBlank(rawFilters))
			return null;
		return rawFilters.split("[\\s,;]+");
	}			

	private File browseForFile(String title, String initialPath, String[] fileFilters) {
		Shell shell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN | SWT.SINGLE);
		if(title != null)
		{
			fileDialog.setText(title);
		}

		if(initialPath != null)
		{
			File file = new File(initialPath);
			if (file.exists()) {
				if (file.isDirectory()) {
					fileDialog.setFilterPath(initialPath);
				} else {
					fileDialog.setFilterPath(file.getParent());
					fileDialog.setFileName(initialPath);
				}
			}
		}
		
		if (fileFilters != null) {
			fileDialog.setFilterExtensions(fileFilters);
		}
		
		String retPath = fileDialog.open();
		
		return retPath == null ? null : new File(retPath);
	}
	
	@Override public List<OutputPortInfo> getDefaultOutputs() {
		return Arrays.asList(new OutputPortInfo("f"), new OutputPortInfo(CANCELED)); }
	@Override public List<InputPortInfo> getDefaultInputs() { return Collections.singletonList(new InputPortInfo("x")); }
	@Override public List<PropertyInfo> getDefaultProperties() {
		return Arrays.asList(
				new PropertyInfo(INITIAL_PATH),
				new PropertyInfo(TITLE),
				new PropertyInfo(FILE_FILTERS)
		);
	}
	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.UI); }



}
