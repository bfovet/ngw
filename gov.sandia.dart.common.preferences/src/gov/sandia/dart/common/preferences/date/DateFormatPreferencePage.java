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
 *
 * Copyright (C) 2005,2006
 *    
 *  All Rights Reserved
 *
 *  StrikeWire, LLC
 *  368 South McCaslin Blvd., #115
 *  Louisville, CO 80027
 *  (720) 890-8591
 *  support@strikewire.com
 *
 *  COMPANY PROPRIETARY
 *
 */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/*
 *
 *  $Author$
 *  $Date$
 *  
 * FILE: 
 *  $Source$
 *
 *
 * Description ($Revision$):
 *
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.common.preferences.date;

import gov.sandia.dart.common.preferences.CommonPreferencesPlugin;
import gov.sandia.dart.common.preferences.fields.LabelFieldEditor;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * @author kholson
 *
 */
public class DateFormatPreferencePage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage
{

  /**
   * 
   */
  public DateFormatPreferencePage()
  {
    super("Date Formats", GRID);
    setPreferenceStore(CommonPreferencesPlugin.getDefault().getPreferenceStore());
    setDescription("Date formatting preferences");
  }





  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
  @Override
  public void init(IWorkbench workbench)
  {
  }





  @Override
  protected void createFieldEditors()
  {
    StringFieldEditor sfe;

    Color errColor = JFaceColors.getErrorText(Display.getCurrent());    
    
    sfe = new StringFieldEditor(EDateFormats.DATEFORMAT_CLIENT.getPrefKey(),
        "General Format",
        getFieldEditorParent());
    
    addField(sfe);

    
    sfe = new StringFieldEditor(EDateFormats.DATEFORMAT_JOBS.getPrefKey(),
        "Job Submission Format",
        getFieldEditorParent());
    
    addField(sfe);
    
    
    addField(new LabelFieldEditor("Do not modify unless requested to do so by DART Administrators", errColor, getFieldEditorParent()));
    
    sfe = new StringFieldEditor(EDateFormats.DATEFORMAT_SERVER.getPrefKey(),
        "Server timestamp format",
        getFieldEditorParent());
    
    addField(sfe);

    MyTableFieldEditor mtfe = new MyTableFieldEditor(getFieldEditorParent());
    
    addField(mtfe);
  }
  
  private static class MyTableFieldEditor extends LabelFieldEditor
  {
	Table tbl;
	
	/**
	 * 
	 */
	public MyTableFieldEditor(Composite parent)
	{
	  super("Date format pattern letter definitions:", null, parent);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(int numColumns)
	{
		super.adjustForNumColumns(numColumns);
		
		if(this.tbl==null || this.tbl.isDisposed())
		{
			return;
		}
		
		((GridData) this.tbl.getLayoutData()).horizontalSpan = numColumns;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns)
	{
		super.doFillIntoGrid(parent, numColumns);
		
	    tbl = new Table(parent, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
	    tbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, numColumns, 1));
	    
        tbl.setHeaderVisible(true);
        tbl.setLinesVisible(true);
        
        
        TableColumn tc1 = new TableColumn(tbl, SWT.BEGINNING);
        tc1.setText("Letter");
        
        TableColumn tc2 = new TableColumn(tbl, SWT.BEGINNING);
        tc2.setText("Date or Time Component");
        
        TableColumn tc3 = new TableColumn(tbl, SWT.BEGINNING);
        tc3.setText("Examples");

        TableItem ti;
        

        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"G", "Era designator", "AD"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"y", "Year", "1996; 96"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"M", "Month in year", "July; Jul; 07"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] { "w", "Week in year", "27"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"W", "Week in month", "2"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"D", "Day in year", "189"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"d", "Day in month", "10"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"F", "Day of week in month", "2"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"E", "Day in week", "Tuesday; Tue"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"a", "Am/pm marker", "PM"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"H", "Hour in day (0-23)", "0"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"k", "Hour in day (1-24)", "24"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"K", "Hour in am/pm (0-11)", "0"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"h", "Hour in am/pm (1-12)", "12"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"m", "Minute in hour", "30"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"s", "Second in minute", "55"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"S", "Millisecond", "978"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"z", "Time zone", "Pacific Standard Time; PST; GMT-08:00"});
        
        ti = new TableItem(tbl, SWT.NONE);
        ti.setText(new String[] {"Z", "Time zone", "-0800"});
        
        tc1.pack();
        tc2.pack();
        tc3.pack();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	@Override
	protected void doLoad()
	{}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault()
	{}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore()
	{}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls()
	{
		return 1;
	}
  }

}
