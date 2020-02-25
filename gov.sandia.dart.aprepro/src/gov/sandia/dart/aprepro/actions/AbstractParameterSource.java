/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/**
 * 
 */
package gov.sandia.dart.aprepro.actions;

import gov.sandia.dart.aprepro.actions.ParameterEvent.Type;

import org.eclipse.core.resources.IFile;



/**
 * @author mjgibso
 *
 */
public abstract class AbstractParameterSource implements IParameterSource
{
	private final IFile _file;
	
	protected AbstractParameterSource(IFile file)
	{
		this._file = file;
	}
	
	/* (non-Javadoc)
	 * @see gov.sandia.dart.aprepro.actions.IParameterSource#createParameter(java.lang.String, java.lang.String)
	 */
	@Override
	public final boolean createParameter(String name, String value)
	{
		boolean done = doCreateParameter(name, value);
		
		ParameterEvent event = new ParameterEvent(Type.PARAMETER_CREATED, name, value, this._file);
		ParameterEventHandler.getInstance().notifyListeners(event);
		
		return done;
	}
	
	protected abstract boolean doCreateParameter(String name, String value);
}
