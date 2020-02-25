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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.PrecisionPoint;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.CurvedConnection;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;

import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.preferences.IWorkflowEditorPreferences;

public class AddWFArcFeature extends AbstractAddFeature {

	/**
	 * Used as the name of property of a connection decorator, so that we can
	 * find the ones the "Remove Text" command should remove.
	 */
	public static final String TEXT_DECORATOR = "textDecorator";
	private static final IColorConstant FOREGROUND = new ColorConstant(98, 131, 167);
	public static final String OLD_CONNECTION = "oldConnection";

	public AddWFArcFeature (IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public PictogramElement add(IAddContext context) {
		IAddConnectionContext addConContext = (IAddConnectionContext) context;
		WFArc addedDARTArc = (WFArc) context.getNewObject();
		Connection oldConnection = (Connection) context.getProperty(OLD_CONNECTION);		
		Connection connection = oldConnection == null ? createConnection(addConContext) : cloneConnection(addConContext, oldConnection);
		connection.setStart(addConContext.getSourceAnchor());
		connection.setEnd(addConContext.getTargetAnchor());

		IGaService gaService = Graphiti.getGaService();
		Polyline polyline = gaService.createPolyline(connection);
		polyline.setLineWidth(1);
		polyline.setForeground(manageColor(FOREGROUND));
		addToDiagram(addedDARTArc);
		
		// create link and wire it
		link(connection, addedDARTArc);

		return connection;
	}

	private Connection cloneConnection(IAddConnectionContext context, Connection oldConnection) {
		Connection connection = null;
		IPeCreateService peCreateService = Graphiti.getPeCreateService();

		if (oldConnection instanceof CurvedConnection) {
			CurvedConnection cc = (CurvedConnection) oldConnection;
			EList<PrecisionPoint> points = cc.getControlPoints();
			double[] control = new double[points.size() * 2];
			for (int i=0; i<points.size(); ++i) {
				control[i*2] = points.get(i).getX();
				control[i*2 + 1] = points.get(i).getY();
			}
					
			connection = peCreateService.createCurvedConnection(control, getDiagram());   
			
		} else if (oldConnection instanceof FreeFormConnection) {
			FreeFormConnection fc = (FreeFormConnection) oldConnection;
			EList<Point> oldBp = fc.getBendpoints();
			
			connection = peCreateService.createFreeFormConnection(getDiagram());
			EList<Point> newBp = ((FreeFormConnection) connection).getBendpoints();
			for (Point point: oldBp) {
				Point newPoint = EcoreUtil.copy(point);
				newPoint.setX(newPoint.getX() + DuplicateNodeFeature.DUP_X_OFFSET);
				newPoint.setY(newPoint.getY() + DuplicateNodeFeature.DUP_Y_OFFSET);
				newBp.add(newPoint);
			}
			
		} else {
				connection = peCreateService.createManhattanConnection(getDiagram());				
		}
		return connection;
	}

	private Connection createConnection(IAddConnectionContext context) {
		Connection connection = null;
		WFArc arc = (WFArc) context.getNewObject();
		IPeCreateService peCreateService = Graphiti.getPeCreateService();

		if (arc.getSource().getName().equals("_LEND_") && arc.getTarget().getName().equals("_LBEGIN_")) {
			double[] control = new double[]  { 0.1d, 50d, 0.9d, 50d };
			connection = peCreateService.createCurvedConnection(control, getDiagram());        	
		} else {
			boolean manhattan = WorkflowEditorPlugin.getDefault().getPreferenceStore().getBoolean(IWorkflowEditorPreferences.MANHATTAN_CONNECTIONS);
			if (manhattan) {
				connection = peCreateService.createManhattanConnection(getDiagram());
			} else {
				connection = peCreateService.createFreeFormConnection(getDiagram());
			}
		}
		return connection;
	}

	@Override
	public boolean canAdd(IAddContext context) {
		// return true if given business object is an WFArc
		// note, that the context must be an instance of IAddConnectionContext
		if (context instanceof IAddConnectionContext
				&& context.getNewObject() instanceof WFArc) {
			return true;
		}
		return false;
	}
	
	public void addToDiagram(WFArc addednode) {
		if (addednode.eResource() == null) {
			EList<EObject> contents = getDiagram().eResource().getContents();
			contents.add(addednode);
			contents.addAll(addednode.getProperties());        	
		}
	}

}
