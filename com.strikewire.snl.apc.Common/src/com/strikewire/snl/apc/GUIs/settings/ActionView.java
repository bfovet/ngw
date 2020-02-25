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
 * Created by mjgibso on Aug 27, 2013 at 6:31:29 AM
 */
package com.strikewire.snl.apc.GUIs.settings;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.GUIs.CompositeUtils;

/**
 * @author mjgibso
 *
 * Much of this class was ripped off from org.eclipse.jface.dialogs.Dialog 
 */
public class ActionView extends AbstractMessageView implements IActionEditorContainer
{
	private Composite parent_;
	
	IActionEditor currentEditor_;
	
	private FormToolkit toolkit_;
	
	public static final String VIEW_ID = "com.strikewire.snl.apc.common.view.action";
	
	private ScrolledForm sForm;
	private Composite buttonBar_;
	private Composite messageArea_;
	
	
	/**
	 * Collection of buttons created by the <code>createButton</code> method.
	 */
	private final HashMap<Integer, Button> buttons_ = new HashMap<Integer, Button>();

	/**
	 * Font metrics to use for determining pixel sizes.
	 */
	private FontMetrics fontMetrics_;
	
	private final PinnedAction pinnedAction_ = new PinnedAction();
	
	private class PinnedAction extends Action
	{
		/**
		 * 
		 */
		public PinnedAction()
		{
			super(null, AS_CHECK_BOX);
			
			setImageDescriptor(CommonPlugin.getImageDescriptor(CommonPlugin.IMAGE_PIN_EDITOR));
		}
	}
	
	private final ActionContributionItem pinnedActionContributionItem_ = new ActionContributionItem(pinnedAction_);
	
	/**
	 * 
	 */
	public ActionView()
	{}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		this.parent_ = parent;
		parent_.setBackground(parent_.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		this.parent_.setLayout(new FillLayout());
		
		this.toolkit_ = new FormToolkit(parent.getDisplay());
		
		// TODO put up a message that no action is currently specified
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
		// TODO need to set focus somewhere...
	}
	
	@Override
	public void close()
	{
		getSite().getPage().hideView(this);
	}
	
	private void clear()
	{
		setMessage("", IMessageProvider.NONE);

		if(currentEditor_ != null)
		{
			currentEditor_.dispose();
			currentEditor_ = null;
		}
		
		if(parent_ != null)
		{
			CompositeUtils.removeChildrenFromComposite(parent_, false);
		}
		
		
		clearPinnedAction();
	}
	
	private void clearPinnedAction()
	{
		IViewSite viewSite = getViewSite();
		if(viewSite == null)
		{
			return;
		}
		
		viewSite.getActionBars().getToolBarManager().remove(pinnedActionContributionItem_);
		
		// reset pinned state
		pinnedAction_.setChecked(false);
	}
	
	private Composite createContents(Composite parent)
	{		
		// initialize the units
		// initializeUnits(parent);
		Composite composite = toolkit_.createComposite(parent);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		
		// initialize the units
		initializeUnits(composite);
		
		sForm = toolkit_.createScrolledForm(composite);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.grabExcessHorizontalSpace = true;
		sForm.setLayoutData(layoutData);

		createTitleAndMessageArea(sForm);

		sForm.getBody().setLayout(new FillLayout());
		currentEditor_.createEditorArea(new ManagedForm(toolkit_, sForm), this);
		
		if(currentEditor_.createButtonBar())
		{
			createButtonBar(composite);
		}
		
		if(currentEditor_.isPinnable())
		{
			IToolBarManager toolBar = getViewSite().getActionBars().getToolBarManager();
			toolBar.add(pinnedActionContributionItem_);
			toolBar.update(false);
		}
		
		currentEditor_.update(this);

		layoutForNewMessage();

		parent_.layout(true, true);
			
		return composite;
	}
		
	private void createTitleAndMessageArea(ScrolledForm parent)
	{				
		String title = currentEditor_.getTitle();
		sForm.getForm().setText(StringUtils.isNotBlank(title) ? title : null);
		
		if(StringUtils.isNotBlank(title))
		{
			Image img = currentEditor_.getTitleImage();
			if(img != null)
			{
				sForm.getForm().setImage(img);
			}
		}
		
		toolkit_.decorateFormHeading(sForm.getForm());
		
		messageArea_ = createMessageArea(sForm.getForm().getHead());
		sForm.setHeadClient(messageArea_);
		setMessage("", IMessageProvider.NONE);
		
	}
	
	/**
	 * Re-layout the labels for the new message.
	 */
	@Override
	protected void layoutForNewMessage() {
		boolean showMessageArea = StringUtils.isNotBlank(message) && messageImage!=null;
		sForm.setHeadClient(showMessageArea ? messageArea_ : null);
		sForm.layout(new Control[] {messageImageLabel, messageLabel});
	}
	
	
	@Override  protected  String getDefaultMessage()
	{
		return currentEditor_!=null ? currentEditor_.getDescription() : null;
	}
	
	@Override
	public boolean isPinned()
	{
		return pinnedAction_.isChecked();
	}
	
	/**
	 * Initializes the computation of horizontal and vertical units based
	 * on the size of current font.
	 * <p>
	 * This method must be called before any of the unit based conversion
	 * methods are called.
	 * </p>
	 * 
	 * @param control
	 *            a control from which to obtain the current font
	 */
	protected void initializeUnits(Control control) {
		// Compute and store a font metric
		GC gc = new GC(control);
		gc.setFont(JFaceResources.getDialogFont());
		fontMetrics_ = gc.getFontMetrics();
		gc.dispose();
	}

	/**
	 * Notifies that this editor's button with the given id has been pressed.
	 * 
	 * @param buttonId
	 *            the id of the button that was pressed (see
	 *            <code>IDialogConstants.*_ID</code> constants)
	 */
	protected void buttonPressed(int buttonId)
	{
		if(currentEditor_.buttonPressed(buttonId))
		{			
			if((IDialogConstants.CANCEL_ID == buttonId) || (!isPinned() && (AbstractActionEditor.APPLY_ID == buttonId)))
			{
				close();
			}
		}
	}

	/**
	 * Returns the number of pixels corresponding to the given number of
	 * horizontal units.
	 * <p>
	 * This method may only be called after <code>initializeUnits</code>
	 * has been called.
	 * </p>
	 * <p>
	 * Clients may call this framework method, but should not override it.
	 * </p>
	 * 
	 * @param units
	 *            the number of horizontal units
	 * @return the number of pixels
	 */
	protected int convertHorizontalUnitsToPixels(int units) {
		// test for failure to initialize for backward compatibility
		if (fontMetrics_ == null) {
			return 0;
		}
		return Dialog.convertHorizontalDLUsToPixels(fontMetrics_, units);
	}

	/**
	 * Returns the number of pixels corresponding to the given number of
	 * vertical units.
	 * <p>
	 * This method may only be called after <code>initializeUnits</code>
	 * has been called.
	 * </p>
	 * <p>
	 * Clients may call this framework method, but should not override it.
	 * </p>
	 * 
	 * @param units
	 *            the number of vertical units
	 * @return the number of pixels
	 */
	protected int convertVerticalUnitsToPixels(int units) {
		// test for failure to initialize for backward compatibility
		if (fontMetrics_ == null) {
			return 0;
		}
		return Dialog.convertVerticalDLUsToPixels(fontMetrics_, units);
	}

	/**
	 * Creates a new button with the given id.
	 * <p>
	 * The implementation of this framework method creates
	 * a standard push button, registers it for selection events including
	 * button presses, and registers default buttons with its shell. The button
	 * id is stored as the button's client data. Note that the parent's layout is
	 * assumed to be a <code>GridLayout</code> and the number of columns in
	 * this layout is incremented. Subclasses may override.
	 * </p>
	 * 
	 * @param parent
	 *            the parent composite
	 * @param id
	 *            the id of the button (see <code>IDialogConstants.*_ID</code>
	 *            constants for standard dialog button ids)
	 * @param label
	 *            the label from the button
	 * @param defaultButton
	 *            <code>true</code> if the button is to be the default button,
	 *            and <code>false</code> otherwise
	 * 
	 * @return the new button
	 */
	@Override
	public Button createButton(int id, String label, boolean defaultButton)
	{
		// increment the number of columns in the button bar
		((GridLayout) buttonBar_.getLayout()).numColumns++;
		Button button = toolkit_.createButton(buttonBar_, label, SWT.PUSH);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				buttonPressed(((Integer) event.widget.getData()).intValue());
			}
		});
		if (defaultButton) {
			Shell shell = buttonBar_.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		buttons_.put(new Integer(id), button);
		setButtonLayoutData(button);
		return button;
	}

	/**
	 * Creates and returns the contents of this view's button bar.
	 * <p>
	 * The implementation of this framework method lays
	 * out a button bar and calls the <code>createButtonsForButtonBar</code>
	 * framework method to populate it. Subclasses may override.
	 * </p>
	 * 
	 * @param parent
	 *            the parent composite to contain the button bar
	 */
	protected void createButtonBar(Composite parent)
	{
		Label separator = toolkit_.createSeparator(parent, SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false));
		
		buttonBar_ = toolkit_.createComposite(parent);
		
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		buttonBar_.setLayoutData(data);
		buttonBar_.setFont(parent.getFont());
		
		// create a layout with spacing and margins appropriate for the font
		// size.
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 0; // this is incremented by createButton
		layout.makeColumnsEqualWidth = true;
		layout.marginWidth = convertHorizontalUnitsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalUnitsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.horizontalSpacing = convertHorizontalUnitsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalUnitsToPixels(IDialogConstants.VERTICAL_SPACING);
		buttonBar_.setLayout(layout);
		
		
		
		// Add the buttons to the button bar.
		currentEditor_.createButtonsForButtonBar(this);
	}

	/**
	 * Returns the button created by the method <code>createButton</code> for
	 * the specified ID as possibly defined on <code>IDialogConstants</code>. If
	 * <code>createButton</code> was never called with this ID, or if
	 * <code>createButton</code> is overridden, this method will return
	 * <code>null</code>.
	 * 
	 * @param id
	 *            the id of the button to look for
	 * 
	 * @return the button for the ID or <code>null</code>
	 * 
	 * @see #createButton(int, String, boolean)
	 */
	@Override
	public Button getButton(int id) {
		return buttons_.get(new Integer(id));
	}

	/**
	 * Returns the button bar control.
	 * <p>
	 * Clients may call this framework method, but should not override it.
	 * </p>
	 * 
	 * @return the button bar, or <code>null</code> if the button bar has not
	 *         been created yet
	 */
	protected Composite getButtonBar() {
		return buttonBar_;
	}

	
	/**
	 * Set the layout data of the button to a GridData with appropriate heights
	 * and widths.
	 * 
	 * @param button
	 */
	protected void setButtonLayoutData(Button button) {
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		int widthHint = convertHorizontalUnitsToPixels(IDialogConstants.BUTTON_WIDTH);
		Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);
		button.setLayoutData(data);
	}

	@Override
	public void dispose()
	{
		clear();
		
		parent_ = null;
		
		if(currentEditor_ != null)
		{
			currentEditor_.dispose();
		}
		currentEditor_ = null;
		
		if(toolkit_ != null)
		{
			toolkit_.dispose();
		}
		toolkit_ = null;
		
		buttonBar_ = null;
		
		for(Button button : buttons_.values())
		{
			if(!button.isDisposed())
			{
				button.dispose();
			}
		}
		buttons_.clear();
		
		fontMetrics_ = null;
		
		super.dispose();
	}

	public synchronized void showEditor(IActionEditor editor)
	{
		clear();
		
		currentEditor_ = editor;
		
		createContents(parent_);
		
		String title = currentEditor_.getTitle();
		if(StringUtils.isNotBlank(title))
		{
			setPartName(title);
		}
		
		// TODO let the editor provide a title tooltip?
		
		Image img = currentEditor_.getTitleImage();
		if(img != null)
		{
			setTitleImage(img);
		}
		
	}
	
	public synchronized static void show(IActionEditor editor, IWorkbenchPage page) throws PartInitException
	{
		IViewPart viewPart;
		String actionID = editor.getActionID();
		viewPart = page.showView(VIEW_ID, actionID, IWorkbenchPage.VIEW_ACTIVATE);

		
		if(viewPart instanceof ActionView)
		{
			ActionView aView = (ActionView) viewPart;
			aView.showEditor(editor);
		}
	}
	
	public static void show(IActionEditor editor) throws PartInitException
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		show(editor, page);
	}

	@Override
	public void setPinned(boolean pinned) {
		pinnedAction_.setChecked(pinned);
	}
}
