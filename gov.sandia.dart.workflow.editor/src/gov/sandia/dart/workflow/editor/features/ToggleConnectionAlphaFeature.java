package gov.sandia.dart.workflow.editor.features;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;

public class ToggleConnectionAlphaFeature extends AbstractCustomFeature {

	private static final double SOLID = 0.0;
	private static final double TRANSPARENT = 0.8;

	public ToggleConnectionAlphaFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		EList<Connection> connections = getDiagram().getConnections();
		if (connections.size() < 1)
			return "No connections";
		Double t = connections.get(0).getGraphicsAlgorithm().getTransparency();
		return isSolid(t) ? "Fade Connections" : "Show Connections";
	}
	
	@Override
	public String getDescription() {
		return "Hide or show connecting lines";
	}
	
	@Override
	public boolean canExecute(ICustomContext context) {
		EList<Connection> connections = getDiagram().getConnections();
		return connections.size() > 0;
	}
	
	@Override
	public void execute(ICustomContext context) {
		EList<Connection> connections = getDiagram().getConnections();
		if (connections.size() < 1)
			return;
		Double t = connections.get(0).getGraphicsAlgorithm().getTransparency();
		if (isSolid(t))
			t = TRANSPARENT;
		else
			t = SOLID;
		for (Connection c: connections) {
			c.getGraphicsAlgorithm().setTransparency(t);
		}
	}

	private boolean isSolid(Double t) {
		return t < 0.5;
	}

	@Override
	public boolean hasDoneChanges() {
		// Although this does change persistable properties, we don't want it to make the diagram dirty. In 
		// WorkflowPersistenceBehavior, we reset the transparency of all connections on loading
		return false;
	}

}
