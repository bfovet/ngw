/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*
 * Created by mjgibso on Aug 27, 2012 at 6:43:09 AM
 */
package gov.sandia.dart.aprepro.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author mjgibso
 *
 */
public abstract class ParameterHandler extends AbstractHandler
{
	public static IDocument getDocument()
	{
		IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getSite().getPart();
		IDocument document = null;

		if(workbenchPart instanceof ITextEditor)
		{
			// Regular Text Editor
			ITextEditor editor = (ITextEditor) workbenchPart;
			IDocumentProvider provider = editor.getDocumentProvider();
			document = provider.getDocument(editor.getEditorInput());
		} else if(workbenchPart instanceof MultiPageEditorPart) {
			// MultipPagePart (ie Jaguar)
			FormEditor f = ((FormEditor) workbenchPart);
			IEditorPart e2 = f.getActiveEditor();
			if(e2 instanceof ITextEditor)
			{
				ITextEditor editor = (ITextEditor) e2;
				IDocumentProvider provider = editor.getDocumentProvider();
				document = provider.getDocument(editor.getEditorInput());
			}
		}
		
		return document;
	}
	
	protected Point getSelection()
	{
		// get highlighted text
		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		// this is done to ensure code is general and works with text editor and sierra editor
		// if (!(part instanceof EditorPart)) {
		// return false;
		// }

		// EditorPart textEditor = (EditorPart) site.g;

		// a very odd way to get the highlighted text, but this is the most general way without having
		// to typecast the editor to a SierraEditor or a TextEditor
		StyledText stext = (StyledText) editorPart.getAdapter(Control.class);
		return stext.getSelection();
	}
}
