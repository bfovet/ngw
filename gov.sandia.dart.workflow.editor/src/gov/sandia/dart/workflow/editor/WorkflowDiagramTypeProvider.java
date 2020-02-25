/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRendererFactory;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

import gov.sandia.dart.workflow.editor.rendering.GARendererFactory;

public class WorkflowDiagramTypeProvider extends AbstractDiagramTypeProvider {
    private IToolBehaviorProvider[] toolBehaviorProviders;

	public WorkflowDiagramTypeProvider() {
		setFeatureProvider(new WorkflowFeatureProvider(this));
	}
	
	@Override
    public synchronized IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
        if (toolBehaviorProviders == null) {
            toolBehaviorProviders =
                new IToolBehaviorProvider[] { new WorkflowToolBehaviorProvider(this) };
        }
        return toolBehaviorProviders;
    }
	
	@Override
	public boolean isAutoUpdateAtStartup() {
		return false;
	}
	
	@Override
	public boolean isAutoUpdateAtRuntime() {
		return true;
	}
	
	@Override
	public boolean isAutoUpdateAtReset() {
		return true;
	}
	
	@Override
	public IGraphicsAlgorithmRendererFactory getGraphicsAlgorithmRendererFactory() {
		return new GARendererFactory(getFeatureProvider());
	}
}
