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

import gov.sandia.dart.aprepro.handler.ApreproSubstitutor;
import gov.sandia.dart.aprepro.handler.NewParameterSubstitutor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;


//This class is used in an editor context menu.
public class createParameter extends ParameterHandler implements IHandler
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IDocument document = getDocument();

		if(document == null)
		{
			return null;
		}

		try {
			Point selection = getSelection();

			int startOffset = selection.x;
			int endOffset = selection.y;
			int length = endOffset - startOffset;
			
			
			String text = document.get(startOffset, length);
			ApreproSubstitutor substitutor = new JaguarNewParameterSubstitutor(document, text, startOffset, length);

			if (substitutor.showDialog() == Dialog.OK) {
				substitutor.substitute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	class JaguarNewParameterSubstitutor extends NewParameterSubstitutor {
		public JaguarNewParameterSubstitutor(IDocument document, String text, int startOffset, int length) throws BadLocationException {
			super(Display.getCurrent().getActiveShell(), document, text, startOffset, length);
		}
	}

}
