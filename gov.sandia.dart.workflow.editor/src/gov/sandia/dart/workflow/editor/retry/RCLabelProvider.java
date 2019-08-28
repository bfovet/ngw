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
