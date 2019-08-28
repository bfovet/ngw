/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.IExecutionInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;

import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.NamedObject;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.features.AddWFNodeFeature;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class WorkflowValidator {

	void validate(IExecutionInfo executionInfo, Map<EObject, IDecorator> decorators) {
		if (executionInfo.getExecutionList().length == 0)
			return;

		
		// Demeter is rolling over in his grave.
		IFeatureProvider fp = executionInfo.getExecutionList()[0].getFeature().getFeatureProvider();
		
		validate(decorators, fp);
	}

	public void validate(Map<EObject, IDecorator> decorators, IFeatureProvider fp) {
		decorators.clear();
		EList<EObject> contents = fp.getDiagramTypeProvider().getDiagram().eResource().getContents();
		Set<String> dupeCheck = new HashSet<>();
		for (EObject object: contents) {
			if (object instanceof WFNode) {
				WFNode node = (WFNode) object;	
				
				PictogramElement pe = fp.getPictogramElementForBusinessObject(object);
				if (pe == null) {
					IDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
					imageRenderingDecorator.setMessage("Dangling node " + node.getName() + " in diagram");
					decorators.put(fp.getDiagramTypeProvider().getDiagram(), imageRenderingDecorator);	
				}

				// RULE: Nodes with no inputs are start nodes
				// TODO This isn't validation, this doesn't belong here
				if (node.isStart() == hasIncomingConnections(node)) {
					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(node);
					domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() {
							node.setStart(!hasIncomingConnections(node));
						}
					});
				}
				
				// RULE: _LEND_ ports only connected to a single _LBEGIN_ port
				for (OutputPort port: node.getOutputPorts()) {
					if (port.getName().equals(AddWFNodeFeature.LEND)) {
						if (port.getArcs().size() > 1) {
							ImageDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
							imageRenderingDecorator.setMessage("Loop ends can only have one connection");
							imageRenderingDecorator.setX(pe.getGraphicsAlgorithm().getWidth() - 20);
							decorators.put(object, imageRenderingDecorator);	
						} else if (!port.getArcs().isEmpty()) {
							WFArc arc = port.getArcs().get(0);
							if (!arc.getTarget().getName().equals(AddWFNodeFeature.LBEGIN)) {
								ImageDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
								imageRenderingDecorator.setMessage("Loop ends can only be connected to loop begins");
								imageRenderingDecorator.setX(pe.getGraphicsAlgorithm().getWidth() - 20);
								decorators.put(object, imageRenderingDecorator);	
							}
						}
					}
				}
				
				if (!dupeCheck.add(node.getName())) {
					// RULE: Node names must be unique					
					ImageDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);
					imageRenderingDecorator.setMessage("Node names must be unique");
					imageRenderingDecorator.setX(pe.getGraphicsAlgorithm().getWidth() - 20);

					decorators.put(object, imageRenderingDecorator);	
				}

				// RULE: Can only clear private workdirs
				if (PropertyUtils.isTrue(node, PropertyUtils.CLEAR_WORK_DIR) &&
						!(PropertyUtils.isTrue(node, PropertyUtils.PRIVATE_WORK_DIR) || PropertyUtils.isTrue(node, PropertyUtils.OLD_PRIVATE_WORK_DIR))) {
					ImageDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);
					imageRenderingDecorator.setMessage("Only private work directories can be cleared");
					imageRenderingDecorator.setX(pe.getGraphicsAlgorithm().getWidth() - 20);	
					
					decorators.put(object, imageRenderingDecorator);	

				}
					
				
				
				for (InputPort port: node.getInputPorts()) {
					validateName(fp, decorators, node, port);					
				}
				
				for (OutputPort port: node.getOutputPorts()) {
					validateName(fp, decorators, node, port);		
				}		
								
			} 
		}
	}

	private void validateName(IFeatureProvider fp, Map<EObject, IDecorator> decorators, WFNode object, NamedObject port) {
		String name = port.getName();
		if (name.indexOf('.') > -1) {
			ImageDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
			imageRenderingDecorator.setMessage("Invalid character '.' in port name '" + port.getName() + "' of node '" + object.getName() + "'");
			PictogramElement pe = fp.getPictogramElementForBusinessObject(object);
			imageRenderingDecorator.setX(pe.getGraphicsAlgorithm().getWidth() - 20);
			decorators.put(object, imageRenderingDecorator);					
		}
	}
	
	private boolean hasIncomingConnections(WFNode node) {
		for (InputPort port: node.getInputPorts()) {
			if (!port.getName().equals(AddWFNodeFeature.LBEGIN) && port.getArcs().size() > 0)
				return true;
		}
		return false;
	}

}
