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
 * Created by mjgibso on Oct 23, 2013 at 9:29:48 PM
 */
package gov.sandia.dart.application;

/**
 * @see IDARTApplicationListener
 * 
 * @author mjgibso
 */
public abstract class DARTApplicationAdapter implements IDARTApplicationListener
{
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.application.IDARTApplicationListener#preApplicationEvent(com.strikewire.snl.apc.application.DARTApplicationEvent)
	 */
	@Override
	public void preApplicationEvent(DARTApplicationEvent event)
	{
	}

	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.application.IDARTApplicationListener#postApplicationEvent(com.strikewire.snl.apc.application.DARTApplicationEvent)
	 */
	@Override
	public void postApplicationEvent(DARTApplicationEvent event)
	{
	}
}
