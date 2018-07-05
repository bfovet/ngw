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


import java.io.File;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.util.ResourceUtils;

/**
 * This class is used to compare two CompareItem instances.
 * Both setLeftItem() and setRightItem() are expected
 * to be called before prepareInput() is called.
 */
public class CompareInput extends CompareEditorInput
{
	private CompareItem leftItem = null;
	private CompareItem rightItem = null;
	
	public CompareInput() 
	{
		super( new CompareConfiguration() );
	}
	
	public void setLeftItem(CompareItem left)
	{
		leftItem = left;
	}
	
	public void setRightItem(CompareItem right)
	{
		rightItem = right;
	}
	
	@Override
	public void setTitle(String title)
	{
		super.setTitle(title);
	}
   
	@Override
  protected Object prepareInput(IProgressMonitor pm) 
	{
		return new DiffNode(leftItem, rightItem);
	}

	public File getLeftFile() {
		return new File(leftItem.getFileURI());
	}
	
	public File getRightFile() {
		// TODO Auto-generated method stub
		return new File(rightItem.getFileURI());
	}
	
	public IFile getLeftIFile(){
		try {
			IFile f = ResourceUtils.getFileForLocation(leftItem.getFileURI().getPath());
			return f;
		} catch(CoreException e) {
			CommonPlugin.getDefault().logError("Error getting IFile for " + leftItem.getFileURI().getPath(), e);
			return null;
		}
	}

	public IFile getRightIFile(){
		try {
			IFile f = ResourceUtils.getFileForLocation(rightItem.getFileURI().getPath());
			return f;
		} catch(CoreException e) {
			CommonPlugin.getDefault().logError("Error getting IFile for " + leftItem.getFileURI().getPath(), e);
			return null;
		}
	}
	
}  //  CompareInput
