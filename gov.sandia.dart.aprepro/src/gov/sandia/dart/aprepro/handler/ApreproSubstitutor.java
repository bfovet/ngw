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
import gov.sandia.dart.aprepro.util.ApreproUtil;

import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public abstract class ApreproSubstitutor {
	
	protected IDocument document;
	protected Map<String, ApreproVariableData> list;
	protected int startOffset, length;
	
	protected String commentChar;
	
	public ApreproSubstitutor(IDocument document, int startOffset, int length) throws BadLocationException {
		this.document = document;
		this.startOffset = startOffset;
		this.length = length;
		
		list = ApreproUtil.buildApreproVariableList(document);
	}
	
	public abstract void substitute() throws BadLocationException;
	
	public abstract int showDialog();
	
	public int sizeOf() {
		return list==null ? 0 : list.size();
	}
		
}
