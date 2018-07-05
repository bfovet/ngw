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

import gov.sandia.dart.workflow.domain.Property;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.configuration.Prop;

import java.util.Arrays;

import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class PropertiesEditingSupport extends EditingSupport {

        public static final int EDITED = 9999;
		private TableViewer viewer;
        private CellEditor editor;
		private int column;

        public PropertiesEditingSupport(TableViewer viewer, int column) {
                super(viewer);
                this.viewer = viewer;
				switch (column) {
					case 0:
						this.editor = new TextCellEditor(viewer.getTable());
						break;
					case 1:						
						this.editor = new ComboBoxCellEditor(viewer.getTable(), Prop.availableTypes());
				}
                this.column = column;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
                return editor;
        }

        @Override
        protected boolean canEdit(Object element) {
                return true;
        }

        @Override
        protected Object getValue(Object element) {
        	Object result = null;	
        	switch (column) {
        		case 0:        		
                    result = ((Property) element).getName();
                    break;
        		case 1:
        			String[] labels = Prop.availableTypes();
        			result = Arrays.binarySearch(labels, ((Property) element).getType());
                    break;

        		}
        		return result == null ? "" : result;        			
        }

        @Override
        protected void setValue(Object element, Object userInputValue) {          
        	try {
        		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(element);
        		domain.getCommandStack().execute(new RecordingCommand(domain) {

        			@Override
        			protected void doExecute() {
        				switch (column) {
        				case 0:					
        					((Property) element).setName(String.valueOf(userInputValue));
        					((Property) element).getNode().eNotify(new NotificationImpl(EDITED, 0, 0));
        					break;
        				case 1:
                			String[] labels = Prop.availableTypes();
                			int index = (Integer) userInputValue;
                			if (index > -1 && index < labels.length) {
                				((Property) element).setType(labels[index]);
            					((Property) element).getNode().eNotify(new NotificationImpl(EDITED, 0, 0));
                			}
        					break;
        				}                    
        			}
        		});

        		viewer.update(element, null);
        	} catch (Exception ex) {
        		WorkflowEditorPlugin.getDefault().logError("Error setting object property", ex);
        	}
        }
}
