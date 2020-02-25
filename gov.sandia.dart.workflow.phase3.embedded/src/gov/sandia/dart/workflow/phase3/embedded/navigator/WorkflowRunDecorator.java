/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.navigator;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationContext;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;

import gov.sandia.dart.workflow.phase3.embedded.EmbeddedWorkflowPlugin;
import gov.sandia.dart.workflow.runtime.Main;

public class WorkflowRunDecorator extends LabelProvider implements ILightweightLabelDecorator, IResourceChangeListener {	
	private static final String WORKFLOW_STATE = "workflow.status.log";
	public static final String ID = "gov.sandia.dart.workflow.phase3.embedded.navigator";

	public WorkflowRunDecorator() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);	
	}
	
	@Override
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof IFolder) {
			doDecoration(decoration, (IFolder) element);
			return;
		}

		IResource resource = (IResource) Platform.getAdapterManager().loadAdapter(element, IResource.class.getName());
		if (resource instanceof IFolder) {
				doDecoration(decoration, (IFolder) resource);			
		}
	}

	private void doDecoration(IDecoration decoration, IFolder folder) {
		IFile file = folder.getFile(WORKFLOW_STATE);
		if (file.exists()) {
			((DecorationContext) decoration.getDecorationContext()).putProperty(IDecoration.ENABLE_REPLACE, Boolean.TRUE); 
			decoration.addOverlay(EmbeddedWorkflowPlugin.getImageDescriptor(EmbeddedWorkflowPlugin.RUN_FOLDER), IDecoration.REPLACE);

			boolean running = true;
			boolean aborted = false;
			boolean succeeded = false;
			// TODO Central parser for these!
			try (FileReader sr = new FileReader(file.getLocation().toFile())) {
				for (String s: IOUtils.readLines(sr)) {
					if (s.contains(Main.ERROR_REPORTING_RESPONSES)) {
						aborted = true; 
						running = false;
						break;								
					} else if (s.startsWith("ABORT: ")) {
						aborted = true; 
						running = false;
						break;
					} else if (s.startsWith("STOP")) {
						succeeded = true;
						running = false;
						break;
					}
				}
			} catch (IOException e) {
				EmbeddedWorkflowPlugin.getDefault().logError("Failed to read status", e);
			}
			if (running) {
				decoration.addSuffix(" [RUNNING]");
				ImageDescriptor imageDescriptor = EmbeddedWorkflowPlugin.getImageDescriptor(EmbeddedWorkflowPlugin.RUNNING);
				decoration.addOverlay(imageDescriptor, IDecoration.TOP_RIGHT);
			} else if (succeeded) {
				decoration.addSuffix(" [SUCCESS]");
				decoration.addOverlay(EmbeddedWorkflowPlugin.getImageDescriptor(EmbeddedWorkflowPlugin.SUCCESSFUL), IDecoration.TOP_RIGHT);

			} else if (aborted) {
				decoration.addSuffix(" [FAILED]");
				decoration.addOverlay(EmbeddedWorkflowPlugin.getImageDescriptor(EmbeddedWorkflowPlugin.ERROR), IDecoration.TOP_RIGHT);

			}
		}
	}
	
	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);	
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		if (delta.findMember(new Path(WORKFLOW_STATE)) != null) {
			Display.getDefault().asyncExec( () -> {
				IDecoratorManager decoratorManager = PlatformUI.getWorkbench().getDecoratorManager();
				decoratorManager.update(ID);
			});
		}
	}
}
