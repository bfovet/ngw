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

import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.util.ParameterUtils;

public class AutoEditParameterFeature extends AbstractCustomFeature implements ICustomFeature {

	private String nodeType;
	private String property;

	public AutoEditParameterFeature(IFeatureProvider fp, String nodeType, String property) {
		super(fp);
		this.nodeType = nodeType;
		this.property = property;
	}

	@Override
	public boolean hasDoneChanges() {
		return false;
	}
	
	@Override
	public final boolean canExecute(ICustomContext context) {
		final PictogramElement pe = context.getPictogramElements()[0];		
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		return bo instanceof WFNode && ParameterUtils.isParameter((WFNode)bo);
	}
	
	@Override
	public void execute(ICustomContext context) {
		final PictogramElement pe = context.getPictogramElements()[0];

		IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
		directEditingInfo.setActive(true);
		directEditingInfo.setMainPictogramElement(pe);
		directEditingInfo.setPictogramElement(pe);
		directEditingInfo.setGraphicsAlgorithm(pe.getGraphicsAlgorithm());
		getFeatureProvider().getDirectEditingInfo().setActive(true);
		getDiagramBehavior().refresh();
	}

}
