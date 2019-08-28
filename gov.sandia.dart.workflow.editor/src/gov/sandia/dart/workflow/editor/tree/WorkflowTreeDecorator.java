/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.tree;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.WFNode;

public class WorkflowTreeDecorator extends LabelProvider implements ILabelDecorator {	
	private static final String WORKFLOW_STATE = "workflow.status.log";
	public static final String ID = "gov.sandia.dart.workflow.phase3.embedded.workflowFileDecorator";

	public WorkflowTreeDecorator() {
	}
	
	
	@Override
	public void dispose() {
	}

	@Override
	public Image decorateImage(Image image, Object element) {
		return null;
	}

	@Override
	public String decorateText(String text, Object element) {
		if(element instanceof WFNode) {
			WFNode node = (WFNode) element;

			int arcCount = 0;
			
			for(OutputPort outputPort : node.getOutputPorts()) {
				arcCount += outputPort.getArcs().size();
			}

			
			if(arcCount > 1) {
				text = text + "  [" + arcCount + "]";
			}
		}		
		return text;
	}
}
