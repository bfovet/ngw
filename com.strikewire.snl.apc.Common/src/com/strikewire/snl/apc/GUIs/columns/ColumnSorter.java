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
 * Created by mjgibso on Aug 2, 2016 at 1:53:00 PM
 */
package com.strikewire.snl.apc.GUIs.columns;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IMemento;

/**
 * @author mjgibso
 *
 */
public class ColumnSorter extends ViewerSorter implements Comparator<Object>
{
	private final String _prefix;
	
	private LinkedList<SortColumn> _sortCols = new LinkedList<>();
	
	/**
	 * 
	 */
	public ColumnSorter(String prefix)
	{
		this._prefix = prefix;
	}
	
	public void initSorting(IPreferenceStore store)
	{
		initSorting(ColumnViewerUtils.getSortInfo(store, _prefix));
	}
	
	public synchronized void initSorting(String sortInfo)
	{
		_sortCols.clear();
		
		if(StringUtils.isBlank(sortInfo))
		{
			return;
		}
		
		String[] sortCols = sortInfo.split(",");
		for(String sortCol : sortCols)
		{
			String[] colParts = sortCol.split(":");
			if(colParts.length != 2)
			{
				//if the saved column data is not in the correct format, skip it
				continue;
			}
			
			String colName = colParts[0];
			
			int direction = Integer.parseInt(colParts[1]);
			
			if(StringUtils.isNotBlank(colName))
			{
				_sortCols.add(new SortColumn(colName, direction));
			}
		}
	}
	
	public synchronized void setSortColumn(AbstractColumnWrapper col, int index)
	{
		setSortColumn(col.getText(), index);
	}
	
	public synchronized void setSortColumn(String colName)
	{
		setSortColumn(colName, 0);
	}
	
	public synchronized void setSortColumn(String colName, int index)
	{
		if(_sortCols.size() < 1)
		{
			addFirstSortColumn(colName, index);
		} else {
			SortColumn top = _sortCols.getFirst();
			if(StringUtils.equals(top.columnName_, colName)) // if it's the same column, just switch the direction
			{
				top.direction_ = (top.direction_ & SWT.UP)==SWT.UP ? SWT.DOWN : SWT.UP;
			} else {// it's a new column, so push down the list
				addFirstSortColumn(colName, index);
			}
		}
	}
	
	private synchronized void addFirstSortColumn(String columnName, int index)
	{
		// first remove this column if it's already in the list so we won't have it twice
		_sortCols.removeIf(c -> StringUtils.equals(columnName, c.columnName_));
		_sortCols.addFirst(new SortColumn(columnName, SWT.UP, index));
	}
	
	public synchronized List<SortColumn> getSortColumns()
	{ return new ArrayList<SortColumn>(_sortCols); }
	
	public SortColumn getSortColumn()
	{
		List<SortColumn> sortColumns = getSortColumns();
		return sortColumns.size() > 0 ? sortColumns.get(0) : null;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object o1, Object o2)
	{
		return compare((Viewer) null, o1, o2);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public final int compare(Viewer viewer, Object o1, Object o2)
	{
//			System.out.println("Comparing: "+o1+" to "+o2);
        int cat1 = category(o1);
        int cat2 = category(o2);
        
        if (cat1 != cat2) {
//	        	System.out.println("  Cat1: "+cat1+", cat2: "+cat2);
			return cat1 - cat2;
		}     
		
		for(SortColumn sortCol : getSortColumns())
		{
			int result = compare(sortCol, o1, o2);
//				System.out.println("  Compare col: "+sortCol.columnName_+", dir: "+sortCol.direction_+", result: "+result);
			if(result != 0)
				return result;
		}
		
		return compareObjects(o1, o2);
	}
	
	protected int compare(SortColumn sortCol, Object o1, Object o2)
	{
		int result = 0;
		result = compare(sortCol.columnName_, o1, o2);
		
		if((sortCol.direction_ & SWT.DOWN) == SWT.DOWN)
		{
			result *= -1;
		}
		
		return result;
	}
	
	protected int compare(String columnName, Object o1, Object o2)
	{
		return compareObjects(o1, o2);
	}
	
	protected int compareObjects(Object o1, Object o2)
	{
		return compareStrings(stringValue(o1), stringValue(o2));
	}
	
	private static String stringValue(Object o)
	{
		return o!=null ? o.toString() : null;
	}
	
	@SuppressWarnings("unchecked")
	protected final int compareStrings(String s1, String s2)
	{
//			System.out.println("  String compare: "+s1+" to "+s2);
		if(s1 == null)
		{
			s1 = "";
		}
		
		if(s2 == null)
		{
			s2 = "";
		}
		
		return getComparator().compare(s1, s2);
	}
	
	private String generateSaveString(){
		StringBuilder sb = new StringBuilder();
		String save = "";
		for(SortColumn col : getSortColumns())
		{
			sb.append(col.columnName_);
			sb.append(':');
			sb.append(col.direction_);
			sb.append(',');
		}
		
//			System.out.println("was: "+sb.toString());
		if(sb.length() > 1)
			save = sb.substring(0, sb.length()-1);
//			System.out.println("now: "+save);
		return save;
	}
	
	synchronized void saveState(IPreferenceStore store)
	{
		store.setValue(_prefix + ColumnViewerUtils.SORT_INFO, generateSaveString());
	}
	
	synchronized void saveState(IMemento memento)
	{
		memento.putString(_prefix + ColumnViewerUtils.SORT_INFO, generateSaveString());
	}
	
	
}
