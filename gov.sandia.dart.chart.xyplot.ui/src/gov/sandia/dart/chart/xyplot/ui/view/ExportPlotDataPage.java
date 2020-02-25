/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.chart.xyplot.ui.view;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.strikewire.snl.apc.validation.IValidatablePage;

/**
 * @author mjgibso
 *
 */
public class ExportPlotDataPage extends WizardPage implements IValidatablePage
{
	private Button browseButton;
	private Text location;
	private Combo whichCombo;
	
	/**
	 * 
	 */
	public ExportPlotDataPage()
	{
		super(ExportPlotDataPage.class.getSimpleName());
		setTitle("Export Data");
		
		setDescription("Export the data from this table to a delimited file");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent)
	{
		initializeDialogUnits(parent);
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		setControl(composite);
		
		composite.setLayout(new GridLayout(3, false));
				
		// create the browse button
		Label browseL = new Label(composite, SWT.NONE);
		browseL.setText("Path for exported file:");
		browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText("Browse");
		location = new Text(composite, SWT.BORDER);
		GridData locationData = new GridData(GridData.FILL_HORIZONTAL);
		location.setLayoutData(locationData);		
		
		Label whichL = new Label(composite, SWT.NONE);
		whichL.setText("Export format:");
		whichCombo = new Combo(composite, SWT.READ_ONLY);
		// This monstrosity returns a String arrqy containing the names of the enum elements.
		String[] names = Arrays.asList(PlotDataExporter.FORMAT.values()).stream().map(PlotDataExporter.FORMAT::toString).toArray(String[]::new);
		whichCombo.setItems(names);
		whichCombo.select(0);
		
		ModifyListener listener = new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent e) {
				validatePage();
			}

		};
		location.addModifyListener(listener);		
		
		browseButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(parent.getShell(), SWT.SAVE);	
				dialog.setFilterExtensions(new String[] {"*.csv", "*.tsv"});
				dialog.setFileName("");
				String newLoc = dialog.open();
				if(StringUtils.isNotBlank(newLoc)) {
					location.setText(newLoc);
				}
			}
		});
		
		validatePage();
	}
	
	/**
     * Returns whether this page's controls currently all contain valid 
     * values.
     *
     * @return <code>true</code> if all controls are valid, and
     *   <code>false</code> if at least one is invalid
     */
	@Override
	public boolean validatePage()
	{
    	setMessage(null);
    	boolean isValid = true;
    	String path = location.getText();
    	if (StringUtils.isBlank(path)) {
    		isValid = false;
    	} else {
    		File f = new File(path);
    		if (!f.getParentFile().exists())
    			isValid = false;
    	}
    	setPageComplete(isValid);
    	return isValid;
    }	
	
	protected String getLocation()
	{
		return location!=null && !location.isDisposed() ? location.getText() : "";
	}

	public PlotDataExporter.FORMAT getWhich() {
		return PlotDataExporter.FORMAT.valueOf(whichCombo.getText());
	}

}
