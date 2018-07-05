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
import gov.sandia.dart.aprepro.ui.ApreproVariableData;
import gov.sandia.dart.aprepro.util.ApreproUtil;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public class DocumentParameterSource extends AbstractParameterSource implements IParameterSource
{
	IDocument document_;
	
	public DocumentParameterSource(IDocument document, IFile file)
	{
		super(file);
		
		document_ = document;
	}
	
	@Override
	public Map<String, String> getParameters() {
		HashMap<String, String> parameters = new HashMap<String, String>();
		
		if(document_ != null){
			try {
				
				Map<String, ApreproVariableData> allVariables = ApreproUtil.buildApreproVariableList(document_);
				
				for(ApreproVariableData variable: allVariables.values()){
					if(variable != null){
						parameters.put(variable.getKey(), variable.getValue());
					}
				}
				

				return parameters;
			} catch (Exception e) {
				ApreproPlugin.getDefault().logError("Error processing resource", e);
			}
		}
		return null;
	}
	
	@Override
	protected boolean doCreateParameter(String name, String value)
	{
		if(document_ == null){
			return false;			
		}

		try{
			String variableString = ApreproUtil.getCommentCharacter() + ApreproUtil.constructApreproString(name, value) + "\n";
			document_.replace(ApreproUtil.findApreproDefinitionInsertionOffset(document_), 0, variableString);
		} catch (BadLocationException e) {
			ApreproPlugin.getDefault().logError("Error adding parameter", e);
		}
		
		return true;
		
	}

	@Override
	public boolean allowCreate() {
		// if we don't have a document so we can't create variables.
		return document_ != null;
	}

}
