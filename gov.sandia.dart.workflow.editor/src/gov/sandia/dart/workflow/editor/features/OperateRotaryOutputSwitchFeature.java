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

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class OperateRotaryOutputSwitchFeature extends AbstractCustomFeature {

	private static final String SELECTOR = "selector";

	public OperateRotaryOutputSwitchFeature(IFeatureProvider fp) {
		super(fp);
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
				String selector = PropertyUtils.getProperty(node, SELECTOR);
				EList<OutputPort> outputPorts = node.getOutputPorts();

				int index = findIndexOfNamedPort(node, selector);
				if (index == -1 || index == outputPorts.size() - 1) {
					PropertyUtils.setProperty(node, SELECTOR, outputPorts.get(0).getName());
				} else {
					PropertyUtils.setProperty(node, SELECTOR, outputPorts.get(index + 1).getName());
				}
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
		return PropertyUtils.hasProperty(bo, SELECTOR) && bo.getOutputPorts().size() > 0;
	}
	
	private int findIndexOfNamedPort(WFNode node, String name) {
		EList<OutputPort> outputPorts = node.getOutputPorts();
		for (int i=0; i<outputPorts.size();++i) {
			if (outputPorts.get(i).getName().equals(name))
				return i;
		}
		return -1;
	}
	
	public static class Provider implements ICustomFeatureProvider {

		@Override
		public ICustomFeature createFeature(IFeatureProvider fp, String nodeType, String property) {
			return new OperateRotaryOutputSwitchFeature(fp);
		}
		
	}
}
