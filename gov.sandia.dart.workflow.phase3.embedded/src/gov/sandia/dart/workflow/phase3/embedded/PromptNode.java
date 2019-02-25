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
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class PromptNode extends SAWCustomNode {

	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
				
		String[] result =  { "" };
		try {

			String question = properties.get("question");
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell(), "Question", question, "", null);
					if (dialog.open() == InputDialog.OK)
						result[0] = dialog.getValue();					
				}			
			});
									
		} catch (Exception e) {
			throw new SAWWorkflowException("Error executing node", e);
		}

		return Collections.singletonMap("f", result[0]);			
	}
	
	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo("x")); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo("f")); }
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo("question")); }
	@Override public String getCategory() { return "Control"; }
	
}
