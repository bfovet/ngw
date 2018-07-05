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
 * 
 */
package com.strikewire.snl.apc.GUIs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * <p>A dialog which provides the ability to display a range of choices,
 * with a check box in front of them. May specify single selection.</p>
 * 
 * 
 * @author mjgibso
 * 
 */
public class CheckListDialog<E> extends TitleAreaDialog
{
  private static final String SELECT_ALL_LABEL = "Select All";
  private static final String SELECT_NONE_LABEL = "Select None";
	
  private static final Point MIN_SIZE = new Point(200, 50);
  private static final Point MAX_SIZE = new Point(700, 500);


  private final List<E> _choices;

  private final ILabelProvider _labelProvider;
  private final ViewerComparator _comparator;

  private final String _shellTitle;
  private final String _dialogTitle;
  private final String _dialogMessage;


  private CheckboxTableViewer viewer_;

  private boolean singleSelection_ = false;

  private List<E> initialSelections_ = new ArrayList<E>();

  private List<E> result_;

  /**
   * @param parentShell The shell
   * @param choices The choices to show to the use
   * @param labelProvider The label provider; may be null
   * @param shellTitle The title shown on the shell; may be null
   * @param dialogTitle The title shown in the dialog; may be null
   * @param dialogMessage A message to display in the dialog; may be null
   * @param readOnly If true, then 
   */
  public CheckListDialog(Shell parentShell, List<E> choices,
          ILabelProvider labelProvider, String shellTitle,
          String dialogTitle, String dialogMessage)
  {
    this(parentShell, choices, labelProvider, null, shellTitle, dialogTitle, dialogMessage);
  }


  /**
   * @param parentShell The shell
   * @param choices The choices to show to the use
   * @param labelProvider The label provider; may be null
   * @param comparator the comparator; may be null
   * @param shellTitle The title shown on the shell; may be null
   * @param dialogTitle The title shown in the dialog; may be null
   * @param dialogMessage A message to display in the dialog; may be null
   * @param readOnly If true, then 
   */
  public CheckListDialog(Shell parentShell, List<E> choices,
                         ILabelProvider labelProvider, ViewerComparator comparator,
                         String shellTitle, String dialogTitle, String dialogMessage)
  {
    super(parentShell);

    this._choices = choices;

    this._labelProvider = labelProvider;
    this._comparator = comparator;

    this._shellTitle = shellTitle;
    this._dialogTitle = dialogTitle;
    this._dialogMessage = dialogMessage;
  }




  public void setSingleSelection(boolean singleSelection)
  {
    this.singleSelection_ = singleSelection;
  }


  public void setInitialSelection(Collection<E> initialSels)
  {
    initialSelections_.clear();
    if (initialSels != null) {
      initialSelections_.addAll(initialSels);
    }
  }

  public void setInitialSelection(E[] initialSelection)
  {
    this.initialSelections_ = Arrays.asList(initialSelection);
  }

  
  public void setInitialSelection(E selection)
  {
    initialSelections_.clear();
    initialSelections_.add(selection);
  }



  @Override
  protected Control createDialogArea(Composite parent)
  {
    if (StringUtils.isNotBlank(this._shellTitle)) {
      getShell().setText(this._shellTitle);
    }
    
    if (StringUtils.isNotBlank(this._dialogTitle)) {
      setTitle(this._dialogTitle);
    }
    
    if (StringUtils.isNotBlank(this._dialogMessage)) {
      setMessage(this._dialogMessage);
    }

    Composite composite =
        new Composite((Composite) super.createDialogArea(parent), SWT.NONE);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    
    
    Table table =
        new Table(composite, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
    table.setLinesVisible(true);
    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    //2012.05(kho): add column weighting to get rid of the extra phantom
    // column that was displaying
    TableColumn tc = new TableColumn(table, SWT.NONE);
    TableColumnLayout layout = new TableColumnLayout();
    layout.setColumnData(tc, new ColumnWeightData(100));
    composite.setLayout(layout);    
    
    
    viewer_ = new CheckboxTableViewer(table);
    

    
    viewer_.setContentProvider(ArrayContentProvider.getInstance());
    
    
    if (this._labelProvider != null) {
      viewer_.setLabelProvider(this._labelProvider);
    }
    if (this._comparator != null) {
      viewer_.setComparator(this._comparator);
    }
    
    
    viewer_.setInput(_choices);

    if (initialSelections_.size() > 0) {
      viewer_.setCheckedElements(initialSelections_.toArray());
    }

    table.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          if ((e.detail & SWT.CHECK) == 0) {
            return;
          }

          if (singleSelection_) {
              TableItem ti = (TableItem) e.item;
              if (ti.getChecked()) {
                viewer_.setAllChecked(false);
              }

              // either re-check if we just un-checked, or if the user
              // just unchecked, don't let them, make them check something else
              // instead
              ti.setChecked(true);
          }
          
          updateButtonEnablements();
        }
      });
    
    return parent;
  }
  
  /* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		Control contents = super.createContents(parent);
		
        updateButtonEnablements();
        
        return contents;
	}
  
  private void updateButtonEnablements()
  {
	  Object[] checked = viewer_.getCheckedElements();
	  int numChecked = checked!=null ? checked.length : 0;
	  int itemCount = viewer_.getTable().getItemCount();
	  
	  setButtonEnabled(IDialogConstants.OK_ID, singleSelection_ ? numChecked==1 : numChecked>0);
	  setButtonEnabled(IDialogConstants.SELECT_ALL_ID, numChecked<itemCount);
	  setButtonEnabled(IDialogConstants.DESELECT_ALL_ID, numChecked>0);
  }
  
  private void setButtonEnabled(int buttonID, boolean enabled)
  {
	  Button btn = getButton(buttonID);
	  if(btn!=null && !btn.isDisposed())
	  {
		  btn.setEnabled(enabled);
	  }
  }
  
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		if(!singleSelection_)
		{
			createButton(parent, IDialogConstants.SELECT_ALL_ID, SELECT_ALL_LABEL, false);
			createButton(parent, IDialogConstants.DESELECT_ALL_ID, SELECT_NONE_LABEL, false);
		}
		super.createButtonsForButtonBar(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId)
	{
		switch(buttonId)
		{
			case IDialogConstants.SELECT_ALL_ID:
				viewer_.setAllChecked(true);
				updateButtonEnablements();
				break;
			case IDialogConstants.DESELECT_ALL_ID:
				viewer_.setAllChecked(false);
				updateButtonEnablements();
				break;
			default:
				super.buttonPressed(buttonId);
				break;
		}
	}



  @Override
  protected Point getInitialSize()
  {
    // set a limit on the size of the dialog

    Table table = viewer_.getTable();
    Object layoutData = table.getLayoutData();
    if (layoutData instanceof GridData) {
      GridData gd = (GridData) layoutData;
      Point defaultSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);

      // stay in min/max bounds
      if (defaultSize.x < MIN_SIZE.x) defaultSize.x = MIN_SIZE.x;
      else if (defaultSize.x > MAX_SIZE.x) defaultSize.x = MAX_SIZE.x;
      if (defaultSize.y < MIN_SIZE.y) defaultSize.y = MIN_SIZE.y;
      else if (defaultSize.y > MAX_SIZE.y) defaultSize.y = MAX_SIZE.y;

      gd.widthHint = defaultSize.x;
      gd.heightHint = defaultSize.y;
    }

    return super.getInitialSize();
  }




  @SuppressWarnings("unchecked")
  @Override
  protected void okPressed()
  {
    result_ = new ArrayList<E>();

    Object[] checked = viewer_.getCheckedElements();

    if (checked != null && checked.length > 0) {
      for (Object obj : checked) {
        if (obj != null && _choices.contains(obj)) {
          result_.add((E) obj);
        }
      }
    }

    super.okPressed();
  }




  public List<E> getResult()
  {
    return result_;
  }
}
