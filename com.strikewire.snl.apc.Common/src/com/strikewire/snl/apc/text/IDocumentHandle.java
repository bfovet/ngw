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
 * Created by mjgibso on Nov 21, 2013 at 6:57:00 AM
 */
package com.strikewire.snl.apc.text;

import org.eclipse.jface.text.IDocument;

/**
 * @author mjgibso
 *
 */
public interface IDocumentHandle
{
	public IDocument document();
	
	public boolean dispose();
}
