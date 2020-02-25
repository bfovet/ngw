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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.NamedObjectWithProperties;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.Port;

public class DeletePortFeature extends DefaultDeleteFeature {

	public DeletePortFeature(IFeatureProvider fp) {
		super(fp);
	}
	@Override
	public void preDelete(IDeleteContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof Port) {
			Port port = (Port) bo;
			Set<EObject> toDelete = new HashSet<>();
			
			if (port instanceof InputPort) {	
				InputPort iport = (InputPort) port;
				for (NamedObjectWithProperties obj: iport.getArcs()) {
					toDelete.addAll(obj.getProperties());
					toDelete.add(obj);
				}
			}
			if (port instanceof OutputPort) {	
				OutputPort oport = (OutputPort) port;

				for (NamedObjectWithProperties obj: oport.getArcs()) {
					toDelete.addAll(obj.getProperties());
					toDelete.add(obj);
				}
				for (NamedObjectWithProperties obj: oport.getResponseArcs()) {
					toDelete.addAll(obj.getProperties());
					toDelete.add(obj);
				}

			}

			toDelete.addAll(port.getProperties());
			toDelete.add(port);							
			
			if (toDelete.size() > 0) {
				deleteBusinessObjects(toDelete.toArray());
				setDoneChanges(true);
			}
		}
	}

}
