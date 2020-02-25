/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.GUIs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.ViewSettingsDialog;
import org.eclipse.ui.views.markers.internal.MarkerMessages;

import com.strikewire.snl.apc.Common.CommonPlugin;


public class ColumnPropertiesDialog extends ViewSettingsDialog {

//	private Composite editArea;

//	private Label messageLabel;

	
	private Collection<String> allColumns;
	private Collection<String> non_visible;
	private List<String> visible;
	private Collection<String> non_visibleDefaults;
	private Collection<String> visibleDefaults;
	private boolean allowNoVisible_;
	
	private ListViewer non_visibleViewer;

	private ListViewer visibleViewer;
	
	private Image upArrow;
	private Image downArrow;
	private Image leftArrow;
	private Image rightArrow;
	
//	//Create Arrow Icons for buttons
//	ImageDescriptor upArrowImageDesc = ImageDescriptor
//			.createFromURL(Platform.getBundle(
//					"com.strikewire.snl.apc.Common").getEntry(
//					"/icons/event_prev.gif"));
//	Image upArrow = upArrowImageDesc.createImage();
//	ImageDescriptor downArrowImageDesc = ImageDescriptor
//			.createFromURL(Platform.getBundle(
//					"com.strikewire.snl.apc.Common").getEntry(
//					"/icons/event_next.gif"));
//	Image downArrow = downArrowImageDesc.createImage();
//	ImageDescriptor leftArrowImageDesc = ImageDescriptor
//			.createFromURL(Platform.getBundle(
//					"com.strikewire.snl.apc.Common").getEntry(
//					"/icons/e_back.gif"));
//	Image leftArrow = leftArrowImageDesc.createImage();
//	ImageDescriptor rightArrowImageDesc = ImageDescriptor
//			.createFromURL(Platform.getBundle(
//					"com.strikewire.snl.apc.Common").getEntry(
//					"/icons/e_forward.gif"));
//	Image rightArrow = rightArrowImageDesc.createImage();
	

	/**
	 * Create a new instance of the receiver.
	 * 
	 * @param view -
	 *            the view this is being launched from
	 */
	public ColumnPropertiesDialog(
			Shell shell,
			Collection<String> allCols,
			List<String> visibleCols,
			Collection<String> visibleDefaultCols,
			Collection<String> non_visibleDefaultCols,
			boolean allowNoVisible
			)
	{
		super(shell);
		
		allColumns = allCols;
		visible = visibleCols;
		non_visible = new ArrayList<String>();
		updateNonVisibleColumns();
		visibleDefaults = visibleDefaultCols;
		non_visibleDefaults = non_visibleDefaultCols;
		upArrow = makeImage(CommonPlugin.IMAGE_UP_ARROW);
		downArrow = makeImage(CommonPlugin.IMAGE_DOWN_ARROW);
		leftArrow = makeImage(CommonPlugin.IMAGE_LEFT_ARROW);
		rightArrow = makeImage(CommonPlugin.IMAGE_RIGHT_ARROW);
		allowNoVisible_ = allowNoVisible;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Select Columns");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#getShellStyle()
	 */
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		Control control = super.createContents(parent);
		
		validate();
		
		return control;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {

		Composite dialogArea = (Composite) super.createDialogArea(parent);
		createColumnsArea(dialogArea);

		applyDialogFont(dialogArea);
		return dialogArea;
	}
	
	
	private Image makeImage(String key)
	{
		ImageDescriptor descriptor = CommonPlugin.getImageDescriptor(key);
		if(descriptor != null)
		{
			return descriptor.createImage(false);
		}
		
		return null;
	}

	/**
	 * Create an area for the selected columns
	 * 
	 * @param dialogArea
	 */
	private void createColumnsArea(Composite dialogArea) {

		initializeDialogUnits(dialogArea);
		Group columnsComposite = new Group(dialogArea, SWT.NONE);
		columnsComposite.setText("Select Columns to Display");
		FormLayout layout = new FormLayout();
		columnsComposite.setLayout(layout);

		columnsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		
		Label non_visibleItemsLabel = new Label(columnsComposite, SWT.NONE);
		non_visibleItemsLabel.setText("Available Columns");
		FormData non_visibleLabelData = new FormData();
		non_visibleLabelData.right = new FormAttachment(45, 0);
		non_visibleLabelData.left = new FormAttachment(
				IDialogConstants.BUTTON_MARGIN);
		non_visibleLabelData.top = new FormAttachment(0);
		non_visibleItemsLabel.setLayoutData(non_visibleLabelData);

		int rightMargin = IDialogConstants.BUTTON_MARGIN * -1;

		Label visibleLabel = new Label(columnsComposite, SWT.NONE);
		visibleLabel.setText("Display Columns");
		FormData visibleLabelData = new FormData();
		visibleLabelData.right = new FormAttachment(100);
		visibleLabelData.left = new FormAttachment(55, 0);
		visibleLabelData.top = new FormAttachment(0);
		visibleLabel.setLayoutData(visibleLabelData);

		non_visibleViewer = new ListViewer(columnsComposite,
				SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);

		FormData non_visibleViewerData = new FormData();
		non_visibleViewerData.right = new FormAttachment(non_visibleItemsLabel, 0,
				SWT.RIGHT);
		non_visibleViewerData.left = new FormAttachment(non_visibleItemsLabel, 0,
				SWT.LEFT);
		non_visibleViewerData.top = new FormAttachment(non_visibleItemsLabel,
				IDialogConstants.BUTTON_MARGIN);
		non_visibleViewerData.bottom = new FormAttachment(100, rightMargin);
		non_visibleViewerData.height = convertHeightInCharsToPixels(15);
		non_visibleViewerData.width = convertWidthInCharsToPixels(25);

		non_visibleViewer.getControl().setLayoutData(non_visibleViewerData);

		non_visibleViewer.setContentProvider(new IStructuredContentProvider() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
			 */
			public Object[] getElements(Object inputElement) {
				return non_visible.toArray();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
			 */
			public void dispose() {
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
			 *      java.lang.Object, java.lang.Object)
			 */
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

		});

		non_visibleViewer.setInput(this);

		visibleViewer = new ListViewer(columnsComposite,
				SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		
		visibleViewer.setContentProvider(new IStructuredContentProvider() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
			 */
			public Object[] getElements(Object inputElement) {
				return visible.toArray();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
			 */
			public void dispose() {
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
			 *      java.lang.Object, java.lang.Object)
			 */
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

		});
		visibleViewer.setInput(this);

		FormData visibleViewerData = new FormData();
		visibleViewerData.right = new FormAttachment(visibleLabel, 0,
				SWT.RIGHT);
		visibleViewerData.left = new FormAttachment(visibleLabel, 0,
				SWT.LEFT);
		visibleViewerData.top = new FormAttachment(visibleLabel,
				IDialogConstants.BUTTON_MARGIN);
		visibleViewerData.bottom = new FormAttachment(100, rightMargin);
		visibleViewerData.height = convertHeightInCharsToPixels(15);
		visibleViewerData.width = convertWidthInCharsToPixels(25);

		visibleViewer.getControl().setLayoutData(visibleViewerData);

		Button toVisibleButton = new Button(columnsComposite, SWT.PUSH);
//		toVisibleButton
//				.setText(getDefaultOrientation() == SWT.RIGHT_TO_LEFT ? MarkerMessages.MarkerPreferences_MoveLeft
//						: MarkerMessages.MarkerPreferences_MoveRight);
		toVisibleButton.setImage(rightArrow);

		FormData toVisibleButtonData = new FormData();

		toVisibleButtonData.top = new FormAttachment(non_visibleViewer
				.getControl(), IDialogConstants.BUTTON_BAR_HEIGHT, SWT.TOP);
		toVisibleButtonData.left = new FormAttachment(non_visibleViewer
				.getControl(), IDialogConstants.BUTTON_MARGIN);
		toVisibleButtonData.right = new FormAttachment(visibleViewer
				.getControl(), rightMargin);
		toVisibleButton.setLayoutData(toVisibleButtonData);

		toVisibleButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) non_visibleViewer.getSelection();
				List<?> selectionList = selection.toList();
				for (Object o : selectionList) {
				  if (o instanceof String) {
				    visible.add((String)o);
				  }
				}

				updateNonVisibleColumns();
				non_visibleViewer.refresh();
				visibleViewer.refresh();
				visibleViewer.setSelection((IStructuredSelection) selection, true);
				
				validate();
			}
		});

		Button toNonVisibleButton = new Button(columnsComposite, SWT.PUSH);
//		toNonVisibleButton
//				.setText(getDefaultOrientation() == SWT.RIGHT_TO_LEFT ? MarkerMessages.MarkerPreferences_MoveRight
//						: MarkerMessages.MarkerPreferences_MoveLeft);
		toNonVisibleButton.setImage(leftArrow);

		FormData toNonVisibleButtonData = new FormData();

		toNonVisibleButtonData.top = new FormAttachment(toVisibleButton,
				IDialogConstants.BUTTON_MARGIN);
		toNonVisibleButtonData.left = new FormAttachment(non_visibleViewer
				.getControl(), IDialogConstants.BUTTON_MARGIN);
		toNonVisibleButtonData.right = new FormAttachment(visibleViewer
				.getControl(), rightMargin);
		toNonVisibleButton.setLayoutData(toNonVisibleButtonData);

		toNonVisibleButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) visibleViewer.getSelection();
				List<?> selectionList = selection.toList();
				visible.removeAll(selectionList);
				updateNonVisibleColumns();
				non_visibleViewer.refresh();
				visibleViewer.refresh();
				non_visibleViewer.setSelection((IStructuredSelection) selection, true);
				
				validate();
			}
		});
		
		
		
		
		
		
		
		
		
		
		//Only Allow one viewer to be selected at a time
		visibleViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			   public void selectionChanged(SelectionChangedEvent event) {
			       // if the selection is empty clear the label
			       if(event.getSelection().isEmpty()) {
			           return;
			       }
			       if(event.getSelection() instanceof IStructuredSelection) {
			           IStructuredSelection nonVisSelection = (IStructuredSelection) non_visibleViewer.getSelection();
			           if(nonVisSelection==null)return;
			           else{
			        	   non_visibleViewer.setSelection(null);
			           }

			       }
			   }
			});
		
		non_visibleViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			   public void selectionChanged(SelectionChangedEvent event) {
			       // if the selection is empty clear the label
			       if(event.getSelection().isEmpty()) {
			           return;
			       }
			       if(event.getSelection() instanceof IStructuredSelection) {
			           IStructuredSelection nonVisSelection = (IStructuredSelection) visibleViewer.getSelection();
			           if(nonVisSelection==null)return;
			           else{
			        	  visibleViewer.setSelection(null);
			           }

			       }
			   }
			});
		
		
		
		

		Button moveSelectionUpButton = new Button(columnsComposite, SWT.PUSH);
		moveSelectionUpButton.setImage(upArrow);

		FormData moveSelectionUpButtonData = new FormData();

		moveSelectionUpButtonData.top = new FormAttachment(toNonVisibleButton,
				IDialogConstants.BUTTON_MARGIN);
		moveSelectionUpButtonData.left = new FormAttachment(non_visibleViewer
				.getControl(), IDialogConstants.BUTTON_MARGIN);
		moveSelectionUpButtonData.right = new FormAttachment(visibleViewer
				.getControl(), rightMargin);
		moveSelectionUpButton.setLayoutData(moveSelectionUpButtonData);

		moveSelectionUpButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection visSelection = (IStructuredSelection) visibleViewer.getSelection();
				if(!visSelection.isEmpty())
				{
					List<?> selection = visSelection.toList();
					for(Object s:selection)
					{
						int pos = visible.indexOf(s.toString());
						if(pos!= 0){
							visible.add(pos-1,visible.remove(pos));
							visibleViewer.refresh();
						}
						else{
							break;//selection reached window bounds
						}
					}
					
				}
				
			}
		});
		
		
		Button moveSelectionDownButton = new Button(columnsComposite, SWT.PUSH);
		moveSelectionDownButton.setImage(downArrow);

		FormData moveSelectionDownButtonData = new FormData();

		moveSelectionDownButtonData.top = new FormAttachment(moveSelectionUpButton,
				IDialogConstants.BUTTON_MARGIN);
		moveSelectionDownButtonData.left = new FormAttachment(non_visibleViewer
				.getControl(), IDialogConstants.BUTTON_MARGIN);
		moveSelectionDownButtonData.right = new FormAttachment(visibleViewer
				.getControl(), rightMargin);
		moveSelectionDownButton.setLayoutData(moveSelectionDownButtonData);

		moveSelectionDownButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection visSelection = (IStructuredSelection) visibleViewer.getSelection();
				if(!visSelection.isEmpty())
				{
					ArrayList<Object> reversedSelection = new ArrayList<Object>();
					for(Object o:visSelection.toList()) reversedSelection.add(o);
					java.util.Collections.reverse(reversedSelection);
					for(Object s:reversedSelection)
					{
						int pos = visible.indexOf(s.toString());
						if(pos!= visible.size()-1){
							visible.add(pos+1,visible.remove(pos));
							visibleViewer.refresh();
						}
						else{
							break; //Selection reached window bounds
						}
					}
				}
				
				
				
			}
		});
	}
	
	protected void validate()
	{
		boolean valid = visible.size() > 0 || allowNoVisible_;
		setValid(
				valid,
				MarkerMessages.MarkerPreferences_AtLeastOneVisibleColumn);
	}


	/**
	 * Set the enabled state of the OK button by state.
	 * 
	 * @param state
	 */
	protected void setValid(boolean state, String errorMessage) {
		Button okButton = getButton(IDialogConstants.OK_ID);

		if (okButton == null)
			return;

		okButton.setEnabled(state);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {

//		extendedView.setVisibleColumns(visible);

		super.okPressed();
	}
	
	public List<String> getVisibleColumns(){
		return Collections.unmodifiableList(visible);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.preferences.ViewSettingsDialog#performDefaults()
	 */
	protected void performDefaults() {
		super.performDefaults();
		non_visible.clear();
		visible.clear();
		
		non_visible.addAll(non_visibleDefaults);
		visible.addAll(visibleDefaults);

		
		non_visibleViewer.refresh();
		visibleViewer.refresh();
	}

	private void updateNonVisibleColumns(){
		non_visible.clear();
		non_visible.addAll(allColumns);
		non_visible.removeAll(visible);
	}
	
	public void dispose()
	{
		if(upArrow != null)
		{
			upArrow.dispose();
			upArrow = null;
		}
		
		if(downArrow != null)
		{
			downArrow.dispose();
			downArrow = null;
		}
		
		if(leftArrow != null)
		{
			leftArrow.dispose();
			leftArrow = null;
		}
		
		if(rightArrow != null)
		{
			rightArrow.dispose();
			rightArrow = null;
		}
	}
	
}



