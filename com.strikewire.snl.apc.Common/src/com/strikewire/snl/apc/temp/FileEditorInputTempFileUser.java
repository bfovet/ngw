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
 * Created by mjgibso on Jun 23, 2010 at 9:25:07 AM
 */
package com.strikewire.snl.apc.temp;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * @author mjgibso
 *
 */
public class FileEditorInputTempFileUser implements ITempFileUser
{
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.temp.ITempFileUser#isTempFileInUse(java.io.File)
	 */
	@Override
  public boolean isTempFileInUse(File tmpFile)
	{
		for(IEditorReference editorRef : TempFileManager.getOpenEditorRefs())
		{
			try {
				IEditorInput input = editorRef.getEditorInput();
				if(input instanceof FileEditorInput)
				{
					IFile file = ((FileEditorInput) input).getFile();
					if(file.getLocation().toFile().equals(tmpFile))
					{
						return true;
					}
				}
			} catch (PartInitException e) {
				CommonPlugin.getDefault().logError("Error getting editor input to test for temp file usage: "+e.getMessage(), e);
			}
		}
		
		return false;
	}
} //class
