/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.settings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.internal.parts.DiagramEditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IManagedForm;

import com.strikewire.snl.apc.GUIs.settings.AbstractSettingsEditor;
import com.strikewire.snl.apc.GUIs.settings.IContextMenuRegistrar;
import com.strikewire.snl.apc.GUIs.settings.IMessageView;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.util.ParameterUtils;

@SuppressWarnings("restriction")
public class DiagramEditPartSettingsEditor extends AbstractSettingsEditor<DiagramEditPart> {

	private static final int FUDGE_FACTOR = 11;
	private final Set<Image> _images = new HashSet<>();
	private DiagramEditPart node;
	private TableViewer viewer;

	@Override
	public void createPartControl(IManagedForm mform, IMessageView messageView, MultiControlSelectionProvider selectionProvider, IContextMenuRegistrar ctxMenuReg)
	{
		super.createPartControl(mform, messageView, selectionProvider, ctxMenuReg);
		ImageDescriptor desc = WorkflowEditorPlugin.getImageDescriptor("/icons/shapes.gif");
		Image image = desc!=null ? desc.createImage() : null;
		if(image != null) {
			_images.add(image);
		}
		form.setImage(image);
		
		form.getBody().setLayout(new GridLayout(1, false));
		toolkit.createLabel(form.getBody(), "Parameters");
		
		viewer = new TableViewer(form.getBody());
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		Table table = viewer.getTable();
		table.setLayoutData(data);
		table.addListener(SWT.Resize, new Listener() {

	          @Override
	          public void handleEvent(Event event) {

	            Table table = (Table) event.widget;
	            int columnCount = table.getColumnCount();
	            if(columnCount == 0)
	              return;
	            Rectangle area = table.getClientArea();          		
	            int totalAreaWidth = area.width;

	            int lineWidth = table.getGridLineWidth();
	            if (lineWidth < 1)
	            		lineWidth = 1;
	            int totalGridLineWidth = (columnCount+1)*lineWidth; 
	            int totalColumnWidth = 0;
	            for (int i=0; i< table.getColumnCount() - 1; ++i) {
	           		TableColumn column = table.getColumn(i);
	           		totalColumnWidth += column.getWidth();
	            }
	            int diff = totalAreaWidth - (totalColumnWidth+totalGridLineWidth) - FUDGE_FACTOR;	            
	            TableColumn lastCol = table.getColumns()[columnCount-1];
	            lastCol.setWidth(diff <= 5 ? 5 : diff);
	          }
	        });
		
		TableViewerColumn vcolumn1 = new TableViewerColumn(viewer, SWT.RIGHT);		
		TableColumn column1 = vcolumn1.getColumn();	
		column1.setText("Name");
		column1.setWidth(80);

		TableViewerColumn vcolumn2 = new TableViewerColumn(viewer, SWT.RIGHT);		
		TableColumn column2 = vcolumn2.getColumn();	
		column2.setText("Type");
		column2.setWidth(80);

		TableViewerColumn vcolumn3 = new TableViewerColumn(viewer, SWT.RIGHT);		
		TableColumn column3 = vcolumn3.getColumn();	
		column3.setText("Value");
		column3.setWidth(80);
		
		vcolumn1.setEditingSupport(new ParameterEditingSupport(viewer, 0));
		vcolumn2.setEditingSupport(new ParameterEditingSupport(viewer, 1));
		vcolumn3.setEditingSupport(new ParameterEditingSupport(viewer, 2));
		
		viewer.setContentProvider(new ArrayContentProvider());	
		table.setLinesVisible(true);
		viewer.setLabelProvider(new ITableLabelProvider() {
			@Override
			public void addListener(ILabelProviderListener listener) {				
			}

			@Override
			public void dispose() {				
			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {				
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				WFNode p = (WFNode) element;
				switch (columnIndex) {
				case 0: return p.getName();
				case 1:  return ParameterUtils.getType(p);				
				case 2: return ParameterUtils.getValue(p);
				default: return p.getName();						
				}			
			}
		});
		table.setHeaderVisible(true);
		
	}
	
	@Override
	public void setNode(DiagramEditPart node) {
		form.setText("Diagram");		
		this.node = node;
		Diagram diagram = (Diagram) node.getModel();
		List<WFNode> params = getParameters(diagram);
		viewer.setInput(params);
	}

	private List<WFNode> getParameters(Diagram diagram) {
		List<WFNode> params = new ArrayList<>();
		Resource eResource = diagram.eResource();		
		for (EObject obj: eResource.getContents()) {
			if (obj instanceof WFNode) {
				WFNode obj2 = (WFNode) obj;
				if (ParameterUtils.isParameter(obj2))
					params.add(obj2);
			}
		}
		return params;
	}

	@Override
	public DiagramEditPart getNode() {		
		return node;
	}

	@Override
	public void dispose() {
		_images.stream().filter(i -> !i.isDisposed()).forEach(i -> i.dispose());
		_images.clear();		
	}

}
