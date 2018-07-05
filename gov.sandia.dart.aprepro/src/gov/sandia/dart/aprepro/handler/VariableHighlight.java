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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

@Deprecated
public class VariableHighlight extends ActionDelegate implements
		IEditorActionDelegate {
	
	private IEditorPart targetEditor = null;

	/**
	 * @see ActionDelegate#run(IAction)
	 */
	public void run(IAction action) {

		IWorkbenchPartSite site = targetEditor.getSite();
		IWorkbenchPart part = site.getPart();  

		// this is done to ensure code is general and works with text editor and sierra editor
		if (!(part instanceof AbstractDecoratedTextEditor)) 
		{
			return;
		}
		
		AbstractDecoratedTextEditor textEditor = (AbstractDecoratedTextEditor) part;		
		
		//a very odd way to get the highlighted text, but this is the most general way without having
		//to typecast the editor to a SierraEditor or a TextEditor
		StyledText stext = (StyledText)textEditor.getAdapter(Control.class);
		Point selection = stext.getSelection();
		
		int startOffset = selection.x;
		int endOffset = selection.y;
		int length = endOffset - startOffset;
		
		//nothing is selected
		if(length == 0) 
		{
			return;
			//how to throw an error?
		}
		
		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		if(document == null)
		{
			return;
		}
				
		try {
			//get highlighted text
			String text = document.get(startOffset, length);			
			ApreproSubstitutor substitutor = getSubstitutor(action.getId(), text, document, startOffset, length);
			
			if(substitutor.showDialog() == Dialog.OK) {
				substitutor.substitute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}		
	
	
	private ApreproSubstitutor getSubstitutor(String id, String text, IDocument document, int startOffset, int length) throws Exception {
		if(id.equalsIgnoreCase("gov.sandia.dart.variableDefinition.variablize"))
			return new NewParameterSubstitutor(Display.getCurrent().getActiveShell(), document, text, startOffset, length);
		else if(id.equalsIgnoreCase("gov.sandia.dart.variableDefinition.use_existing"))
			return new ExistingParameterSubstitutor(Display.getCurrent().getActiveShell(), document, text, startOffset, length);
		else
			throw new Exception("Substitutor type not supported");
	}
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.targetEditor = targetEditor;
	}	
}
