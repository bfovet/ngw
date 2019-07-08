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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import gov.sandia.dart.workflow.domain.Port;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.configuration.Output;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class OutputPortEditingSupport extends EditingSupport {

        private final TableViewer viewer;
        private CellEditor editor;
		private int column;

        public OutputPortEditingSupport(TableViewer viewer, int column) {
                super(viewer);
                this.viewer = viewer;
				switch (column) {
				case 0:
					 this.editor = new TextCellEditor(viewer.getTable());
		                editor.setValidator(new ICellEditorValidator() {
							
							@Override
							public String isValid(Object value) {
								String text = String.valueOf(value);
								if (text.contains(".")) {
									return "Port names cannot contain a period ('.')";
								} else {
									return null;
								}
							}
						});		                
		                break;
				case 1:						
					this.editor = new StringComboBoxCellEditor(viewer.getTable(), Output.availableTypes());
					break;
				case 2:
					this.editor = new TextCellEditor(viewer.getTable());
					break;
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
        		String result = null;	
        		switch (column) {
        		case 0:        		        			
                    result = ((Port) element).getName();
                    break;
        		case 1:
                    result = ((Port) element).getType();
                    break;
    			case 2:
    				String value = PropertyUtils.getProperty((Port)element, "filename");
    				return value == null ? "" : value;
        		}
        		return result == null ? "" : result;        			
        }

        @Override
        protected void setValue(Object element, Object userInputValue) {          
        	try {
        		// TODO Break this up into three commands so it won't always dirty document.
        		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(element);
        		domain.getCommandStack().execute(new RecordingCommand(domain) {

        			@Override
        			protected void doExecute() {
        				switch (column) {
        				case 0:					
        					((Port) element).setName(String.valueOf(userInputValue));
        					break;
        				case 1:
        					((Port) element).setType(String.valueOf(userInputValue));
        					break;
        				case 2:
        					String oldValue = PropertyUtils.getProperty((Port)element, "filename");
        					String newValue = String.valueOf(userInputValue);
        					if (!StringUtils.isEmpty(newValue) || oldValue != null) {
        						PropertyUtils.setProperty((Port)element, "filename", newValue);
        						break;
        					}
        				}
        			}
        		});
        		viewer.update(element, null);
        	} catch (Exception e) {
        		WorkflowEditorPlugin.getDefault().logError("Error setting properties of port", e);
        	}
        }
}
