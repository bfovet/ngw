/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractCopyFeature;

import gov.sandia.dart.workflow.domain.Image;
import gov.sandia.dart.workflow.domain.Note;
import gov.sandia.dart.workflow.domain.Response;
import gov.sandia.dart.workflow.domain.ResponseArc;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.domain.WFNode;

public class CopyWorkflowObjectsFeature extends AbstractCopyFeature {

	public CopyWorkflowObjectsFeature(IFeatureProvider fp) {
		super(fp);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void copy(ICopyContext context) {
		putToClipboard(context.getPictogramElements());
	}

	@Override
	public boolean canCopy(ICopyContext context) {
		Object[] objects = context.getPictogramElements();
		if (objects != null && objects.length > 0) {
			for (Object o: objects) {
				if (o instanceof PictogramElement) {
					Object bo = getBusinessObjectForPictogramElement((PictogramElement) o);
					if (bo instanceof WFNode || bo instanceof Note || bo instanceof Image ||
						bo instanceof WFArc || bo instanceof Response ||
						bo instanceof ResponseArc) {
						continue;
					} else {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}


}
