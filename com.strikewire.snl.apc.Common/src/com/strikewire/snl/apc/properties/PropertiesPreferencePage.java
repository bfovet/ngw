/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/**
 * Created on Aug 23, 2007 at 3:01:42 PM
 */
package com.strikewire.snl.apc.properties;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.GUIs.ChoicesButtonDialog;

/**
 * @author mjgibso
 *
 */
public abstract class PropertiesPreferencePage<E extends PropertiesInstance<E>> extends PreferencePage implements IWorkbenchPreferencePage, PropertiesConstants
{
	protected static final String ADD = "Add";
	protected static final String DUPLICATE = "Duplicate";
	protected static final String EDIT = "Edit";
	protected static final String DELETE = "Delete";
	protected static final String IMPORT = "Import";
	protected static final String EXPORT = "Export";
	
	private final PropertiesStore<E> props_;
	private List<Control> buttons_;
	
	private TableViewer tableViewer_;
	
	/**
	 * _contentProvider - The content provider
	 */
	protected IStructuredContentProvider _contentProvider = initContentProvider(); 
	
	
	
	protected PropertiesPreferencePage(PropertiesStore<E> props)
	{
		super(props.getPropertiesDisplayName()+"s");
		// TODO use the constructor that takes an image descriptor
		
		this.props_ = props;
		setDescription("Override built-in or create new user defined "+props.getPropertiesDisplayName()+"s.\n" +
				"  -Built-in "+props.getPropertiesDisplayName()+"s are shown in bold\n" +
				"  -Overridden built-in "+props.getPropertiesDisplayName()+"s have a star suffix '*'");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent)
	{
		super.createControl(parent);
		updateButtons();
	}
	
	/**
	 * Returns how many columns are being used
	 */
	protected int getNumColumns()
	{
	  return 2;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected final Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		int numCols = getNumColumns();
		GridLayout layout = new GridLayout(numCols, false);
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginBottom = 0;
		layout.marginRight = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		
		
		// add in filtering buttons
		addFilteringButtons(parent);
		
		
		// the table label
		Label tableL = new Label(composite, SWT.NONE);
		tableL.setText(props_.getPropertiesDisplayName()+"s:");
		GridData data = new GridData();
		data.horizontalSpan = numCols;
		tableL.setLayoutData(data);
		
		Composite tableComposite = new Composite(composite, SWT.NONE);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		tableComposite.setLayoutData(data);
		createTableArea(tableComposite);
		
		Composite buttonComposite = new Composite(composite, SWT.NONE);
		data = new GridData();
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		buttonComposite.setLayoutData(data);
		createButtonArea(buttonComposite);
		
		updateTable();
		
		return composite;
	}
	
	protected void createTableArea(Composite composite)
	{
		int numCols = 1;
		GridLayout layout = new GridLayout(numCols, false);
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginBottom = 0;
		layout.marginRight = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		
		// the table
		tableViewer_ = new TableViewer(composite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		initTable(tableViewer_);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		tableViewer_.getTable().setLayoutData(data);
		tableViewer_.getTable().addSelectionListener(new SelectionListener() {
		
			@Override
      public void widgetSelected(SelectionEvent e)
			{ updateButtons(); }
		
			@Override
      @SuppressWarnings("unchecked")
			public void widgetDefaultSelected(SelectionEvent event)
			{
				// pop up the edit dialog for the selected properties
				if(event.item==null || !(event.item instanceof TableItem))
					return;
				editProps((IPropertiesInstance<E>) event.item.getData(), false);
			}
		
		});
	}
	
	/**
	 * Method to initialize a content provider; returns a
	 * PropertiesProvider, but implementing classes may override.
	 */
	protected IStructuredContentProvider initContentProvider()
	{
	  return new PropertiesProvider();
	}
	
	/**
	 * Method to obtain the current content providier
	 */
	protected IStructuredContentProvider getContentProvider()
	{
	  return _contentProvider;
	}
	
	
	protected void initTable(TableViewer viewer)
	{
		// initialize the viewer
		viewer.setLabelProvider(new PropertiesLabelProvider());
		viewer.setContentProvider(getContentProvider());
		viewer.setInput(new Object());
		viewer.setSorter(new ViewerSorter());
	}
	
	protected void updateTable()
	{	
		tableViewer_.refresh(true);
		
		updateButtons();
	}

	protected void updateButtons()
	{
		int count = ((IStructuredSelection) tableViewer_.getSelection()).size();
		for(Control btn : buttons_)
		{
			if(!(btn instanceof Button))
				continue;
			
			Button button = (Button) btn;
			if(button.getText().equals(EDIT))
				button.setEnabled(count == 1);
			else if(button.getText().equals(DELETE))
				button.setEnabled(count > 0);
			else if(button.getText().equals(EXPORT))
				button.setEnabled(count > 0);
			else if(button.getText().equals(DUPLICATE))
				button.setEnabled(count == 1);
		}
		
		// restore-defaults enablement
		Button defaultButton = getDefaultsButton();
		if(defaultButton != null)
			defaultButton.setEnabled(count > 0);
	}
	
	protected List<ButtonSpec> createButtonsList()
	{
		List<ButtonSpec> buttonsList = new ArrayList<ButtonSpec>();
		buttonsList.add(new ButtonSpec(ADD, new SelectionAdapter() {
		
			@Override
			public void widgetSelected(SelectionEvent event) {
				// create a new blank properties, pop up the edit dialog
				// on it, and if it was OKed, store it in the Properties object
				// and update our table
				MutablePropertiesInstance<E> props = props_.createNewProperties();
				editProps(props.asBaseType(), true);
			}
		
		}));
		buttonsList.add(new ButtonSpec(DUPLICATE, new SelectionAdapter() {
		
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				// create a copy of the selected properties and pop up the edit dialog on it.
				// if the edit dialog returns with an OK, add it to the properties object and
				// our table, and select it.
				IStructuredSelection selection = (IStructuredSelection) tableViewer_.getSelection();
				if(selection.size() > 1)
					System.err.println("More than one "+props_.getPropertiesDisplayName()+" selected for duplication.  duplicating the first one selected.");
				Object firstItem = selection.getFirstElement();
				IPropertiesInstance<E> originalProperties = (IPropertiesInstance<E>) firstItem;
				MutablePropertiesInstance<E> duplicateProperties = originalProperties.clone();
				duplicateProperties.setName(props_.getUniqueName(duplicateProperties.getName()));
				editProps(duplicateProperties, true);
			}
		
		}));
		buttonsList.add(new ButtonSpec(EDIT, new SelectionAdapter() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// pop up the edit dialog on the selected properties
				IStructuredSelection selection = (IStructuredSelection) tableViewer_.getSelection();
				editProps((IPropertiesInstance<E>) selection.getFirstElement(), false);
				
				/* PROBLEM:  If the contents of the selected TableItem is wider than the width of the widest TableItem */
				/*           then, on a Mac, the TableItem is truncated and ellipses appear                            */
				/* SOLUTION:  Force the layout manager to resize the TableItem.                                        */
				tableViewer_.getTable().setFont(tableViewer_.getTable().getFont());
			}
		
		}));
		buttonsList.add(new ButtonSpec(DELETE, new SelectionAdapter() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				// go through the selection, if it contains any built-in or overridden properties,
				// tell the user they can't delete them.
				IStructuredSelection selection = (IStructuredSelection) tableViewer_.getSelection();
				if(selection.isEmpty())
				{
					System.err.println("Nothing selected to delete");
					return;
				}
				List<MutablePropertiesInstance<E>> propertiesToRemove = new ArrayList<MutablePropertiesInstance<E>>();
				for(Object element : selection.toArray())
				{
					IPropertiesInstance<E> properties = (IPropertiesInstance<E>) element;
					boolean isDefault;
					if(properties instanceof MutablePropertiesInstance)
						isDefault = ((MutablePropertiesInstance<E>) properties).isOverriding();
					else
						isDefault = true;
					
					if(isDefault)
					{
						// this is either an overridden or default properties
						String message = "Built-in "+props_.getPropertiesDisplayName()+"s cannot be deleted." +
								"  Please select only user defined "+props_.getPropertiesDisplayName()+"s to delete." +
								"\n\nIf you're trying to restore a default "+props_.getPropertiesDisplayName()+" that" +
								" has been overriden, please instead use the \"Restore Defaults\" button at the bottom.";
						MessageDialog.openError(getShell(), "Cannot Delete", message);
						return;
					}
					
					propertiesToRemove.add((MutablePropertiesInstance<E>) properties);
				}
				
//				// if we're deleting any overridden properties, make sure the user knows they're "restoring defaults"
//				boolean restoreDefaults = false;
//				if(hasOverridden)
//				{
//					MessageBox mb = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
//					mb.setText("Restore Originals?");
//					mb.setMessage("The selection contains overridden "+props_.getPropertiesDisplayName()+"s.  Would you like to restore the originals?");
//					switch (mb.open())
//					{
//						case SWT.YES: restoreDefaults = true; break;
//						case SWT.NO: restoreDefaults = false; break;
//						default: return;
//					}
//				}
				
				for(MutablePropertiesInstance<E> mProps : propertiesToRemove)
					props_.removeProperties(mProps);
				
				updateTable();
			}
		
		}));
		buttonsList.add(new ButtonSpec(""));
		buttonsList.add(new ButtonSpec(IMPORT, new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN | SWT.MULTI);
				fd.setText("Choose "+props_.getPropertiesDisplayName()+"(s)");
				fd.setFilterExtensions(new String[] { "*"+DOT_PROPERTIES });
				String firstFile = fd.open();
				if(firstFile == null)
					return;
				
				IPath firstFilePath = new Path(firstFile);
				String[] fileNames = fd.getFileNames();
				try {
					if(fileNames == null)
					{
						try {
							MutablePropertiesInstance<E> mp = props_.createNewProperties(firstFilePath.toFile());
							mp.setModified();
							if(props_.getUserPropertiesNames().contains(mp.getName()))
							{
								if(!MessageDialog.openConfirm(getShell(), "Confirm Overwrite", "A "+props_.getPropertiesDisplayName()+" already exists with the name \""+mp.getName()+"\".  If you continue with the import, you will overwrite it."))
									return;
								
								props_.removeProperties(mp);
							}
							props_.addProperties(mp);
						} catch (Exception e) {
							MessageDialog.openError(getShell(), "Error Importing", "Error importing the file "+firstFilePath+".  Message: "+e.getMessage());
							e.printStackTrace();
						}
					} else {
						IPath folderPath = firstFilePath.removeLastSegments(1);
						Boolean overwrite = null;
						for(String fileName : fileNames)
						{
							try {
								MutablePropertiesInstance<E> mp = props_.createNewProperties(folderPath.append(fileName).toFile());
								mp.setModified();
								if(props_.getUserPropertiesNames().contains(mp.getName()) && overwrite==null)
								{
									ChoicesButtonDialog cbd = new ChoicesButtonDialog(getShell(), "Overwrite Existing "+props_.getPropertiesDisplayName()+"?", "A "+props_.getPropertiesDisplayName()+" already exists with the name \""+mp.getName()+"\".  Are you sure you want to overwrite it?", new String[] { "Yes", "Yes to all", "No", "No to all", "Cancel" }, "Cancel");
									cbd.open();
									String choice = cbd.getValue();
									if(choice==null || choice.equals("Cancel"))
										return;
									if(choice.equals("Yes to all"))
										overwrite = true;
									if(choice.equals("No to all"))
									{
										overwrite = false;
										continue;
									}
									
									if(choice.equals("No"))
										continue;
									
									props_.removeProperties(mp);
								}
								
								if(overwrite!=null && !overwrite)
									continue;
								
								props_.addProperties(mp);
							} catch (Exception e) {
								MessageDialog.openError(getShell(), "Error Importing", "Error importing the file "+folderPath.append(fileName)+".  Message: "+e.getMessage());
								e.printStackTrace();
							}
						}
					}
				} finally {
					updateTable();
				}
			}
		
		}));
		buttonsList.add(new ButtonSpec(EXPORT, new SelectionAdapter() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer_.getSelection();
				if(selection.size() < 1)
					return;
				
				DirectoryDialog dd = new DirectoryDialog(getShell(), SWT.NONE);
				dd.setMessage("Choose a location to export the selected "+props_.getPropertiesDisplayName()+"(s).");
				dd.setText("Choose Folder");
				String chosenFolderPath = dd.open();
				if(chosenFolderPath == null)
					return;
				
				File chosenFolder = new File(chosenFolderPath);
				if(chosenFolder.exists())
				{
					if(chosenFolder.isFile())
					{
						MessageDialog.openError(getShell(), "Cannot Overwrite File", "The specified location represents a file which cannot be overwritten.  Please choose a directory.");
						return;
					}
				} else
					chosenFolder.mkdirs();
				
				// see if anything will be overwritten
				List<String> existingFileNames = Arrays.asList(chosenFolder.list());
				Boolean overwrite = null;
				for(Object propsObj : selection.toList())
				{
					IPropertiesInstance<E> props = (IPropertiesInstance<E>) propsObj;
					if(existingFileNames.contains(props.getName()+DOT_PROPERTIES) && overwrite==null)
					{
						String message = "The file named "+props.getName()+DOT_PROPERTIES+" already exists.  Are you sure you want to overwrite it?";
						ChoicesButtonDialog cbd = new ChoicesButtonDialog(getShell(), "Overwrite Existing File?", message, new String[] {"Yes", "Yes to all", "No", "No to all", "Cancel"}, "Cancel");
						cbd.open();
						String choice = cbd.getValue();
						if(choice==null || choice.equals("Cancel"))
							return;
						if(choice.equals("Yes to all"))
							overwrite = true;
						if(choice.equals("No to all"))
							overwrite = false;
						if(choice.equals("No"))
							continue;
					}
					
					if(overwrite!=null && !overwrite)
						continue;
					
					props.saveAs(new File(chosenFolder, props.getName()+DOT_PROPERTIES));
				}
			}
		
		}));
		return buttonsList;
	}
	
	protected void editProps(IPropertiesInstance<E> props, boolean forceAdd)
	{
		if(props == null)
		{
			System.err.println("Null properties sent for edit.");
			return;
		}
		// if this is not a mutable properties, we need to make one for it.  It means that the user is "overriding"
		// TODO do we want to warn the user that's what they're doing?  "Overriding"
		MutablePropertiesInstance<E> mp;
		boolean newProps = false;
		if(props instanceof MutablePropertiesInstance)
			mp = (MutablePropertiesInstance<E>) props;
		else {
			mp = props.clone();
			newProps = true;
		}
		if(showEditDialog(mp) == Window.OK)
		{
			if((forceAdd || newProps) && mp.isModified())
			{
				props_.addProperties(mp);
				updateTable();
				tableViewer_.setSelection(new StructuredSelection(mp));
			}
			updateTable();
		}
	}
	
	protected final TableViewer getTableViewer()
	{ return tableViewer_; }
	
	protected abstract int showEditDialog(MutablePropertiesInstance<E> mp);
	
	protected void createButtonArea(Composite composite)
	{
		int numCols = 1;
		GridLayout layout = new GridLayout(numCols, false);
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginBottom = 0;
		layout.marginRight = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		
		buttons_ = new ArrayList<Control>();
		
		List<ButtonSpec> buttonSpecs = createButtonsList();
		if(buttonSpecs == null)
			return;
		
		for(ButtonSpec buttonSpec : buttonSpecs)
		{
			if(buttonSpec == null)
				continue;
			
			if(buttonSpec.name_==null || buttonSpec.name_.trim().equals(""))
			{
				buttons_.add(new Label(composite, SWT.NONE));
				continue;
			}
			
			Button button = new Button(composite, SWT.NONE);
			button.setText(buttonSpec.name_);
			GridData data = new GridData();
			data.horizontalAlignment = GridData.FILL;
			data.verticalAlignment = GridData.BEGINNING;
			button.setLayoutData(data);
			if(buttonSpec.selectionListener_ != null)
				button.addSelectionListener(buttonSpec.selectionListener_);
			buttons_.add(button);
		}
	}
	
	

	/**
	 * An empty method to be overridden to place any additional buttons,
	 * such as used for filtering, on the dialog
	 */
	protected void addFilteringButtons(Composite parent)
	{
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
  public void init(IWorkbench workbench)
	{}
	
//	private void updatePage()
//	{
//		setErrorMessage(null);
//		setMessage(null);
//		setValid(validate());
//	}
//	
//	private boolean validate()
//	{
//		return true;
//	}

	@Override
	public boolean performCancel()
	{
		// revert all modified mutable properties.  this includes added and deleted ones.
		props_.initialize();
		return super.performCancel();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void performDefaults()
	{
		// go through the selection.  pick out only overridden properties, and restore them.
		IStructuredSelection selection = (IStructuredSelection) tableViewer_.getSelection();
		if(selection.isEmpty())
		{
			System.err.println("Nothing selected to restore");
			return;
		}
		List<MutablePropertiesInstance<E>> propertiesToRestore = new ArrayList<MutablePropertiesInstance<E>>();
		for(Object element : selection.toArray())
		{
			IPropertiesInstance<E> properties = (IPropertiesInstance<E>) element;
			if(!(properties instanceof MutablePropertiesInstance))
				continue;
			
			MutablePropertiesInstance<E> mProps = (MutablePropertiesInstance<E>) properties;
			if(!mProps.isOverriding())
				continue;
			
			propertiesToRestore.add(mProps);
		}
		
		for(MutablePropertiesInstance<E> mProps : propertiesToRestore)
			props_.removeProperties(mProps);
		
		updateTable();
		super.performDefaults();
	}

	@Override
	public boolean performOk()
	{
		// persist all modified mutable properties.  This includes added and deleted ones.
		try {
			props_.saveChanges();
		} catch (IOException e) {
			String msg = "The following error occurred attempting to save the "+props_.getPropertiesDisplayName()+"s: "+e.getMessage();
			MessageDialog.openError(getShell(), "Error Saving "+props_.getPropertiesDisplayName()+"s", msg);
			CommonPlugin.getDefault().logError(msg, e);
		}
		
		return super.performOk();
	}
	
	protected static class ButtonSpec
	{
		final String name_;
		final SelectionListener selectionListener_;
		
		ButtonSpec(String name)
		{ this(name, null); }
		
		ButtonSpec(String name, SelectionListener selectionListener)
		{
			if(name == null)
				throw new IllegalArgumentException("Button name cannot be null");
			
			this.name_ = name;
			this.selectionListener_ = selectionListener;
		}
	}
	
	protected class PropertiesLabelProvider extends LabelProvider implements IFontProvider
	{
		private final Font boldFont_;
		
		protected PropertiesLabelProvider()
		{
			FontData[] fds = getTableFont().getFontData();
			fds[0].setStyle(fds[0].getStyle() | SWT.BOLD);
			boldFont_ = new Font(null, fds);
		}
		
		protected Font getTableFont()
		{ return tableViewer_.getTable().getFont(); }
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element)
		{
			if(element==null || !(element instanceof IPropertiesInstance))
			{
				return "";
			}
			
			IPropertiesInstance<?> props = (IPropertiesInstance<?>) element;
			String name = props.getName();
			if((props instanceof MutablePropertiesInstance) && ((MutablePropertiesInstance<?>) props).isOverriding())
			{
				name += "*";
			}
			
			return name;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IFontProvider#getFont(java.lang.Object)
		 */
		@Override
    public Font getFont(Object element)
		{
			if(element==null || !(element instanceof IPropertiesInstance))
			{
				return getTableFont();
			}
			
			IPropertiesInstance<?> props = (IPropertiesInstance<?>) element;
			boolean hasDefault = false;
			// it is/has a default if it's not mutable or it is mutable and it's overriding
			if(props instanceof MutablePropertiesInstance)
			{
				if(((MutablePropertiesInstance<?>) props).isOverriding())
				{
					hasDefault = true;
				}
			} else {
				hasDefault = true;
			}
			
			return hasDefault ? this.boldFont_ : getTableFont();
		}
		
		@Override
		public void dispose()
		{
			this.boldFont_.dispose();
			
			super.dispose();
		}
	}
	
	public class PropertiesProvider implements IStructuredContentProvider
	{
	  Viewer _viewer;
	  
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		@Override
    public void dispose() {}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		  if (viewer != null) {
		    _viewer = viewer;
		  }
		  
		  if (_viewer != null && newInput != null) {
		    _viewer.refresh();
		  }
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		@Override
    public Object[] getElements(Object inputElement)
		{ return props_.getAllProperties(false).toArray(); }
	}
}
