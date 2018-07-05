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

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.util.ParameterUtils;

public class ParameterEditingSupport extends EditingSupport {

        private final TableViewer viewer;
        private final CellEditor editor;
		private int column;

        public ParameterEditingSupport(TableViewer viewer, int column) {
                super(viewer);
                this.viewer = viewer;
                this.editor = new TextCellEditor(viewer.getTable());
                this.column = column;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
                return editor;
        }

        @Override
        protected boolean canEdit(Object element) {
                return element instanceof WFNode;
        }

        @Override
        protected Object getValue(Object element) {
        		String result = null;	
        		switch (column) {
        		case 0:        		        			
                    result = ((WFNode) element).getName();
                    break;
        		case 1:
                    result = ParameterUtils.getType((WFNode) element);
                    break;
    			case 2:
                result = ParameterUtils.getValue((WFNode) element);
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
        					((WFNode) element).setName(String.valueOf(userInputValue));
        					break;
        				case 1:
        					ParameterUtils.setType((WFNode) element, String.valueOf(userInputValue));
        					break;
        				case 2:
        					ParameterUtils.setValue((WFNode) element, String.valueOf(userInputValue));        					
        				}
        			}
        		});
        		viewer.update(element, null);
        	} catch (Exception e) {
        		WorkflowEditorPlugin.getDefault().logError("Error setting properties of port", e);
        	}
        }
}
