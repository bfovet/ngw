/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;

import gov.sandia.dart.workflow.domain.ResponseArc;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.preferences.IWorkflowEditorPreferences;

public class AddResponseArcFeature extends AbstractAddFeature {

    private static final IColorConstant FOREGROUND = new ColorConstant(98, 131, 167);
 
    public AddResponseArcFeature (IFeatureProvider fp) {
        super(fp);
    }
 
    @Override
	public PictogramElement add(IAddContext context) {
        IAddConnectionContext addConContext = (IAddConnectionContext) context;
        ResponseArc addedDARTArc = (ResponseArc) context.getNewObject();
        IPeCreateService peCreateService = Graphiti.getPeCreateService();
        
        // CONNECTION WITH POLYLINE
        Connection connection = null;
        if (WorkflowEditorPlugin.getDefault().getPreferenceStore().getBoolean(IWorkflowEditorPreferences.MANHATTAN_CONNECTIONS)) {
        	connection = peCreateService.createManhattanConnection(getDiagram());
        } else {
        	connection = peCreateService.createFreeFormConnection(getDiagram());
        } 
        
        connection.setStart(addConContext.getSourceAnchor());
        connection.setEnd(addConContext.getTargetAnchor());
 
        IGaService gaService = Graphiti.getGaService();
        Polyline polyline = gaService.createPolyline(connection);
        polyline.setLineWidth(1);
        polyline.setForeground(manageColor(FOREGROUND));
        // Store domain object in diagram
        if (addedDARTArc.eResource() == null) {
            getDiagram().eResource().getContents().add(addedDARTArc);
        }
        // create link and wire it
        link(connection, addedDARTArc);

        return connection;
    }
 
    @Override
	public boolean canAdd(IAddContext context) {
        // return true if given business object is an ResponseArc
        // note, that the context must be an instance of IAddConnectionContext
        if (context instanceof IAddConnectionContext
            && context.getNewObject() instanceof ResponseArc) {
            return true;
        }
        return false;
    }

}
