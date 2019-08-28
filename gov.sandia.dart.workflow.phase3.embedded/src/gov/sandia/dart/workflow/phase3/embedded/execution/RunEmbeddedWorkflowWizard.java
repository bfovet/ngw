/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.execution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import gov.sandia.dart.workflow.editor.WorkflowDiagramEditor;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFNode;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFObject;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFProperty;
import gov.sandia.dart.workflow.runtime.parser.IWFParser;

public class RunEmbeddedWorkflowWizard extends Wizard {
	
    public static final String EMBEDDED_PARAMS_FILE = "workflow.parameters.properties";

	private RuntimeDirectoryPage page;
	private EmbeddedWorkflowParametersPage paramsPage;
	private IFile workflowFile;
	private File runLocation;
	
	public static class Parameter {
		public final String name;
		public final String type;
		public String value; // TODO: Are there actually non-String parameters?
		
		public Parameter(String name, String type, String value) {
			this.name = name;
			this.type = type;
			this.value = value;
		}
	}
	
	public List<Parameter> embeddedParameters = new ArrayList<>();

	private boolean clearWorkdir;
	
	public void setParametersFromWorkflow() {
		embeddedParameters.clear();
		IWFParser parser = new IWFParser();
		File iwfFile = workflowFile.getLocation().toFile();
		List<IWFObject> objects = parser.parse(iwfFile);
		for (IWFObject o : objects) {
			if (o instanceof IWFNode) {
				IWFNode iwfNode = (IWFNode) o;
				if (iwfNode.type.equals("parameter")) {
					for (IWFProperty p: iwfNode.properties) {
						if ("value".equals(p.name)) {
							// System.err.println("PARAMETER " + iwfNode.name + " value " + p.value);
							embeddedParameters.add(new Parameter(iwfNode.name, p.type, p.value));
						}
					}
				}
			}
		}
	}
		
	public void updateParametersFromFile(File paramPropsFile) {
		if (paramPropsFile != null && paramPropsFile.exists()) {
			Properties parametersFromFile = new Properties();
			try (InputStream paramPropsStream = new FileInputStream(paramPropsFile)) {
				parametersFromFile.load(paramPropsStream);
				Set<Object> paramNamesFromFile = parametersFromFile.keySet();
				embeddedParameters.stream().filter(p -> paramNamesFromFile.contains(p.name))
                	.forEach(p -> p.value = parametersFromFile.getProperty(p.name));
			} catch (IOException e) {
				WorkflowEditorPlugin.getDefault().logError("Problem reading parameters file", e);
			}
		}
	}
	
	public RunEmbeddedWorkflowWizard(IFile workflowFile, File runLocation) {
		this.workflowFile = workflowFile;
		this.runLocation = runLocation;
		setParametersFromWorkflow();
	}

	@Override
	public void addPages() {
		IPath defaultPath;
		if (runLocation != null)
			defaultPath = new Path(runLocation.getAbsolutePath());
		else
			defaultPath = new Path(workflowFile.getParent().getLocation().toFile().getAbsolutePath());
		page = new RuntimeDirectoryPage(this, workflowFile, defaultPath);
		addPage(page);
		page.setDescription("Choose Top-Level Runtime Directory");
		page.setTitle("Choose Directory");
		page.setImageDescriptor(WorkflowEditorPlugin.getImageDescriptor("icons/shapes64.gif"));
		paramsPage = new EmbeddedWorkflowParametersPage(embeddedParameters);
		addPage(paramsPage);
	}

	@Override
	public String getWindowTitle() {
		return "Run Embedded Workflow";
	}
	
	@Override
	public boolean canFinish() {
		return page.isPageComplete();
	}
	
	private WorkflowDiagramEditor getOpenEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = page.getActiveEditor();
	
		if(!(editor instanceof WorkflowDiagramEditor)) {
			return null;
		}
		
		return (WorkflowDiagramEditor) editor;
	}
	
	@Override
	public boolean performFinish() {
		IPath path = page.getPath();
		File workdir = path.toFile();
		if (!workdir.exists())
			if (!workdir.mkdirs()) {
				MessageBox diag = new MessageBox(page.getShell(), SWT.ICON_ERROR | SWT.OK);
				diag.setMessage("Unable to create workdir " + workdir);
				diag.open();
				return false;
			}
		if (clearWorkdir) 
			if (workflowFile.getParent().getLocation().toFile().getAbsoluteFile().equals(workdir.getAbsoluteFile())) {
				MessageBox diag = new MessageBox(page.getShell(), SWT.ICON_ERROR | SWT.OK);
				diag.setMessage("Can't clear directory containing workflow file");
				diag.open();
				return false;
			} else {
				try {
					FileUtils.cleanDirectory(workdir);
				} catch (IOException e) {
					MessageBox diag = new MessageBox(page.getShell(), SWT.ICON_ERROR | SWT.OK);
					diag.setMessage("Error clearing workdir: " + e.getMessage());
					diag.open();
					return false;
				}
			}	
		
		File workflowParametersFile = new File(path.toFile(), EMBEDDED_PARAMS_FILE);
		if (workflowParametersFile != null) {
			Properties parametersToWrite = new Properties();
			embeddedParameters.forEach(p -> parametersToWrite.setProperty(p.name, p.value));
			try {
				parametersToWrite.store(new FileOutputStream(workflowParametersFile), "parameters for " + workflowFile.getName());
			} catch (IOException e) {
				MessageBox diag = new MessageBox(page.getShell(), SWT.ICON_ERROR | SWT.OK);
				diag.setMessage("Error writing parameters file: " + e.getMessage());
				diag.open();
				return false;
			}
		}
		
		WorkflowDiagramEditor editor = getOpenEditor();
		if (editor != null) {
			// TODO This isn't necessarily right -- the editor might be for some other workflow file.
			editor.setRunLocation(path.toFile());
		}
		Job job = new EmbeddedWorkflowJob("Workflow " + workflowFile.getName(), workflowFile, path, workflowParametersFile);
		job.schedule();
		return true;
	}

	public void setClearWorkdir(boolean selection) {
		clearWorkdir = selection;		
	}
	
	public boolean getClearWorkdir() {
		return clearWorkdir;		
	}
	
}
