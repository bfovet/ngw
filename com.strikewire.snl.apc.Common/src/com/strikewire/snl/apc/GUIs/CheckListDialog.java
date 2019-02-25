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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.table.utils.ExtendedTableViewer.ExtTableViewerCfg;
import com.strikewire.snl.apc.table.utils.ITableColumn;

/**
 * <p>
 * A dialog which provides the ability to display a range of choices, with a
 * check box in front of them. May specify single selection.
 * </p>
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


  private final ICheckListDialogCfg<E> dlgConfig;

  private CheckboxTableViewer _viewer;

  private boolean _singleSelection = false;

  private final List<E> _initialSelections = new ArrayList<>();

  private final List<E> _result = new ArrayList<>();




  /**
   * @param parentShell
   *          The shell
   * @param choices
   *          The choices to show to the use
   * @param labelProvider
   *          The label provider; may be null
   * @param shellTitle
   *          The title shown on the shell; may be null
   * @param dialogTitle
   *          The title shown in the dialog; may be null
   * @param dialogMessage
   *          A message to display in the dialog; may be null
   * @param readOnly
   *          If true, then
   * @deprecated Use CheckListDialog(CheckListDialogCfg)
   */
  @Deprecated
  public CheckListDialog(Shell parentShell, List<E> choices,
                         ILabelProvider labelProvider, String shellTitle,
                         String dialogTitle, String dialogMessage)
  {
    this(parentShell,
        choices,
        labelProvider,
        null,
        shellTitle,
        dialogTitle,
        dialogMessage);
  }




  /**
   * @param parentShell
   *          The shell
   * @param choices
   *          The choices to show to the use
   * @param labelProvider
   *          The label provider; may be null
   * @param comparator
   *          the comparator; may be null
   * @param shellTitle
   *          The title shown on the shell; may be null
   * @param dialogTitle
   *          The title shown in the dialog; may be null
   * @param dialogMessage
   *          A message to display in the dialog; may be null
   * @param readOnly
   *          If true, then
   */
  public CheckListDialog(Shell parentShell, List<E> choices,
                         ILabelProvider labelProvider,
                         ViewerComparator comparator, String shellTitle,
                         String dialogTitle, String dialogMessage)
  {
    super(parentShell);

    CheckListDialogCfg.Builder<E> bldr = new CheckListDialogCfg.Builder<>();
    bldr.setParentShell(parentShell)
        .setChoices(choices)
        .setLabelProvider(labelProvider)
        .setComparator(comparator)
        .setShellTitle(shellTitle)
        .setDialogTitle(dialogTitle)
        .setDialogMessage(dialogMessage);

    dlgConfig = bldr.build();
  }




  /**
   * Constructor that accepts a configuration
   * 
   * @param cfg
   *          The configuration for the dialog; may not be null
   */
  public CheckListDialog(ICheckListDialogCfg<E> cfg)
  {
    super(cfg.getParentShell());

    dlgConfig = cfg;
  }




  public void setSingleSelection(boolean singleSelection)
  {
    this._singleSelection = singleSelection;
  }




  public void setInitialSelection(Collection<E> initialSels)
  {
    _initialSelections.clear();
    if (initialSels != null) {
      _initialSelections.addAll(initialSels);
    }
  }




  public void setInitialSelection(E[] initialSelection)
  {
    this._initialSelections.addAll(Arrays.asList(initialSelection));
  }




  public void setInitialSelection(E selection)
  {
    _initialSelections.clear();
    _initialSelections.add(selection);
  }




  @Override
  protected Control createDialogArea(Composite parent)
  {
    setDlgTextInfo(dlgConfig);

    setDlgImage(dlgConfig);

    //
    // create a composite and set the grid data on it
    //
    Composite composite =
        new Composite((Composite) super.createDialogArea(parent), SWT.NONE);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));


    //
    // if we do not have table viewer configuration, create
    //
    if (dlgConfig.getTableViewerCfgs() == null
        || dlgConfig.getTableViewerCfgs().isEmpty()) {
      Table table = createSingleColumnTable(composite);

      _viewer = new CheckboxTableViewer(table);

      if (dlgConfig.getLabelProvider() != null) {
        _viewer.setLabelProvider(dlgConfig.getLabelProvider());
      }
      if (dlgConfig.getComparator() != null) {
        _viewer.setComparator(dlgConfig.getComparator());
      }

    } // if: building single column table
    else {
      Table table = createTableOnComposite(composite);

      table.setHeaderVisible(true);

      //
      // we want a TableColumnLayout and set it on the composite
      //
      TableColumnLayout layout = new TableColumnLayout();
      composite.setLayout(layout);


      _viewer = new CheckboxTableViewer(table);
      createColumns(_viewer, layout, dlgConfig);
    } // else : building based upon (presumably) multiple columns


    //
    // set the content provider; a simply array content provider
    //
    _viewer.setContentProvider(ArrayContentProvider.getInstance());


    //
    // set the various choices
    //
    _viewer.setInput(dlgConfig.getChoices());


    //
    // if there are any initial selections, set them
    if (_initialSelections.size() > 0) {
      _viewer.setCheckedElements(_initialSelections.toArray());
    }

    //
    // we wish to handle check selections, so add selection listener
    //
    _viewer.getTable().addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        if ((e.detail & SWT.CHECK) == 0) {
          return;
        }

        if (_singleSelection) {
          TableItem ti = (TableItem) e.item;
          if (ti.getChecked()) {
            _viewer.setAllChecked(false);
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




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.
   * widgets.Composite)
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
    Object[] checked = _viewer.getCheckedElements();
    int numChecked = checked != null ? checked.length : 0;
    int itemCount = _viewer.getTable().getItemCount();

    setButtonEnabled(IDialogConstants.OK_ID,
        _singleSelection ? numChecked == 1 : numChecked > 0);
    setButtonEnabled(IDialogConstants.SELECT_ALL_ID, numChecked < itemCount);
    setButtonEnabled(IDialogConstants.DESELECT_ALL_ID, numChecked > 0);
  }




  private void setButtonEnabled(int buttonID, boolean enabled)
  {
    Button btn = getButton(buttonID);
    if (btn != null && !btn.isDisposed()) {
      btn.setEnabled(enabled);
    }
  }




  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse
   * .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    if (!_singleSelection) {
      createButton(parent,
          IDialogConstants.SELECT_ALL_ID,
          SELECT_ALL_LABEL,
          false);
      createButton(parent,
          IDialogConstants.DESELECT_ALL_ID,
          SELECT_NONE_LABEL,
          false);
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
    switch (buttonId) {
      case IDialogConstants.SELECT_ALL_ID:
        _viewer.setAllChecked(true);
        updateButtonEnablements();
        break;
      case IDialogConstants.DESELECT_ALL_ID:
        _viewer.setAllChecked(false);
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

    Table table = _viewer.getTable();
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
    Object[] checked = _viewer.getCheckedElements();

    if (checked != null && checked.length > 0) {
      for (Object obj : checked) {
        if (obj != null && dlgConfig.getChoices().contains(obj)) {
          _result.add((E) obj);
        }
      }
    }

    super.okPressed();
  }




  /**
   * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
   */
  @Override
  protected void cancelPressed()
  {
    super.cancelPressed();
  }




  public List<E> getResult()
  {
    return _result;
  }




  /**
   * Sets the shell and dialog titles, and the dialog message from the
   * configuration; only sets if the information is not blank
   */
  private void setDlgTextInfo(ICheckListDialogCfg<E> cfg)
  {
    if (StringUtils.isNotBlank(cfg.getShellTitle())) {
      getShell().setText(cfg.getShellTitle());
    }

    if (StringUtils.isNotBlank(cfg.getDialogTitle())) {
      setTitle(cfg.getDialogTitle());
    }

    if (StringUtils.isNotBlank(cfg.getDialogMessage())) {
      setMessage(cfg.getDialogMessage());
    }
  }




  private void setDlgImage(ICheckListDialogCfg<E> cfg)
  {
    final String key = this.getClass().getCanonicalName();

    if (cfg.getShellImage() != null) {
      ImageDescriptor imgDscr = cfg.getShellImage();

      ImageRegistry imgRgstry = CommonPlugin.getDefault().getImageRegistry();

      Image img = imgRgstry.get(key);

      if (img == null) {
        imgRgstry.put(key, imgDscr);
        img = imgRgstry.get(key);
      }

      getShell().setImage(img);
    }
  }




  /**
   * Creates a table
   */
  private Table createTableOnComposite(Composite composite)
  {
    Table table =
        new Table(composite, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
    table.setLinesVisible(true);
    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    return table;
  }




  /**
   * Creates a new table on the specified composite, with the layout data for
   * grid data and full fill, and sets the layout on the specified composite to
   * a TableColumnLayout
   */
  private Table createSingleColumnTable(Composite composite)
  {
    Table table = createTableOnComposite(composite);


    // 2012.05(kho): add column weighting to get rid of the extra phantom
    // column that was displaying
    TableColumn tc = new TableColumn(table, SWT.NONE);
    TableColumnLayout layout = new TableColumnLayout();
    layout.setColumnData(tc, new ColumnWeightData(100));
    composite.setLayout(layout);

    return table;
  }




  private void createColumns(TableViewer viewer,
                             TableColumnLayout layout,
                             ICheckListDialogCfg<E> dlgConfig)
  {
    //
    // now we need to create the table columns, with the layout info
    //
    for (ExtTableViewerCfg cfg : dlgConfig.getTableViewerCfgs()) {
      TableViewerColumn tvc = new TableViewerColumn(viewer, cfg.getStyle());

      // set the title
      ITableColumn itc = cfg.getColumnConst();
      if (itc != null) {
        if (StringUtils.isNotBlank(itc.getTitle())) {
          tvc.getColumn().setText(itc.getTitle());
        }
      }

      // if we have specific layout data, use it, otherwise set
      // based upon weight or pixel
      if (cfg.getColLayoutData() != null) {
        layout.setColumnData(tvc.getColumn(), cfg.getColLayoutData());
      }
      else {
        int weight = (itc != null ? itc.getWeight() : 0);
        switch (cfg.getLayoutDataType()) {
          case WeigthData:
            layout.setColumnData(tvc.getColumn(), new ColumnWeightData(weight));
            break;

          case PixelData:
            layout.setColumnData(tvc.getColumn(), new ColumnPixelData(weight));
            break;

          case None:
            break;
        }
      } // else: set column based upon weight

      // set label provider for the column, if present
      if (cfg.getColLabelProvider() != null) {
        tvc.setLabelProvider(cfg.getColLabelProvider());
      }


      // set resizable/moveable/alignment
      tvc.getColumn().setResizable(cfg.getColResizeable());
      tvc.getColumn().setMoveable(cfg.getColMoveable());
      tvc.getColumn().setAlignment(cfg.getAlignment());
    } // for : process all columns

  }
}
