package gov.sandia.dart.workflow.editor.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.custom.ICustomFeature;

public class AutoEditParameterFeatureProvider implements ICustomFeatureProvider {

	public AutoEditParameterFeatureProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ICustomFeature createFeature(IFeatureProvider fp, String nodeType, String property) {
		// TODO Auto-generated method stub
		return new AutoEditParameterFeature(fp, nodeType, property);
	}

}
