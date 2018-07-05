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
 * Created by mjgibso on Feb 17, 2014 at 5:53:34 AM
 */
package com.strikewire.snl.apc.properties;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author mjgibso
 *
 */
public interface INotifyingPropertySource extends IPropertySource
{

	public void setPropertyChangeListener(IPropertyChangeListener listener);
	
}
