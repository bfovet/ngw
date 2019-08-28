/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded;

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class AskYesNoNode extends SAWCustomNode {

	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
				
		String[] name = { "no" };
		Object arg1 = null;
		try {
			arg1 = runtime.getInput(getName(), "x", Object.class);

			if (arg1 == null)
				arg1 = "0";
			if (!runtime.isCancelled()) {
				String question = properties.get("question");
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell(),
								"Question", null, question, MessageDialog.QUESTION_WITH_CANCEL,
										new String[] {IDialogConstants.YES_LABEL,
		                    				IDialogConstants.NO_LABEL,
		                    				IDialogConstants.CANCEL_LABEL},
		                    				0) {
							@Override
							public int open() {
								setShellStyle(SWT.SHEET);
								return super.open();
							}
							
						};
						int result = dialog.open();
						
						name[0] = result == 0 ? "yes" : "no";
						if (result == 2)
							runtime.cancel();
					}			
				}); 
			}
									
		} catch (Exception e) {
			throw new SAWWorkflowException("Error executing node", e);
		}

		// There are two outputs, "yes" and "no". This will only send a
		// token to the one that matches the answer to the question
		return Collections.singletonMap(name[0], arg1);			
	}
	
	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo("x")); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo("yes"), new OutputPortInfo("no")); }
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo("question")); }
//	@Override public List<String> getDefaultProperties() { return Arrays.asList("question"); }
	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.CONTROL, NodeCategories.UI); }
	
}
