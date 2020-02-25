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
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class OperateSwitchFeature extends AbstractCustomFeature {

	private String property;
	private String nodeType;

	public OperateSwitchFeature(IFeatureProvider fp, String nodeType, String property) {
		super(fp);
		this.nodeType = nodeType;
		this.property = property;
	}

	@Override
	public String getName() {
		return "Operate Switch";
	}
	
	@Override
	public void execute(ICustomContext context) {
		final PictogramElement pe = context.getPictogramElements()[0];
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		if (bo instanceof WFNode) {
			WFNode node = (WFNode)bo;	
			if (canOperateOn(node)) {
				boolean isOn =  "true".equals(PropertyUtils.getProperty(node, property));
				if (isOn)
					PropertyUtils.setProperty(node, property, "false");
				else
					PropertyUtils.setProperty(node, property, "true");	
			}		
		}
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		if (context.getPictogramElements().length != 1)
			return false;

		final PictogramElement pe = context.getPictogramElements()[0];
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		if (bo instanceof WFNode) {
			return canOperateOn((WFNode) bo);
		}
		return false;
	}

	private boolean canOperateOn(WFNode bo) {
		return bo.getType().equals(nodeType) && PropertyUtils.hasProperty(bo, property);
	}
	
	public static class Provider implements ICustomFeatureProvider {

		@Override
		public ICustomFeature createFeature(IFeatureProvider fp, String nodeType, String property) {
			return new OperateSwitchFeature(fp, nodeType, property);
		}
		
	}
}
