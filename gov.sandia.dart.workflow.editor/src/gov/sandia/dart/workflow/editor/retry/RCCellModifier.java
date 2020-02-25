/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.retry;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

public class RCCellModifier implements ICellModifier {

	private RetryConfigurationEditor editor;

	public RCCellModifier(RetryConfigurationEditor editor) {
		this.editor = editor;
	}

	@Override
	public boolean canModify(Object element, String property) {
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		int columnIndex = RetryConfigurationEditor.columnNames.indexOf(property);
		String[] data = (String[]) element;
		if ("Action".equals(property)) {
			if (data.length <= columnIndex)
				return new Integer(0);
			int index = RetryConfigurationEditor.actionNames.indexOf(data[columnIndex]);
			if (index == -1)
				index = 0;
			return new Integer(index);
		}
		return (data.length > columnIndex) ? data[columnIndex] : "";		
	}

	@Override
	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem) element;
		String[] data = (String[]) item.getData();
		int columnIndex = RetryConfigurationEditor.columnNames.indexOf(property);

		if (data.length > columnIndex) {
			if ("Action".equals(property)) {
				if (value instanceof Integer) {
					int index = (Integer) value;
					if (index < 0)
						index = 0;
					if (index > RetryConfigurationEditor.actionNames.size() - 1)
						index = RetryConfigurationEditor.actionNames.size() - 1;
					data[columnIndex] = RetryConfigurationEditor.actionNames.get(index);
					editor.setDirty();
					editor.getTableViewer().refresh();
				}

			} else if (!String.valueOf(value).equals(data[columnIndex])) {
				data[columnIndex] = String.valueOf(value);
				editor.setDirty();
				editor.getTableViewer().refresh();
			}
		}

	}

}
