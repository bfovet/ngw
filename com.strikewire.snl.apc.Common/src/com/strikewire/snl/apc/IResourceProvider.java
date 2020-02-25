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
 * Create on Mar 23, 2012 at 2:33:23 PM by mjgibso
 */
package com.strikewire.snl.apc;

import java.util.Collection;

import org.eclipse.core.resources.IResource;

/**
 * @author mjgibso
 *
 */
public interface IResourceProvider
{
	public Collection<IResource> getResources();
}
