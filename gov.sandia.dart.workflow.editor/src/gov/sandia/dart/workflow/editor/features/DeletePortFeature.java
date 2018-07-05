package gov.sandia.dart.workflow.editor.features;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

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
			Port node = (Port) bo;
			Set<EObject> toDelete = new HashSet<>();
			
			toDelete.addAll(node.getProperties());			
			
			if (toDelete.size() > 0) {
				deleteBusinessObjects(toDelete.toArray());
				setDoneChanges(true);
			}
		}
	}

}
