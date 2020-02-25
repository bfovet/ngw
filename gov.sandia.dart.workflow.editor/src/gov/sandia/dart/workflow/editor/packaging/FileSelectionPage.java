/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.packaging;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class FileSelectionPage extends WizardPage {
    private Composite container;
	private String[] files;
	private CheckboxTableViewer viewer_;

    public FileSelectionPage(String[] files) {
        super("Select Files");
        setTitle("Select Files");
        setDescription("Select the files that should be packaged as part of the component");
        this.files = files;
    }

    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      
        Table table =
        		new Table(container, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        TableColumn tc = new TableColumn(table, SWT.NONE);
        TableColumnLayout layout = new TableColumnLayout();
        layout.setColumnData(tc, new ColumnWeightData(100));
        container.setLayout(layout);    

        viewer_ = new CheckboxTableViewer(table);
        viewer_.setContentProvider(ArrayContentProvider.getInstance());              
        viewer_.setInput(files);
        viewer_.setAllChecked(true);

        setControl(container);
        setPageComplete(true);
    }

    public List<String> getSelectedFiles() {
        Object[] objs = viewer_.getCheckedElements();
        List<String> results = new ArrayList<>();
        for (Object o: objs) {
        		results.add(o.toString());
        }
        return results;
    }
}
