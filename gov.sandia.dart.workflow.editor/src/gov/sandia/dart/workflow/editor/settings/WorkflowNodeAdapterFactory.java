/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.settings;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.graphiti.ui.internal.parts.IDiagramEditPart;

import com.strikewire.snl.apc.GUIs.settings.ISettingsEditor;
import com.strikewire.snl.apc.util.ExtensionPointUtils;

import gov.sandia.dart.workflow.domain.Note;
import gov.sandia.dart.workflow.domain.Response;
import gov.sandia.dart.workflow.domain.ResponseArc;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.WorkflowFeatureProvider;

public class WorkflowNodeAdapterFactory implements IAdapterFactory {

	private Map<String, Class<ISettingsEditor<? extends Object>>> customEditors;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {		
		if(adapterType == ISettingsEditor.class) {
			if (adaptableObject instanceof WFNode) {
				WFNode cc = (WFNode) adaptableObject;
				return getSettingsEditor(cc);	
			} else if (adaptableObject instanceof Note) {
				Note cc = (Note) adaptableObject;
				return getSettingsEditor(cc);	
			} else if (adaptableObject instanceof Response) {
				Response cc = (Response) adaptableObject;
				return getSettingsEditor(cc);
			} else if (adaptableObject instanceof WFArc) {
				WFArc cc = (WFArc) adaptableObject;
				return getSettingsEditor(cc);
			} else if (adaptableObject instanceof ResponseArc) {
				ResponseArc cc = (ResponseArc) adaptableObject;
				return getSettingsEditor(cc);

			} else if (adaptableObject instanceof IDiagramEditPart) {
				IDiagramEditPart cc = (IDiagramEditPart) adaptableObject;
				return getSettingsEditor(cc);
			} else if (adaptableObject instanceof ContainerShapeEditPart) {
				ContainerShapeEditPart cc = (ContainerShapeEditPart) adaptableObject;
				return getSettingsEditor(cc);
			}  else if (adaptableObject instanceof ConnectionEditPart) {
				ConnectionEditPart cc = (ConnectionEditPart) adaptableObject;
				return getSettingsEditor(cc);
			} 

		} else if (adapterType == IFile.class) {
			if (adaptableObject instanceof IDiagramEditPart) {
				IDiagramEditPart part = (IDiagramEditPart) adaptableObject;
				Resource resource = part.getPictogramElement().getGraphicsAlgorithm().eResource();
				URI uri = resource.getURI();
				if (uri.isPlatformResource()) {
					String path = uri.toPlatformString(true);
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					IWorkspaceRoot root = workspace.getRoot();
					return root.findMember(new Path(path));
				}
			}
		}

		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Class[] getAdapterList() {
		return new Class[] {ISettingsEditor.class};
	}

	private synchronized ISettingsEditor<? extends Object> getSettingsEditor(final WFNode cc)	
	{
		loadExtensions();
		String type = cc.getType();
		Class<? extends ISettingsEditor<? extends Object>> clazz = customEditors.get(type);
		if (clazz != null)
			try {
				return clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				WorkflowEditorPlugin.getDefault().logError(String.format("Can't create custom editor for type %s", type), e);
				// FALL THROUGH
			}		
		return new WFNodeSettingsEditor();
	}

	private void loadExtensions() {
		if (customEditors != null)
			return;
		 customEditors = new ConcurrentHashMap<>();
		 List<IConfigurationElement> elements = ExtensionPointUtils.getConfigurationElements(WorkflowEditorPlugin.PLUGIN_ID, "nodeTypeEditor", "editor");
		 for (IConfigurationElement element: elements) {
			 String name = element.getAttribute("nodeType");
			 try {
				 @SuppressWarnings("unchecked")
				Class<ISettingsEditor<? extends Object>> editorClass =
					(Class<ISettingsEditor<? extends Object>>) element.createExecutableExtension("settingsEditor").getClass();					
				 customEditors.put(name, editorClass);
			 } catch (Exception e) {
					WorkflowEditorPlugin.getDefault().logError(String.format("Can't load custom editor for type %s", name), e);
			 }			
		 }

	}

	private ISettingsEditor<? extends Object> getSettingsEditor(final Note cc)
	{		
		return new NoteSettingsEditor();
	}

	private ISettingsEditor<? extends Object> getSettingsEditor(final Response cc)
	{		
		return new ResponseSettingsEditor();
	}

	private ISettingsEditor<? extends Object> getSettingsEditor(final WFArc cc)
	{		
		return new WFArcSettingsEditor();
	}
	
	private ISettingsEditor<? extends Object> getSettingsEditor(final ResponseArc cc)
	{		
		return new ResponseArcSettingsEditor();
	}

	private ISettingsEditor<? extends ContainerShapeEditPart> getSettingsEditor(final ContainerShapeEditPart cc)
	{
		return new ContainerShapeEditPartSettingsEditor();
	}

	private ISettingsEditor<? extends ConnectionEditPart> getSettingsEditor(final ConnectionEditPart cc)
	{
		return new ConnectionEditPartSettingsEditor();
	}

	private ISettingsEditor<? extends IDiagramEditPart> getSettingsEditor(final IDiagramEditPart cc)
	{
		if (cc.getFeatureProvider() instanceof WorkflowFeatureProvider)
			return new DiagramEditPartSettingsEditor();
		else
			return null;
	}
}
