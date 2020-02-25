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
 * Created by mjgibso on Jan 14, 2015 at 11:40:54 AM
 */
package gov.sandia.dart.chart.xyplot.ui.view;

import gov.sandia.dart.chart.xyplot.ui.Activator;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author mjgibso
 *
 */
public class ExportPlotDataWizard extends Wizard implements INewWizard
{
	private ExportPlotDataPage _page;
	private PlotDataExporter editor;
		
	/**
	 * 
	 */
	public ExportPlotDataWizard(PlotDataExporter editor)
	{		
		this.editor = editor;
		setWindowTitle("Export Data");
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		setNeedsProgressMonitor(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages()
	{
		
		_page = new ExportPlotDataPage();
		addPage(_page);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish()
	{		
		
		try {
			String location = _page.getLocation();
			PlotDataExporter.FORMAT which = _page.getWhich();
			if (StringUtils.isEmpty(location))
				return false;
			
			getContainer().run(true, false, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {							
					editor.outputDelimiterSeparatedValues(location, which);
				}
			});
			
			return true;
		} catch (Throwable t) {
			String msgTitle = "Error exporting data";
			Activator.getDefault().logError(msgTitle, t);
			MessageDialog.openError(getShell(), msgTitle, t.getMessage());
			return false;
		}
	}
	
}
