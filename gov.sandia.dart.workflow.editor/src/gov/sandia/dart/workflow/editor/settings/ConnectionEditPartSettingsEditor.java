/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.settings;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.ui.internal.parts.ConnectionEditPart;
import org.eclipse.ui.forms.IManagedForm;

import com.strikewire.snl.apc.GUIs.settings.AbstractSettingsEditor;
import com.strikewire.snl.apc.GUIs.settings.IContextMenuRegistrar;
import com.strikewire.snl.apc.GUIs.settings.IMessageView;
import com.strikewire.snl.apc.GUIs.settings.ISettingsEditor;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;

@SuppressWarnings("restriction")
public class ConnectionEditPartSettingsEditor extends AbstractSettingsEditor<ConnectionEditPart> {
	private ConnectionEditPart part;
	@SuppressWarnings("rawtypes")
	private ISettingsEditor child;
	private MultiControlSelectionProvider selectionProvider;
	private IContextMenuRegistrar ctxMenuReg;

	@Override
	public void createPartControl(IManagedForm mform, IMessageView messageView,
			MultiControlSelectionProvider selectionProvider,
			IContextMenuRegistrar ctxMenuReg) {
		super.createPartControl(mform, messageView, selectionProvider, ctxMenuReg);
		this.selectionProvider = selectionProvider;
		this.ctxMenuReg = ctxMenuReg;
	}
	
	@Override
	public void setNode(ConnectionEditPart node) {
		part = node;
		Object comp = getBusinessObject();
		if (comp != null) {
			if (child != null && child.getNode() != null && child.getNode().getClass() != comp.getClass()) {
				child.dispose();				
				child = null;
			}				
			
			if (child == null) {
				Object adapter = getAdapter(comp, ISettingsEditor.class);
				if (adapter != null) {
					child = (ISettingsEditor) adapter;
					child.createPartControl(mform, messageView, selectionProvider, ctxMenuReg);
				}
			}
			
			if (child != null && comp != null)
				child.setNode(comp);
		}
	}

	public Object getBusinessObject() {
		IFeatureProvider fp = part.getFeatureProvider();
		Object bo = fp.getBusinessObjectForPictogramElement(part.getPictogramElement());
		return bo;
	}

	@Override
	public ConnectionEditPart getNode() {
		return part;
	}

	@Override
	public void dispose() {
		if (child != null) {
			child.dispose();
			child = null;
		}
		
	}
	
	private Object getAdapter(Object node, Class<?> adapterType)
	{
		Object adapter = Platform.getAdapterManager().getAdapter(node, adapterType);
		if(adapterType.isInstance(adapter))
		{
			return adapter;
		}
		
		if(node instanceof IAdaptable)
		{
			adapter = ((IAdaptable) node).getAdapter(adapterType);
		}
		
		if(adapterType.isInstance(adapter))
		{
			return adapter;
		}
		
		return null;
	}
	
	@Override
	public boolean isReusable() 
	{
		return false;
	}
}
