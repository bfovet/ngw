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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.util.PropertyUtils;

public abstract class AbstractFileReferenceFeature extends AbstractCustomFeature {

	protected final String nodeType;
	protected final String property;

	public AbstractFileReferenceFeature(IFeatureProvider fp, String nodeType, String property) {
		super(fp);
		this.nodeType = nodeType;
		this.property = property;
	}

	public boolean canOperateOn(WFNode node) {
		if (node.getType().equals(nodeType)) {
			final String fileName = PropertyUtils.getProperty(node, property);
			if (fileName != null) {
				IFile diagramFile = getDiagramFolder().getFile(new Path(fileName));
				return diagramFile.exists();
			}	
		}
		return false;
	}

	protected IContainer getDiagramFolder() {
		URI uri = getFeatureProvider().getDiagramTypeProvider().getDiagram().eResource().getURI();
		String pathString = uri.toPlatformString(true);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pathString));
		return file.getParent();
	}

}
