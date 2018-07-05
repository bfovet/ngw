/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*---------------------------------------------------------------------------*/
/*
 *  Copyright (C) 2013
 *  Sandia National Laboratories
 *
 *  File originated by:
 *  StrikeWire, LLC
 *  149 South Briggs St., #102-A
 *  Erie, CO 80516
 *  (720) 890-8590
 *  support@strikewire.com
 *
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.table.utils;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TableColumn;

public class TableViewerColumnFactory
{
  private static final TableViewerColumnFactory _this =
      new TableViewerColumnFactory();
  
  
  private TableViewerColumnFactory()
  {
  }
  
  
  public static TableViewerColumnFactory getInstance()
  {
    return _this;
  }
  
  
  private void setMoveResizeAndListener(final TableColumn tc,
                                        final SelectionListener lsnr)
  {
    tc.setMoveable(true);
    tc.setResizable(true);
    
    if (lsnr != null) {
      tc.addSelectionListener(lsnr);
    }
  }
  
  private void setTableColumnLayoutWeight(TableColumn tc,
                                          TableColumnLayout tcLayout,
                                          int weight)
  {
    if (tcLayout != null && tc != null) {
      tcLayout.setColumnData(tc, new ColumnWeightData(weight));
    }
  }
  
  
  /**
   * <p>Creates a new tableviewercolumn on the specified tableViewer, setting
   * the column title to the .getTitle() value from the columnConst object,
   * and the weight to the .getWeight(). If the tcLayout is not null, then
   * sets the column data on the layout for the column and the weight data.</p>
   * 
   * <p>Sets the columns to moveable and resizable</p>
   * 
   * 
   * <p>If the selection listener is not null, adds it to the column of
   * the tableviewercolumn</p>
   * 
   */
  public TableViewerColumn makeTableViewerColumn(final TableViewer tableViewer,
                                           final ITableColumn columnConst,
                                           final TableColumnLayout tcLayout,
                                           final ColumnLabelProvider labelProvider,
                                           final SelectionListener lsnr)
  {
    return makeTableViewerColumn(tableViewer, columnConst, SWT.NONE,
        tcLayout,
        labelProvider,
        lsnr);
  }
  
  
  public TableViewerColumn makeTableViewerColumn(final TableViewer tableViewer,
                                                 final ITableColumn columnConst,
                                                 final int columnStyle,
                                                 final TableColumnLayout tcLayout,
                                                 final ColumnLabelProvider labelProvider,
                                                 final SelectionListener lsnr)
  {
    TableViewerColumn tvc = new TableViewerColumn(tableViewer, columnStyle);
    
    String title = columnConst.getTitle();
    int weight = columnConst.getWeight();
    
    tvc.getColumn().setText(title);
    
    setMoveResizeAndListener(tvc.getColumn(), lsnr);
    setTableColumnLayoutWeight(tvc.getColumn(), tcLayout, weight);
    
    if (labelProvider != null) {
      tvc.setLabelProvider(labelProvider);
    }
    
    return tvc;
  } //makeTableColumn
  
  
  
  public TableColumn makeTableColumn(final TableViewer tableViewer,
                                     final ITableColumn columnConst,
                                     final TableColumnLayout tcLayout,
                                     final SelectionListener lsnr)
  {
    TableColumn tc = new TableColumn(tableViewer.getTable(), SWT.NONE);
    
    String title = columnConst.getTitle();
    int weight = columnConst.getWeight();

    tc.setText(title);

    setMoveResizeAndListener(tc, lsnr);
    setTableColumnLayoutWeight(tc, tcLayout, weight);
    
    
    return tc;
  }

  
} //class TableViewerColumnFactory
