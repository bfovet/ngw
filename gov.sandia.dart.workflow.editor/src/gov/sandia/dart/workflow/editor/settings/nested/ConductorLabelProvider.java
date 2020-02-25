/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.settings.nested;

import org.eclipse.jface.viewers.LabelProvider;

import gov.sandia.dart.workflow.domain.Conductor;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class ConductorLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof Conductor) {
			String type = PropertyUtils.getProperty((Conductor) element, "conductor");
			if (type == null)
				return "unknown";
			switch (type) {
			case "simple": return "Run once";
			case "repeat": {
				String count = PropertyUtils.getProperty((Conductor) element, "count");
				return "Run " + count + " times";
			}
			case "list": {
				String param = PropertyUtils.getProperty((Conductor) element, "parameter");
				return "Run while setting '" + param + "' to a list of values";
			}
			case "sweep": {
				String param = PropertyUtils.getProperty((Conductor) element, "parameter");
				return "Sweep parameter '" + param + "' over a range";
			}
			default:
				return type;
			}
		} else {
			return super.getText(element);
		}
	}
}
