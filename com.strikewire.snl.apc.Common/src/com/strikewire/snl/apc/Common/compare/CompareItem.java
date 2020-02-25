/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.Common.compare;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

/**
 * Used to store the contents and other data about a file that is being
 * compared with another.
 */
public class CompareItem implements IStreamContentAccessor, ITypedElement, IModificationDate 
{
	private String contents, name;
	URI fileURI;
	private long time;
	
	public CompareItem(String name, String contents, URI uri, long time) 
	{
		this.name = name;
		this.contents = contents;
		this.time = time;
		fileURI = uri;
	}
	
	public InputStream getContents() throws CoreException 
	{
		return new ByteArrayInputStream( contents.getBytes() );
	}
	
	public Image getImage() {return null;}
	public long getModificationDate() {return time;}
	public String getName() {return name;}
	public String getString() {return contents;}
	public String getType() {return "i";}
	public URI getFileURI() {return fileURI;}
	
}  //  CompareItem
