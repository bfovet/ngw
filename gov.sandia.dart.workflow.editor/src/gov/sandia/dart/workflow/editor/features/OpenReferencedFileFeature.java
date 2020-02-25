/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.features;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class OpenReferencedFileFeature extends AbstractFileReferenceFeature {

	public OpenReferencedFileFeature(IFeatureProvider fp, String nodeType, String property) {
		super(fp, nodeType, property);
	}

	@Override
	public String getName() {
		return "Open Referenced File";
	}
	
	@Override
	public void execute(ICustomContext context) {
		final PictogramElement pe = context.getPictogramElements()[0];
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		if (bo instanceof WFNode) {
			WFNode node = (WFNode)bo;	
			if (canOperateOn(node)) {
				try {
					openEditor(node);
				} catch (PartInitException e) {
					WorkflowEditorPlugin.getDefault().logError("Can't open editor", e);
				}								
			}		
		}
	}

	protected IEditorPart openEditor(WFNode node) throws PartInitException {
		// TODO Would be great if we could resolve, e.g., ${workflow.workdir} here
		IFile diagramFile = getDiagramFolder().getFile(new Path(PropertyUtils.getProperty(node, property)));
		java.net.URI locationURI = diagramFile.getLocationURI();
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(locationURI);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		return IDE.openEditorOnFileStore( page, fileStore );
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		if (context.getPictogramElements().length != 1)
			return false;

		final PictogramElement pe = context.getPictogramElements()[0];
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		if (bo instanceof WFNode) {
			return canOperateOn((WFNode) bo);
		}
		return false;
	}

	@Override
	public boolean canUndo(IContext context) {
		return false;
	}
	
	@Override
	public boolean hasDoneChanges() {
		return false;
	} 
}
