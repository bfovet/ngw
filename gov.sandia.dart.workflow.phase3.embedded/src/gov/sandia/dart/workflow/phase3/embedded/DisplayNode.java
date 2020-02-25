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

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class DisplayNode extends SAWCustomNode {
	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		String arg1 = (String) runtime.getInput(getName(), DEFAULT_INPUT, String.class);
		if (arg1 == null)
			arg1 = "0";

		String result = arg1.trim();
		if (StringUtils.isNotEmpty(getFormatString(properties))) {
			result = String.format(getFormatString(properties), result);
		}
		
		if (!runtime.isCancelled()) {
			String finalResult = result;
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell(),
							getName(), null, finalResult, MessageDialog.CONFIRM,
									new String[] {IDialogConstants.OK_LABEL,
	                    				IDialogConstants.CANCEL_LABEL},
	                    				0) {
						@Override
						public int open() {
							setShellStyle(SWT.SHEET);
							return super.open();
						}
						
					};
					int result = dialog.open();
					
					if (result != 0) {
						runtime.cancel();
					}
				}
			});
		}
		return Collections.singletonMap("f", result);
	}

	public String getFormatString(Map<String, String> properties) {
		return properties.get("formatString");
	}

	@Override public List<OutputPortInfo> getDefaultOutputs() { return Collections.singletonList(new OutputPortInfo("f")); }
	@Override public List<InputPortInfo> getDefaultInputs() { return Collections.singletonList(new InputPortInfo("x")); }
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo("formatString")); }
	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.UI); }



}
