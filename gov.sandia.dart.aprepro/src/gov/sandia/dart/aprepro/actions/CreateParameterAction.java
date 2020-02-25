/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.aprepro.actions;

import gov.sandia.dart.aprepro.ApreproPlugin;
import gov.sandia.dart.aprepro.util.ApreproUtil;

import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

// This class is used to create popup menus in, e.g., a cubit pickwidget or an AST editor
public class CreateParameterAction extends Action {

	IParameterUpdater updater_;
	IParameterSource source_;
	
	public static final String CREATE_PARAMETER_ICON = "icons/tag_blue_add.png";
	
	public CreateParameterAction(IParameterUpdater updater, IParameterSource source){
		super("Create a parameter", ApreproPlugin.imageDescriptorFromPlugin("gov.sandia.dart.aprepro", CREATE_PARAMETER_ICON));
		updater_ = updater;
		source_ = source;
	}
	
	@Override
	public void run() {
		
		updater_.initialize();
		String value = updater_.getSelectedText();				
		
		final Map<String, String> existingParameters = source_.getParameters();		
		
		String baseName = "newParameter";
		int count = 0;
		boolean hasMatch = false;
		
		String proposedName;

		if(existingParameters == null){
			proposedName = baseName;
		}else{

			do{
				hasMatch = false;
				if(count > 0){
					proposedName = baseName + count; 
				}else{
					proposedName = baseName;
				}
				
				if(existingParameters.containsKey(proposedName)){
					count++;
					hasMatch = true;
				}
			} while(hasMatch);					
		}		
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		InputDialog dialog = new InputDialog(shell, "Create New Parameter", "Select a name for a new parameter with the value '" + value + "'", proposedName, new IInputValidator() {
			
			@Override
			public String isValid(String newText) {
				if(existingParameters.containsKey(newText)){
					return "Parameter with name '" + newText + "' already exists.";
				}
				return null;
			}
		});
		
		if(dialog.open() != Dialog.OK){
			return;
		}

		String name = dialog.getValue();

		updater_.setSelectedText(ApreproUtil.constructApreproString(name));
		
		if(!source_.createParameter(name,  value)){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "No resource", "Unable to create parameter.");

			return;
		}

		
		return;
	}	
}
