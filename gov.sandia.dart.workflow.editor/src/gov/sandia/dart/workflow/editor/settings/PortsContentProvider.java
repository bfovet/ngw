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

import gov.sandia.dart.workflow.domain.WFNode;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class PortsContentProvider implements IStructuredContentProvider, Adapter {

	private boolean useInputs;
	private WFNode node;
	private Viewer table;

	public PortsContentProvider(boolean useInputs) {
		this.useInputs = useInputs;		
	}

	@Override
	public void dispose() {
		node.eAdapters().remove(this);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		table = viewer;
		if (oldInput != null && oldInput instanceof WFNode) {
			((WFNode) oldInput).eAdapters().remove(this);
		}
		if (newInput != null && newInput instanceof WFNode) {
			node = (WFNode) newInput;
			node.eAdapters().add(this);
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return useInputs? node.getInputPorts().toArray() : node.getOutputPorts().toArray();
	}

	@Override
	public void notifyChanged(Notification notification) {
		table.refresh();
	}

	@Override
	public Notifier getTarget() {
		return node;
	}

	@Override
	public void setTarget(Notifier newTarget) {
		if (newTarget instanceof WFNode)
		node = (WFNode) newTarget;
	}

	@Override
	public boolean isAdapterForType(Object type) {
		// No idea.
		return true;
	}

}
