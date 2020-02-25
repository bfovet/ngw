/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.graphiti.features.IFeatureProvider;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.phase3.embedded.execution.EmbeddedWorkflowJob;

public class RunFromNodeFeature extends AbstractSpecificRunFeature {

	public RunFromNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Run from here";
	}

	@Override
	protected Job getWorkflowJob(WFNode selectedNode, IFile file, IPath path) {
		String name = selectedNode.getName();
		return new EmbeddedWorkflowJob("Workflow " + file.getName(), file, path).setStartNode(name);
	}

	
}
