package gov.sandia.dart.workflow.editor.features;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.custom.ICustomFeature;

/**
 * 
 */

/**
 * @author mjgibso
 *
 */
public class OpenInternalNestedWorkflowFeatureProvider implements ICustomFeatureProvider
{

	/**
	 * 
	 */
	public OpenInternalNestedWorkflowFeatureProvider()
	{}

	/* (non-Javadoc)
	 * @see gov.sandia.dart.workflow.editor.features.ICustomFeatureProvider#createFeature(org.eclipse.graphiti.features.IFeatureProvider, java.lang.String, java.lang.String)
	 */
	@Override
	public ICustomFeature createFeature(IFeatureProvider fp, String nodeType, String property)
	{
		return new OpenInternalNestedWorkflowFeature(fp, nodeType, property);
	}

}
