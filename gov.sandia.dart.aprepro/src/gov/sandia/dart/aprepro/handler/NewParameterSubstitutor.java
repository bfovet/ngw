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

import gov.sandia.dart.aprepro.ui.VariablePickerDialog;
import gov.sandia.dart.aprepro.util.ApreproUtil;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.undo.DocumentUndoManagerRegistry;
import org.eclipse.text.undo.IDocumentUndoManager;

public class NewParameterSubstitutor extends ApreproSubstitutor {

	private VariablePickerDialog dialog;
			
	public NewParameterSubstitutor(Shell shell, IDocument document, String text, int startOffset, int length) throws BadLocationException {
		super(document, startOffset, length);
		
		dialog = new VariablePickerDialog(shell, text, list);		
	}

	@Override 
	public void substitute() throws BadLocationException
	{
		commentChar = dialog.getCommentText();
		IDocumentUndoManager manager = DocumentUndoManagerRegistry.getDocumentUndoManager(document);
		if (manager != null)
			manager.beginCompoundChange();
		try {
			ApreproUtil.setCommentCharacter(commentChar);
			document.replace(startOffset, length, ApreproUtil.constructApreproString(dialog.getVariableName()));
			document.replace(ApreproUtil.findApreproDefinitionInsertionOffset(document), 0, commentChar + ApreproUtil.constructApreproString(dialog.getVariableName(), dialog.getVariableValue()) + "\n");
		} finally {
			if (manager != null)
				manager.endCompoundChange();
		}
	}

	@Override
	public int showDialog() {
		return dialog.open();
	}		
}
