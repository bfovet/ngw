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
 * Created by mjgibso on Aug 27, 2013 at 11:16:26 AM
 */
package com.strikewire.snl.apc.GUIs.settings;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.IManagedForm;


/**
 * @author mjgibso
 *
 */
public interface IActionEditor
{
	public Image getTitleImage();
	
	public String getTitle();
	
	public String getDescription();
	
	public boolean isPinnable();
	
	public String getActionID();
	
	public void update(IActionEditorContainer aeContainer);
	
	/**
	 * Method is invoked to allow the instance of this interface to create their
	 * UI in the provided parent composite.
	 * <p>
	 * The parent composite uses a FillLayout as a layout manager, so there's no need
	 * to set any layout data on child components.
	 * </p>
	 * <p>
	 * It is good practice to create a composite under the provided parent, in which any
	 * desired layout manager can be installed.
	 * </p>
	 * <p>
	 * A reference to the IActionEditorContainer that will contain this editor is provided
	 * so that the instance of this class may call the container's close method when it is
	 * appropriate to do so.  Alternatively, if the instance of this class provides its own
	 * buttons (see {@link #createButtonBar()}), it can control when the view is closed via
	 * the return from {@link #buttonPressed(int)}, which is called when buttons are pressed.
	 * </p>
	 * @param mform - The parent composite to draw on
	 * @param aeContainer - The owning ActionEditorContainer (usually an ActionView)
	 */
	public void createEditorArea(IManagedForm mform, IActionEditorContainer aeContainer);
	
	/**
	 * Method dictates whether or not the owning ActionView should create a button bar.
	 * <p>
	 * Note, if the implementor of this decides not to create a button bar, or create any
	 * buttons, it will be their responsibility to close the owning {@link IActionEditorContainer}
	 * by calling {@link IActionEditorContainer#close()} on the reference provided to the
	 * {@link #createEditorArea(IManagedForm, IActionEditorContainer)} method when appropriate.
	 * </p>
	 * @see #createButtonsForButtonBar(IActionEditorContainer)
	 * 
	 * @return <b>true</b> if a button bar should be created, <b>false</b> if it should not.
	 */
	public boolean createButtonBar();
	
	/**
	 * Method is invoked to allow the instance of this interface to create buttons on the
	 * button bar.
	 * <p>
	 * This method will only be called if the return from {@link #createButtonBar()} is true.
	 * </p>
	 * <p>
	 * Buttons are created on the button bar by calling
	 * {@link IActionEditorContainer#createButton(int, String, boolean)} on the provided owning
	 * IActionEditorContainer reference.
	 * </p>
	 * 
	 * @param aView
	 */
	public void createButtonsForButtonBar(IActionEditorContainer aeContainer);
	
	/**
	 * Notifies that this editor's button with the given id has been pressed.
	 * <p>
	 * The provided <b>buttonId</b> corresponds to that provided when the button was created
	 * via a call to {@link ActionView#createButton(int, String, boolean)}.
	 * </p>
	 * <p>
	 * Method also indicates whether or not the view should be closed after the button is
	 * pressed.  This is indicated via the return.  A return of <b>true</b> indicates the
	 * view should be closed after the button is pressed.  A return of <b>false</b> indicates
	 * the view should remain open after the button is pressed.
	 * </p>
	 * 
	 * @param buttonId
	 *            the id of the button that was pressed (see
	 *            <code>IDialogConstants.*_ID</code> constants)
	 * @return <b>true</b> if the view should be closed after the button is pressed, <b>false</b>
	 *  		if the view should remain open after the button is pressed.
	 */
	public boolean buttonPressed(int buttonId);
	
	/**
	 * Method is called when the owning {@link ActionView} is closing.  The implementation of this
	 * interface should take responsibility for disposing for example any non-cached GDI objects
	 * it creates to prevent GDI leaks.
	 */
	public void dispose();
}
