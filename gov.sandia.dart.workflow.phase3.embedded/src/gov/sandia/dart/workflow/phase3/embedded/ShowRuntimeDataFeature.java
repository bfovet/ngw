/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.strikewire.snl.apc.util.ResourceUtils;

import gov.sandia.dart.workflow.domain.NamedObject;
import gov.sandia.dart.workflow.domain.ResponseArc;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.editor.WorkflowDiagramEditor;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.settings.NOWPSettingsEditorUtils;
import gov.sandia.dart.workflow.runtime.core.Datum;
import gov.sandia.dart.workflow.runtime.core.Persistor;

/**
 * This feature is unlike any others in that it deals with runtime data rather than design-time data.
 *
 */

public class ShowRuntimeDataFeature extends AbstractCustomFeature {

	public ShowRuntimeDataFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Show runtime data";
	}
	
	@Override
	public void execute(ICustomContext context) {
		final PictogramElement pe = context.getPictogramElements()[0];
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		String data = null;
		if (bo instanceof WFArc) {
			data = getData((WFArc) bo);
		} else if (bo instanceof ResponseArc) {
			data = getData((ResponseArc) bo);
		}
		

		if (data != null) {
			NamedObject arc = (NamedObject) bo;
			
			File file = new File(data);
			if (file.exists()) {
				try {
					IFile iFile = ResourceUtils.getFileForLocation(data);
					java.net.URI locationURI = iFile.getLocationURI();
					IFileStore fileStore = EFS.getLocalFileSystem().getStore(locationURI);
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IDE.openEditorOnFileStore( page, fileStore );
				} catch (CoreException e) {
					WorkflowEditorPlugin.getDefault().logError("Can't open editor", e);
				}
			} else {
				Shell shell = Display.getCurrent().getActiveShell();			
				MessageDialog.openInformation(shell, arc.getName(), data);
			}
		}
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		if (context.getPictogramElements().length != 1)
			return false;

		final PictogramElement pe = context.getPictogramElements()[0];
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		if (bo instanceof WFArc) {
			return canOperateOn((WFArc) bo);
		} else if (bo instanceof ResponseArc) {
			return canOperateOn((ResponseArc) bo);
		}
		return false;
	}

	private boolean canOperateOn(WFArc bo) {
		return getData(bo) != null;
	}
	
	private boolean canOperateOn(ResponseArc bo) {
		return getData(bo) != null;
	}

	private String getData(WFArc bo) {
		WorkflowDiagramEditor editor = NOWPSettingsEditorUtils.getDiagramEditor(bo);				
		if (editor != null) {
			Persistor p = new Persistor(editor.getRunLocation());
			try {
				p.loadState();
				String node = bo.getTarget().getNode().getName();
				String port = bo.getTarget().getName();
				if (p.getInputs(node) == null)
					return null;
				Datum data = p.getInputs(node).get(port);
				return data.getAs(String.class).toString();

			} catch (IOException e) {
				Display.getCurrent().beep();
			}
		}
		return null;
	}
	
	private String getData(ResponseArc bo) {
		WorkflowDiagramEditor editor = NOWPSettingsEditorUtils.getDiagramEditor(bo);				
		if (editor != null) {
			Persistor p = new Persistor(editor.getRunLocation());
			try {
				p.loadState();
				String response = bo.getTarget().getName();
				return p.getResponses().get(response) == null ? null : p.getResponses().get(response).toString();
			} catch (IOException e) {
				Display.getCurrent().beep();
			}
		}
		return null;
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
