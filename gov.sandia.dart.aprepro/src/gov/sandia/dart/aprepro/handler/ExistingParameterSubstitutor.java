/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.aprepro.handler;

import gov.sandia.dart.aprepro.ui.ApreproVariableData;
import gov.sandia.dart.aprepro.ui.VariableUsageDialog;
import gov.sandia.dart.aprepro.util.ApreproUtil;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ExistingParameterSubstitutor extends ApreproSubstitutor {

	private VariableUsageDialog dialog;
	
	public ExistingParameterSubstitutor(Shell shell, IDocument document, String text, int startOffset, int length) throws BadLocationException {
		super(document, startOffset, length);
		dialog = new VariableUsageDialog(shell, list);
	}

	@Override
	public void substitute() throws BadLocationException {
		if(list.size() == 0) 
		{
			return;
		}
		
		Object[] selection = dialog.getResult();
		if(selection == null) return;			//nothing selected
		if(selection.length != 1) return; 		//shouldn't select more than 1
		
		ApreproVariableData data = (ApreproVariableData)selection[0];
		document.replace(startOffset, length, ApreproUtil.constructApreproString(data.getKey()));
		
		commentChar = ApreproUtil.getCommentCharacter();
//		checkForConsistency();
	}
	
	@Override
	public int showDialog() {
		if(list.size() == 0)
		{
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "No variables", "There are no variables defined.");
			return 0;
		} 
		else 
		{
			return dialog.open();
		}
	}		
}	
