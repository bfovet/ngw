package gov.sandia.dart.workflow.editor.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.custom.ICustomFeature;

public interface ICustomFeatureProvider
{
	ICustomFeature createFeature(IFeatureProvider fp, String nodeType, String property);
}
