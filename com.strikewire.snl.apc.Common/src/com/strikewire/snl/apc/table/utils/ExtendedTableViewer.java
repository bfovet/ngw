/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/

package com.strikewire.snl.apc.table.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;

/**
 * <p>
 * When using the TableViewer, one adds a TableViewerColumn to it. However, once
 * added, there is now way to get to the:
 * </p>
 * <ul>
 * <li>TableViewerColumn objects</li>
 * <li>TableColumnLayout</li>
 * </ul>
 * 
 * After instantiating the tableviewer, it is often helpful to be able to get at
 * these elements. This class provides such capabilities.
 * 
 * @author kholson
 *
 */
public class ExtendedTableViewer
{
  /**
   * _tableViewer - The TableViewer
   */
  private final TableViewer _tableViewer;

  /**
   * _tcLayout - The Layout
   */
  private final TableColumnLayout _tcLayout = new TableColumnLayout();

  /**
   * A Map which holds the columns to a specified instance key = ITableColumn,
   * value = TableViewerColumn
   */
  private final CurrentColumns _columnsByName = new CurrentColumns();


  private final List<TableViewerColumn> _columns = new ArrayList<>();




  /**
   * <p>
   * Creates a TableViewer with the default style (SWT.MULTI | SWT.H_SCROLL |
   * SWT.V_SCROLL | SWT.BORDER) as defined in the TableViewer class.
   * </p>
   * <p>
   * Also sets the parent to have TableColumnLayout
   * </p>
   * 
   * @see TableViewer#TableViewer(Composite)
   */
  public ExtendedTableViewer(Composite parent)
  {
    _tableViewer = new TableViewer(parent);
    init(parent);
  }




  /**
   * Creates the basic TableViewer with the specified style on the specified
   * parent Composite. The style is the standard SWT.* bitwise operations as
   * valid for a TableViewer
   * 
   * @see TableViewer#TableViewer(Composite, int)
   */
  public ExtendedTableViewer(Composite parent, int style)
  {
    _tableViewer = new TableViewer(parent, style);
    init(parent);
  }


  /**
   * Creates the TableViewer by using the specified table; the table
   * must be created on a composite that will only contain the table,
   * and that can have the layout set to TableColumnLayout
   */
  public ExtendedTableViewer(Table tbl)
  {
    _tableViewer = new TableViewer(tbl);
    init(tbl.getParent());
  }

  /**
   * Adds an unnamed column with the specified style to the TableViewer; the
   * column is stored in the internal structure and may be accessed in returned
   * collection in the order it was added.
   */
  public TableViewerColumn addColumn(ExtTableViewerCfg cfg)
  {
    // create the column
    TableViewerColumn tvc = new TableViewerColumn(_tableViewer, cfg.getStyle());

    // add it to the list, which places in order
    _columns.add(tvc);
    
    ITableColumn itc = cfg.getColumnConst();
    
    if (itc != null) {
      String title = itc.getTitle();
      if (StringUtils.isNotBlank(title)) {
        _columnsByName.put(cfg.columnConst, tvc);

        tvc.getColumn().setText(title);
      }
    }
    
    
    if (cfg.getColLayoutData() != null) {
      _tcLayout.setColumnData(tvc.getColumn(), cfg.getColLayoutData());
    }
    else {
    
      int weight = (itc != null ? itc.getWeight() : 0);
      switch (cfg.getLayoutDataType()) {
        case WeigthData:
          _tcLayout.setColumnData(tvc.getColumn(), new ColumnWeightData(weight));
          break;
          
        case PixelData:
          _tcLayout.setColumnData(tvc.getColumn(), new ColumnPixelData(weight));
          break;
          
        case None:
          break;
      }
    }
    
    if (cfg.getColLabelProvider() != null) {
      tvc.setLabelProvider(cfg.getColLabelProvider());
    }
    
    if (cfg.getSelectionListener() != null) {
      tvc.getColumn().addSelectionListener(cfg.getSelectionListener());
    }
    
    tvc.getColumn().setResizable(cfg.getColResizeable());
    tvc.getColumn().setMoveable(cfg.getColMoveable());
    
    tvc.getColumn().setAlignment(cfg.getAlignment());

    return tvc;
  }




  public Collection<TableViewerColumn> getColumns()
  {
    return Collections.unmodifiableList(_columns);
  }




  public TableViewerColumn getColumn(ITableColumn name)
  {
    return _columnsByName.get(name);
  }




  public int getNumColumns()
  {
    return _columns.size();
  }




  public TableViewer getTableViewer()
  {
    return _tableViewer;
  }




  public Table getTable()
  {
    return _tableViewer.getTable();
  }




  public TableColumnLayout getLayout()
  {
    return _tcLayout;
  }




  /*
   * =========================================================================
   * Methods that pass to the TableViewer
   * =========================================================================
   */
  public void setContentProvider(IContentProvider provider)
  {
    _tableViewer.setContentProvider(provider);
  }




  public void setInput(Object input)
  {
    _tableViewer.setInput(input);
  }




  public void setComparator(ViewerComparator comparator)
  {
    _tableViewer.setComparator(comparator);
  }


  
  public ViewerComparator getComparator()
  {
    return _tableViewer.getComparator();
  }
  
  
  public void refresh()
  {
    _tableViewer.refresh();
  }

  
  public Control getControl()
  {
    return _tableViewer.getControl();
  }
  

  /*
   * =========================================================================
   * Private methods
   * =========================================================================
   */
  private void init(Composite parent)
  {
    parent.setLayout(_tcLayout);
  }


  /*
   * =========================================================================
   * Public classes (configuration)
   * =========================================================================
   */
  /**
   * <p>
   * A configuration class for adding a table column.
   * </p>
   * <p>
   * Need to set: label provider, layoutDataType;
   * </p>
   * <ul>
   * <li>columnConst (ITableColumn): null</li>
   * <li>style : SWT.NONE</li>
   * <li>colLabelProvider (ColumnLabelProvider): null</li>
   * <li>selectionLsnr (SelectionListener): null</li>
   * <li>layoutDataType (EColumnLayoutData) : None;
   * <li>weightOrPixels : may be set directly, or will be set
   * if the columnConst is specified</li>
   * <li>colResizeable : false</li>
   * <li>colMoveable : false</li>
   * <li>colLayoutData : null</li>
   * </ul>
   * 
   * @author kholson
   *
   */
  public static class ExtTableViewerCfg
  {
    private ITableColumn columnConst = null;
    private int style = SWT.NONE;
    private ColumnLabelProvider colLabelProvider = null;
    private SelectionListener selectionLsnr = null;
    private EColumnLayoutData layoutDataType = EColumnLayoutData.None;
    private int weightOrPixels = -1;
    private boolean colResizable = false;
    private boolean colMoveable = false;
    private ColumnLayoutData colLayoutData = null;
    private int alignment = SWT.LEFT;
    
    
    /**
     * Sets the alignment for the column; should
     * be SWT.LEFT, SWT.RIGHT, SWT.CENTER, etc.
     */
    public ExtTableViewerCfg setAlignment(int align)
    {
      alignment = align;
      return this;
    }
    
    
    public int getAlignment()
    {
      return alignment;
    }
    
    
    public ExtTableViewerCfg setColLayoutData(ColumnLayoutData cld)
    {
      colLayoutData = cld;
      return this;
    }
    
    public ColumnLayoutData getColLayoutData()
    {
      return colLayoutData;
    }
    
    public ExtTableViewerCfg setColResizeable(boolean r)
    {
      colResizable = r;
      return this;
    }
    
    public ExtTableViewerCfg setColMoveable(boolean m)
    {
      colMoveable = m;
      return this;
    }
    
    public boolean getColResizeable()
    {
      return colResizable;
    }
    
    public boolean getColMoveable()
    {
      return colMoveable;
    }
    
    

    public ExtTableViewerCfg setWeightOrPixels(int size)
    {
      weightOrPixels = size;
      
      return this;
    }
    
    /**
     * Returns the weight or pixels associated with this configuration;
     * if not set explicitly, then will use the value in the 
     * columnConst if columnConst != null; will return 0 if
     * not set and columnCost == null.
     */
    public int getWeightOrPixels()
    {
      int ret = weightOrPixels;
      
      if (ret < 0 && columnConst != null) {
        ret = columnConst.getWeight();
      }
      
      return (ret >= 0 ? ret : 0);
    }
    

    /**
     * @return the columnConst
     */
    public ITableColumn getColumnConst()
    {
      return columnConst;
    }




    /**
     * @param columnConst
     *          the columnConst to set
     */
    public ExtTableViewerCfg setColumnConst(ITableColumn columnConst)
    {
      this.columnConst = columnConst;
      return this;
    }




    /**
     * @return the style
     */
    public int getStyle()
    {
      return style;
    }




    /**
     * @param style
     *          the style to set
     */
    public ExtTableViewerCfg setStyle(int style)
    {
      this.style = style;
      return this;
    }




    /**
     * @return the colLabelProvider
     */
    public ColumnLabelProvider getColLabelProvider()
    {
      return colLabelProvider;
    }




    /**
     * @param colLabelProvider
     *          the colLabelProvider to set
     */
    public ExtTableViewerCfg setColLabelProvider(ColumnLabelProvider colLabelProvider)
    {
      this.colLabelProvider = colLabelProvider;
      return this;
    }




    /**
     * @return the selectionListener; may be null
     */
    public SelectionListener getSelectionListener()
    {
      return selectionLsnr;
    }




    /**
     * @param selectionLsnr
     *          the selectionLsnr
     */
    public ExtTableViewerCfg setSelectionListener(SelectionListener selectionLsnr)
    {
      this.selectionLsnr = selectionLsnr;
      return this;
    }




    /**
     * @return the layoutDataType
     */
    public EColumnLayoutData getLayoutDataType()
    {
      return layoutDataType;
    }




    /**
     * @param layoutDataType
     *          the layoutDataType to set
     */
    public ExtTableViewerCfg setLayoutDataType(EColumnLayoutData layoutDataType)
    {
      this.layoutDataType = layoutDataType;
      return this;
    }



  }

  public enum EColumnLayoutData
  {
    PixelData, WeigthData, None;
  }


  /*
   * =========================================================================
   * Private classes
   * =========================================================================
   */

  /**
   * <p>
   * A class for tracking which columns are current used.
   * </p>
   * 
   * @author kholson
   *
   */
  private static class CurrentColumns extends
      HashMap<ITableColumn, TableViewerColumn>
  {

    /**
     * serialVersionUID -
     */
    private static final long serialVersionUID = 3010094376878644837L;
  }
}
