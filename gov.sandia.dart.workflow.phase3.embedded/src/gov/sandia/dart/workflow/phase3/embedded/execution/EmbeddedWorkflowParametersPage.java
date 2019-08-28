package gov.sandia.dart.workflow.phase3.embedded.execution;

import java.util.List;
//import org.eclipse.core.resources.IFile;
//import org.eclipse.emf.transaction.RecordingCommand;
//import org.eclipse.emf.transaction.TransactionalEditingDomain;
//import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.phase3.embedded.execution.RunEmbeddedWorkflowWizard.Parameter;

public class EmbeddedWorkflowParametersPage extends WizardPage {
	private Composite container;
	private TableViewer tableViewer;
	//private Properties _paramsFromWizard;
	private List<Parameter> embeddedParameters;
	
	private static class ParameterValueEditingSupport extends EditingSupport {
        private final TableViewer viewer;
        private final CellEditor editor;

        public ParameterValueEditingSupport(TableViewer viewer) {
                super(viewer);
                this.viewer = viewer;
                this.editor = new TextCellEditor(viewer.getTable());
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
                return editor;
        }

        @Override
        protected boolean canEdit(Object element) {
                return element instanceof Parameter;
        }

        @Override
        protected Object getValue(Object element) {
        	return ((Parameter)element).value;
        }

        @Override
        protected void setValue(Object element, Object userInputValue) {          
        	try {
        		// TODO Break this up into three commands so it won't always dirty document.
//        		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(element);
//        		domain.getCommandStack().execute(new RecordingCommand(domain) {
//
//        			@Override
//        			protected void doExecute() {
        				((Parameter) element).value = String.valueOf(userInputValue);        					
//        			}
//        		});
        		viewer.update(element, null);
        	} catch (Exception e) {
        		WorkflowEditorPlugin.getDefault().logError("Error setting value of embedded parameter", e);
        	}
        }
	}

	public EmbeddedWorkflowParametersPage(List<Parameter> embeddedParameters) {
		super("Specify workflow parameter values");
		setTitle("Workflow parameters");
		setDescription("Specify values for workflow parameters");

		this.embeddedParameters = embeddedParameters;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		tableViewer = new TableViewer(container);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		TableViewerColumn vcolumn1 = new TableViewerColumn(tableViewer, SWT.LEFT);		
		TableColumn column1 = vcolumn1.getColumn();	
		column1.setText("Name");
		column1.setWidth(80);

		TableViewerColumn vcolumn2 = new TableViewerColumn(tableViewer, SWT.LEFT);		
		TableColumn column2 = vcolumn2.getColumn();	
		column2.setText("Type");
		column2.setWidth(80);

		TableViewerColumn vcolumn3 = new TableViewerColumn(tableViewer, SWT.RIGHT);		
		TableColumn column3 = vcolumn3.getColumn();	
		column3.setText("Value");
		column3.setWidth(130);
		vcolumn3.setEditingSupport(new ParameterValueEditingSupport(tableViewer));
		
		tableViewer.setContentProvider(new ArrayContentProvider());	
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.setLabelProvider(new ITableLabelProvider() {
			@Override
			public void addListener(ILabelProviderListener listener) {}

			@Override
			public void dispose() {}

			@Override
			public boolean isLabelProperty(Object element, String property) { return false; }

			@Override
			public void removeListener(ILabelProviderListener listener) {}

			@Override
			public Image getColumnImage(Object element, int columnIndex) { return null; }

			@Override
			public String getColumnText(Object element, int columnIndex) {
				Parameter p = (Parameter) element;
				switch (columnIndex) {
					case 0: return p.name;
					case 1:  return p.type;				
					case 2: return p.value;
					default: return p.name;					
				}			
			}
		});
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.setInput(embeddedParameters);
		
		setControl(container);
		
		setPageComplete(true);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			tableViewer.refresh();
	}
}
