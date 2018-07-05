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
 * Created by mjgibso on Aug 28, 2013 at 5:01:32 AM
 */
package com.strikewire.snl.apc.GUIs.settings;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;

import com.strikewire.snl.apc.Common.CommonPlugin;


/**
 * TODO add validation hooks, and display validation messages somewhere, and update the apply
 * button enablement accordingly.
 *
 * @author mjgibso
 * 
 */
public abstract class AbstractActionEditor implements IActionEditor
{
	public static final int APPLY_ID = IDialogConstants.CLIENT_ID + 1;
	
	public static final String APPLY_LABEL = "Apply";
	
	public static final int PREVIEW_ID = IDialogConstants.CLIENT_ID + 2;
	
	public static final String PREVIEW_LABEL = "Preview";
	
	private Image image_;
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.settings.IActionViewEditor#createButtonBar()
	 */
	@Override
	public boolean createButtonBar()
	{
		return true;
	}
	
	/**
	 * Adds buttons to the {@link ActionView}'s button bar.
	 * <p>
	 * The implementation of this framework method adds
	 * standard apply and cancel buttons using the <code>createButton</code>
	 * framework method. These standard buttons will be accessible from
	 * {@link IActionEditorContainer#getButton(int)}, using {@link #APPLY_ID}
	 * and {@link IDialogConstants#CANCEL_ID} respectively.
	 * Subclasses may override.
	 * </p>
	 * 
	 * @param ActionView
	 *            the reference to the ActionView on which to create the buttons
	 */
	@Override
	public void createButtonsForButtonBar(IActionEditorContainer aeContainer)
	{
		// create Apply and Cancel buttons by default
		
		// TODO help button
		
		if(this instanceof IPreviewable)
		{
			aeContainer.createButton(PREVIEW_ID, getPreviewLabel(), false);
		}
		aeContainer.createButton(APPLY_ID, getApplyLabel(), true);
		aeContainer.createButton(IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.settings.IActionViewEditor#buttonPressed(int)
	 */
	@Override
	public boolean buttonPressed(int buttonId)
	{
		switch (buttonId)
		{
			case APPLY_ID:
				return performApply();
			case IDialogConstants.CANCEL_ID:
				return true;
			case PREVIEW_ID:
				if(this instanceof IPreviewable)
				{
					((IPreviewable) this).performPreview();
				}
				return false;
			default:
				IStatus warn = CommonPlugin.getDefault().newWarningStatus("Unhandled action view button ID: "+buttonId, new Exception());
				CommonPlugin.getDefault().log(warn);
				return false;
		}
	}
	
	protected String getApplyLabel()
	{
		return APPLY_LABEL;
	}
	
	protected String getPreviewLabel()
	{
		return PREVIEW_LABEL;
	}
	
	/**
	 * Method invoked when the apply button is pressed.  Clients should override unless
	 * they've defined their own buttons, not to include the apply button, or they're
	 * overriding the buttonPressed method themselves and handling all button presses
	 * directly.
	 * 
	 * @return true if the view should be closed upon return from this method
	 */
	protected boolean performApply()
	{
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.settings.IActionViewEditor#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return null;
	}
	
	@Override
  public String getDescription()
	{
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.settings.IActionViewEditor#getTitleImage()
	 */
	@Override
	public Image getTitleImage()
	{
		if(image_==null || image_.isDisposed())
		{
			ImageDescriptor desc = getTitleImageDescriptor();
			if(desc != null)
			{
				image_ = desc.createImage();
			}
		}
		
		return image_;
	}
	
	public ImageDescriptor getTitleImageDescriptor()
	{
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.settings.IActionEditor#dispose()
	 */
	@Override
	public void dispose()
	{
		if(image_!=null && !image_.isDisposed())
		{
			image_.dispose();
		}
		image_ = null;
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.settings.IActionViewEditor#isPinnable()
	 */
	@Override
	public boolean isPinnable()
	{
		return false;
	}
	
	@Override
	public void update(IActionEditorContainer aeContainer)
	{
		boolean valid = isValid(aeContainer);
		
		Button applyBtn = aeContainer.getButton(APPLY_ID);
		if(applyBtn != null)
		{
			applyBtn.setEnabled(valid);
		}
	}
	
	public boolean isValid(IMessageView aeContainer)
	{
		aeContainer.setMessage(null, 0);
		return true;
	}
}
