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
 * Created by mjgibso on Dec 19, 2009 at 1:27:19 AM
 */
package com.strikewire.snl.apc.status;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * @author mjgibso
 *
 */
public class HTMLMessageDecorator implements ILabelDecorator
{
	////////////////// THESE METHODS NOT USED BY WorkbenchStatusDialogManager /////////////////////
	public Image decorateImage(Image image, Object element) { return null; }
	public void addListener(ILabelProviderListener listener) {}
	public void dispose() {}
	public boolean isLabelProperty(Object element, String property) { return false; }
	public void removeListener(ILabelProviderListener listener) {}
	////////////////// END METHODS NOT USED BY WorkbenchStatusDialogManager ///////////////////////
	
	public String decorateText(String text, Object element)
	{
		if(text == null)
		{
			return null;
		} else if(text.toLowerCase().contains("<html")) {
			return "Press Details to see detailed error message";
		} else {
			return text;
		}
	}
}
