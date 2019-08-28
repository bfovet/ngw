package gov.sandia.dart.workflow.editor.retry;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

public class RetryConfigurationEditor extends EditorPart {

	private Table table;
	private TableViewer tableViewer;
	private final static String[] COLUMN_NAMES = {"File", "Pattern", "Delay", "Action", "Comments"};
	final static List<String> columnNames = Collections.unmodifiableList(Arrays.asList(COLUMN_NAMES));
	private final static String[] ACTION_NAMES = {"FAIL", "FAIL_IF_NOT", "RETRY", "RETRY_IF_NOT", "SUCCESS", "REPORT", "REPORT_IF_NOT"};
	final static List<String> actionNames = Collections.unmodifiableList(Arrays.asList(ACTION_NAMES));
	private boolean isDirty;
	private List<String[]> data = new ArrayList<>();

	public RetryConfigurationEditor() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		IFile file = ((FileEditorInput) getEditorInput()).getFile();
		StringBuilder builder = new StringBuilder();
		for (String[] line: data) {
			builder.append(StringUtils.join(line, ",")).append("\n");
		}
		try {
			file.setContents(new ByteArrayInputStream(builder.toString().getBytes()), IResource.FORCE, null);
		} catch (CoreException e) {
			WorkflowEditorPlugin.getDefault().logError("Error saving configuration file", e);
		}
		isDirty = false;
		firePropertyChange(PROP_DIRTY); 
	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		// TODO Error handling, better model!
		try (InputStream is = ((FileEditorInput) input).getFile().getContents()) {
			List<String> lines = IOUtils.readLines(is, Charset.defaultCharset());
			for (String line: lines) {
				String[] split = line.split(",");
				String[] clean = {"", "", "", "", ""};
				for (int i=0; i<Math.min(5,  split.length); ++i) {
					clean[i] = split[i].trim();
				}
				data.add(clean);
			}
		} catch (Exception e) {
			// TODO Error handling
		}
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		createTable(parent);
		createTableViewer();
		createButtons(parent);
	}

	
	private void createTable(Composite parent) {
		
		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = 4;
		parent.setLayout (layout);

		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
					SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		table = new Table(parent, style);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 4;
		table.setLayoutData(gridData);		
					
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.LEFT, 0);		
		column.setText(COLUMN_NAMES[0]);
		column.setWidth(250);
		column.setToolTipText("Path to a runtime file, relative to a nestedWorkflow node's working directory");
		
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(COLUMN_NAMES[1]);
		column.setWidth(250);
		column.setToolTipText("A literal pattern to match in the named file");


		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText(COLUMN_NAMES[2]);
		column.setWidth(50);
		column.setToolTipText("If the pattern is found in the file, wait this number of seconds before proceeding");


		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText(COLUMN_NAMES[3]);
		column.setWidth(100);
		column.setToolTipText("Action take on match after delay");
		
		column = new TableColumn(table, SWT.LEFT, 4);
		column.setText(COLUMN_NAMES[4]);
		column.setWidth(100);
		column.setToolTipText("Notes, comments or description");


	}

	/**
	 * Create the TableViewer 
	 */
	private void createTableViewer() {

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		
		tableViewer.setColumnProperties(COLUMN_NAMES);

		// Create the cell editors
		CellEditor[] editors = new CellEditor[COLUMN_NAMES.length];

		// Column 1 : File
		editors[0] = new TextCellEditor(table);

		// Column 2 : Pattern
		TextCellEditor textEditor = new TextCellEditor(table);
		editors[1] = textEditor;

		// Column 3 : Delay
		textEditor = new TextCellEditor(table);
		((Text) textEditor.getControl()).addVerifyListener(
				new VerifyListener() {
					@Override
					public void verifyText(VerifyEvent e) {
						e.doit = e.text.matches("[\\-0-9]*") ;
					}
				});
		editors[2] = textEditor;

		// Column 4 : Action
		// TODO COMBO
		editors[3] = new ComboBoxCellEditor(table, ACTION_NAMES, SWT.READ_ONLY);

		// Column 5 : Comments
		editors[4] = new TextCellEditor(table);

		
		tableViewer.setCellEditors(editors);
		tableViewer.setCellModifier(new RCCellModifier(this));
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new RCLabelProvider());
		tableViewer.setInput(data);
	}

	private void createButtons(Composite parent) {
		
		// Create and configure the "Add" button
		Button add = new Button(parent, SWT.PUSH | SWT.CENTER);
		add.setText("Add");
		
		add.addSelectionListener(new SelectionAdapter() {
       		@Override
			public void widgetSelected(SelectionEvent e) {
				data.add(new String[] {"", "", "", "", ""});
				setDirty();
				tableViewer.refresh();
			}
		});

		Button delete = new Button(parent, SWT.PUSH | SWT.CENTER);
		delete.setText("Delete");

		delete.addSelectionListener(new SelectionAdapter() {       	
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = table.getSelectionIndex();
				if (index > -1) {
					data.remove(index);
					setDirty();
					tableViewer.refresh();
				}
			}
		});
		
		Button up = new Button(parent, SWT.PUSH | SWT.CENTER);
		up.setText("Up");

		up.addSelectionListener(new SelectionAdapter() {       	
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = table.getSelectionIndex();
				if (index > 0) {
					String[] row = data.remove(index);
					data.add(index - 1, row);
					setDirty();
					tableViewer.refresh();
				}
			}
		});
		
		Button down = new Button(parent, SWT.PUSH | SWT.CENTER);
		down.setText("Down");

		down.addSelectionListener(new SelectionAdapter() {       	
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = table.getSelectionIndex();
				if (index < table.getItemCount() - 1) {
					String[] row = data.remove(index);
					data.add(index + 1, row);
					setDirty();
					tableViewer.refresh();
				}
			}
		});


	}

	
	@Override
	public void setFocus() {
		table.setFocus();
	}
	
	
	public void setDirty() {
		isDirty = true;	
		firePropertyChange(PROP_DIRTY); 
	}
	
	TableViewer getTableViewer() {
		return tableViewer;
	}

}
