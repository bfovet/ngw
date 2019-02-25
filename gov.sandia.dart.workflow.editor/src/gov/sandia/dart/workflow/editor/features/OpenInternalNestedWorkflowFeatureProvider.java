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
