package gov.sandia.dart.workflow.editor.features;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.sandia.dart.workflow.domain.Port;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.preferences.IWorkflowEditorPreferences;
import gov.sandia.dart.workflow.util.ParameterUtils;

public class MinMaxFeature extends AbstractCustomFeature {

	public static final int MIN_MAX_ICON_SIZE = 16;
	private static final int COMPRESSED_PORT = 25; // AddWFNodeFeature.TOP_PORT;
	private static final int MINIMUM_HEIGHT = 32;
	private static final int PORT_SPACING = AddWFNodeFeature.PORT_SPACING;

	public MinMaxFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canExecute(ICustomContext context) {		
		if (isEnabled() && context.getPictogramElements().length == 1) {
			final PictogramElement pe = context.getPictogramElements()[0];
			Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
			if (bo instanceof WFNode && !ParameterUtils.isParameter((WFNode) bo)) {
				GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
				int dx =  (ga.getX() + ga.getWidth()) - context.getX();
				int dy =  context.getY() - ga.getY();
				return dx > 0 && dx < MIN_MAX_ICON_SIZE && dy > 0 && dy < MIN_MAX_ICON_SIZE;
			}			
		}
		return false;
	}
	
	@Override
	public void execute(ICustomContext context) {
		PictogramElement pe = context.getPictogramElements()[0];
		WFNode node = (WFNode) getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
		if (ga.getHeight() == MINIMUM_HEIGHT) {
			spreadOutPorts(node.getInputPorts());
			spreadOutPorts(node.getOutputPorts());
			ga.setHeight(computeHeight(node));
		} else {
			ga.setHeight(MINIMUM_HEIGHT);
			compressPorts(node.getInputPorts());
			compressPorts(node.getOutputPorts());
		}		
	}

	private void compressPorts(EList<? extends Port> ports) {
		IFeatureProvider fp = getFeatureProvider();
		for (Port port: ports) {
			FixPointAnchor pe = (FixPointAnchor) fp.getPictogramElementForBusinessObject(port);
			pe.getLocation().setY(COMPRESSED_PORT);
		}
	}

	private void spreadOutPorts(EList<? extends Port> ports) {
		IFeatureProvider fp = getFeatureProvider();
		int index = 0;
		for (Port port: ports) {
			FixPointAnchor pe = (FixPointAnchor) fp.getPictogramElementForBusinessObject(port);
			pe.getLocation().setY(AddWFNodeFeature.TOP_PORT + index++*PORT_SPACING);
		}
	}

	private int computeHeight(WFNode node) {		
		return MINIMUM_HEIGHT + PORT_SPACING * Math.max(node.getInputPorts().size(), node.getOutputPorts().size());
	}

	public static boolean isEnabled() {
        return WorkflowEditorPlugin.getDefault().getPreferenceStore().getBoolean(IWorkflowEditorPreferences.SHOW_MIN_MAX_ICON);
	}

	@Override
	public String getName() {
		
		return "Expand/Collapse Node";
	}
	
	@Override
	public String getDescription() {
		return "Expand or Collapse Node";
	}

}
