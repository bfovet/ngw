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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class RCLabelProvider extends LabelProvider implements ITableLabelProvider {
	@Override
	public String getColumnText(Object element, int columnIndex) {
		String[] data = (String[]) element;
		switch (columnIndex) {
			case 0:
				return data.length > 0 ? data[0] : "";
			case 1 :
				return data.length > 1 ? data[1] : "";
			case 2 :
				return data.length > 2 ? data[2] : "";
			case 3 :
				return data.length > 3 ? data[3] : "";
			case 4 :
				return data.length > 4 ? data[4] : "";
			default :
				return ""; 	
		}
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

}
