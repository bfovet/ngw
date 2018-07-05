/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.GUIs.columns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.GUIs.ColumnPropertiesDialog;
import com.strikewire.snl.apc.util.IColumnViewerHelperInfo;


/**
 * This class aims to facilitate constructing and saving views with TableViewers.
 * 
 * @author ejranst
 * @author mjgibso
 */
public class ColumnViewerUtils
{
	public static String COLUMN_INFO = ".ColumnInfo";
	public static String SORT_INFO = ".SortInfo";
	
	private static final int MIN_COLUMN_WIDTH = 30;
	
	private final TreeViewer _viewer;
	private final Tree _tree;
	private final TreeColumnSorter _sorter;
	
	private final IPreferenceStore _prefs;
	private final String _prefix;
	
	private final IColumnViewerHelperInfo _info;
	
	public ColumnViewerUtils(TreeViewer viewer, TreeColumnSorter sorter, IPreferenceStore prefs, String prefix, IColumnViewerHelperInfo info)
	{
		this._viewer = viewer;
		this._tree = viewer.getTree();
		this._sorter = sorter;
		
		this._prefs = prefs;
		this._prefix = prefix;
		
		this._info = info;
	}
	
	/**
	 * This function accepts a memento from a view with a TreeViewer
	 * and saves a string containing the visible column names, their
	 * widths, and the sort column and direction on the received memento.
	 * @param memento The memento to save the column data on
	 * @param viewer The tree viewer for which to save the visible column data
	 * @param sorter 
	 * @param prefix Column data will be saved on the memento under the key "prefix + COLUMN_INFO"
	 * @see com.strikewire.snl.apc.GUIs.columns.ColumnViewerUtils Look Here for definition of <code>COLUMN_INFO</code>
	 */
    public void saveState(IMemento memento)
    {
	    //write column data to memento
	    memento.putString(_prefix+COLUMN_INFO, generateSaveString());
	    
	    saveSortInfo(memento);
    }
    
	public void savePreferenceData()
	{
		// save column info
    	_prefs.putValue(_prefix+COLUMN_INFO, generateSaveString());
    	
    	saveSortInfo();
	}
	
	public void saveSortInfo(IMemento memento)
	{
		_sorter.saveState(memento);
	}
	
	public void saveSortInfo()
	{
		_sorter.saveState(_prefs);
	}
	
	private String generateSaveString()
	{
		StringBuilder colsb = new StringBuilder();
		
		int[] order = _tree.getColumnOrder();
		
		//iterate over the visible columns
		for(int i=0; i<order.length; i++)
		{
			int index = order[i];
			
			//name
			colsb.append(_tree.getColumn(index).getText());
			colsb.append('~');
			//width
			colsb.append(_tree.getColumn(index).getWidth());
			colsb.append('~');
			//order
			colsb.append(i);
			
			colsb.append(';'); //separate sets of column data by ";"
		}
		
		return colsb.length() > 0 ? colsb.substring(0, colsb.length()-1) : "";
	}
		
	/**
     * This function will load the passed in TreeViewer with the state saved on the memento.
     * @param memento The memento on which the TreeViewer data is stored
     * @param viewer the TreeViewer which to load with the data from the memento
     * @param prefix The stored TreeViewer data is assumed to be at under the key "prefix + COLUMN_INFO"
     * @param sorter A ColumnSorter that will be registered with each column that is added to the TreeViewer
     * @param store The preference store in which to look for stored TreeViewer data in the event that loading
     * from the memento is unsuccessful.
     * @see com.strikewire.snl.apc.GUIs.columns.ColumnViewerUtils Look Here for definition of <code>COLUMN_INFO</code>
     */
    public void loadState(IMemento memento)
    {
    	if(memento == null)
    	{
    		return;
    	}
    	
		// get saved column info
	    String columnData =  memento.getString(_prefix+COLUMN_INFO);//Get the column data string from the preference store
	    
	    if(StringUtils.isBlank(columnData))
	    {
	    	loadPreferenceData();
	    	return;
	    }
	    
		String sortInfo = memento.getString(_prefix + SORT_INFO);
		
	    loadState(columnData, sortInfo);
    }
    
	/**
     * 
     */
	public void loadPreferenceData() 
	{
		// get saved column info
	    String columnData =  _prefs.getString(_prefix+COLUMN_INFO);
	    
	    if(StringUtils.isBlank(columnData))
	    {
	    	columnData = _prefs.getDefaultString(_prefix + COLUMN_INFO);
	    }
	    
	    if(StringUtils.isBlank(columnData))
	    {
	    	return;
	    }
	    
		String sortInfo = getSortInfo(_prefs, _prefix);
		
		loadState(columnData, sortInfo);
	}
	
	static String getSortInfo(IPreferenceStore prefs, String prefix)
	{
		return prefs.getString(prefix + SORT_INFO);
	}
	
    private void loadState(String columnData, String sortInfo)
    {
    	try {
    	    List<ColumnInfo> columnInfos = parseColumnInfo(columnData);
    	    
    		//get the names of all columns to be displayed
    	    String[] visibleColumns = new String[columnInfos.size()];
    	    for(int i=0; i<visibleColumns.length; i++)
    	    {
    	    	visibleColumns[i] = columnInfos.get(i).getColumnName();
    	    }
    	    
    		//set visible columns	
    		_viewer.setColumnProperties(visibleColumns);
    		
    		//for each column
    		for(ColumnInfo column : columnInfos)
    		{
    			//generate a new TreeColumn
    			createColumn(column.getColumnName(), column.getWidth());
    		}
    		
    		_sorter.initSorting(sortInfo);
    	} catch (Throwable t) {
    		CommonPlugin.getDefault().logError("Error loading column information", t);
    	}
	}
    
	private List<ColumnInfo> parseColumnInfo(String columnData)
	{
		// An array list to hold retrieved column information
		List<ColumnInfo> columnInfo_ = new ArrayList<ColumnInfo>();
		
		// keep a list of names we've already seen to avoid adding duplicates
		Set<String> seenNames = new HashSet<String>();

		// It is assumed column entries are separated by semicolons
		String[] columns = columnData.split(";");
		for(String column : columns)
		{
			// It is assumed that data elements for a column are separated by tildes
			String[] colParts = column.split("~");
			ColumnInfo colInfo = new ColumnInfo();

			// Write the column data into a ColumnInformation object
			colInfo.setColumnName(colParts[0]);
			colInfo.setWidth(Integer.parseInt(colParts[1]));
			colInfo.setOrder(Integer.parseInt(colParts[2]));

			if(seenNames.add(colInfo.getColumnName()))
			{
				columnInfo_.add(colInfo);
			}
		}
		
		return columnInfo_;
	}
	
	private void createColumn(final String name, int width)
	{
		// generate a new TreeColumn (have to save and restore the expanded
		// state ourselves (see DTA-8475)
		Object[] expanded = _viewer.getExpandedElements();
		final TreeColumn col = new TreeColumn(_tree, SWT.NONE);
		_viewer.setExpandedElements(expanded);
		
		//assign the tree column properties from the ColumnInfo object
		col.setText(name);
		col.pack();
		
		// treat required columns as not movable
		boolean movable = !_info.getRequiredColumnNames().contains(name);
		
		col.setMoveable(movable);
		
		col.setWidth(width);
		
		col.addListener(SWT.Resize, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				if(col.getWidth() < 25)
				{
					col.setWidth(25);
				}
			}
		});
		
		//Add a selection adapter to the column to set it as the sortColumn
		col.addSelectionListener(new SelectionAdapter()
		{
		
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.widget==null || !(e.widget instanceof TreeColumn))
				{
					return;
				}
				
				TreeColumn newSortCol = (TreeColumn) e.widget;
				_sorter.setSortColumn(newSortCol);
			}
		
		});
		
		if(!movable)
		{
			// Treat required columns as not movable.  Just setting the not movable attribute
			// of the required column isn't enough.  User could drag a non-required, movable column
			// in front of the required non-movable column, effectively moving it.  Simply listen
			// for this to occur, and restore the required column(s) as the first one(s).
			col.addListener(SWT.Move, new Listener() {
				
				@Override
				public void handleEvent(Event event) {
					
					Set<String> orderedColumnNames = new LinkedHashSet<String>();
					
					orderedColumnNames.addAll(_info.getRequiredColumnNames());
					orderedColumnNames.addAll(Arrays.asList(getColumnNamesInOrder()));
					
					refreshViewer(new ArrayList<String>(orderedColumnNames));
				}
			});
		}
	}
	
	private String[] getColumnNamesInOrder()
	{
		TreeColumn[] cols = _tree.getColumns();
		int[] order = _tree.getColumnOrder();
		String[] colNames = new String[cols.length];
		for(int i=0; i<cols.length; i++)
		{
			colNames[i] = cols[order[i]].getText();
		}
		return colNames;
	}
	
	public int openColumnPreferencesDialog(Shell shell)
	{
		return openColumnPreferencesDialog(shell, false);
	}
	
	public int openColumnPreferencesDialog(Shell shell, boolean getCurrentFromPrefs)
	{
		Collection<String> reqCols = _info.getRequiredColumnNames();
		
		Collection<String> defaultVisCols = getVisibleDefaultColumns();
		defaultVisCols.removeAll(reqCols);
		
		Collection<String> defaultNonVisCols = getHiddenDefaultColumns();
		defaultNonVisCols.removeAll(reqCols);
		
		List<String> nonReqVisCols = getCurrentFromPrefs ? getColumnsFromPreferences() : getVisibleColumns();
		nonReqVisCols.removeAll(reqCols);
		
		Collection<String> allNonReqCols = new ArrayList<String>(_info.getAllColumnNames());
		allNonReqCols.removeAll(reqCols);
		
		boolean allowNoVisible = _info.getRequiredColumnNames().size() > 0;
		
		ColumnPropertiesDialog dialog = new ColumnPropertiesDialog(shell, allNonReqCols, nonReqVisCols, defaultVisCols, defaultNonVisCols, allowNoVisible);
		int result = dialog.open();
		if(result == Window.OK)
		{
			List<String> cols = new ArrayList<String>(dialog.getVisibleColumns());
			cols.addAll(0, _info.getRequiredColumnNames());
			
			refreshViewer(cols);
		}
		
		return result;
	}
	
	public List<String> getColumnsFromPreferences()
	{
		try {
			String columnData = _prefs.getString(_prefix+COLUMN_INFO);
			if(StringUtils.isBlank(columnData))
			{
				return getVisibleColumns();
			}
		
			List<ColumnInfo> columnInfos = parseColumnInfo(columnData);
			if(columnInfos==null || columnInfos.size()<1)
			{
				return getVisibleColumns();
			}
			
			List<String> columns = new ArrayList<String>(columnInfos.size());
			for(ColumnInfo info : columnInfos)
			{
				columns.add(info.getColumnName());
			}
			
			return columns;
		} catch(Throwable t) {
			IStatus warning = CommonPlugin.getDefault().newWarningStatus("Error retrieving or parsing persisted column information." +
					"  Resorting to using columns currently displayed in the viewer instead.", t);
			CommonPlugin.getDefault().log(warning);
			return getVisibleColumns();
		}
	}
	
	private List<String> getVisibleColumns()
	{
		List<String> columns = new ArrayList<String>();
		
		int count = _tree.getColumnCount();
		int[] order = _tree.getColumnOrder();
		
		for(int i=0; i<count; i++)
		{
			int index = order[i];
			TreeColumn col = _tree.getColumn(index);
			String colName = col.getText();
			int colWidth = col.getWidth();
			if(StringUtils.isNotBlank(colName) && colWidth>0)
			{
				columns.add(colName);
			}
		}
		
		return columns;
	}
	
	private List<String> getVisibleDefaultColumns()
	{
		// get saved column info
	    List<String> defaultColumns = new ArrayList<String>();
	    String columnData =  _prefs.getDefaultString(_prefix+COLUMN_INFO);
	    
		String[] columns = columnData.split(";");
		for(String column : columns)
		{
			String[] colParts = column.split("~");
			defaultColumns.add(colParts[0]);
		}
		
		return defaultColumns;
	}
	
	private Collection<String> getHiddenDefaultColumns()
	{
		Collection<String> hiddenDefCols = new ArrayList<String>(_info.getAllColumnNames());
		hiddenDefCols.removeAll(getVisibleDefaultColumns());
	
		return hiddenDefCols;
	}
	
	private void refreshViewer(List<String> visibleColumns)
	{
		ArrayList<String> colsToAdd = new ArrayList<String>(visibleColumns);
		
		//Update columns currently on the tree
		for(TreeColumn tc : _tree.getColumns())
		{
			String colName = tc.getText();
			if(visibleColumns.contains(tc.getText()))
			{
				// backwards compatibility: we used to just set sizes to 0 rather than actually disposing them
				if(tc.getWidth() == 0)
				{
					tc.setWidth(100);
					tc.setResizable(true);
				}
				
				if(colsToAdd.contains(colName))
				{
					colsToAdd.remove(colName); //remove added column from list of columns that need to be added
				}
			}
			else{
				tc.dispose();
			}
		}
		
		//Add new columns to the tree if necessary
		for(String name:colsToAdd)
		{
			createColumn(name, 100);
		}
		
		//Adjust order of the tree
		TreeColumn[] cols = _tree.getColumns();
		Map<String, Integer> columnMap = new HashMap<String, Integer>(cols.length);
		for(int i=0; i<cols.length; i++)
		{
			columnMap.put(cols[i].getText(), i);
		}
		int[] newOrder = new int[cols.length];
		for(int i=0; i<newOrder.length; i++)
		{
			newOrder[i] = columnMap.get(visibleColumns.get(i));
		}
		_tree.setColumnOrder(newOrder);
		
		_viewer.setColumnProperties(visibleColumns.toArray(new String[visibleColumns.size()]));

		_viewer.refresh(true);
		_sorter.updateSortColumn();
	}
	
	/**
	 * A class to maintain the layout information for a tree viewer of tree columns.
	 * When column data is loaded from a memento or from the preferene store,
	 * it is first loaded into ColumnInfo objects, and then the Column Info objects are used
	 * to create the view.  Objects of this class are intended to be transitory only. 
	 */
	static class ColumnInfo
	{
		private String columnName_;
		private String columnId_;
		private int order_;
		private int width_;
		
		public ColumnInfo(){}
		public ColumnInfo(String columnName, String columnId, int order, int width){
			this.columnName_=columnName;
			this.columnId_=columnId;
			this.order_=order;
			setWidth(width);
		}
        public void setColumnName(String columnName){this.columnName_=columnName;}	
        public void setColumnId(String columnId){this.columnId_=columnId;}	
        public void setOrder(int order){this.order_=order;}	
        public void setWidth(int width){
        	width_ = (width > MIN_COLUMN_WIDTH) ? width : MIN_COLUMN_WIDTH;
        }
        public String getColumnName(){return columnName_;}
        public String getColumnId(){return columnId_;}
        public int getOrder(){return order_;}
        public int getWidth(){return width_;}	
	}
}
