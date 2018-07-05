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

import gov.sandia.dart.workflow.domain.NamedObjectWithProperties;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class PropertiesContentProvider implements IStructuredContentProvider, Adapter {

	private NamedObjectWithProperties node;
	private Viewer table;

	@Override
	public void dispose() {
		node.eAdapters().remove(this);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		table = viewer;
		if (oldInput != null && oldInput instanceof NamedObjectWithProperties) {
			((NamedObjectWithProperties) oldInput).eAdapters().remove(this);
		}
		if (newInput != null && newInput instanceof NamedObjectWithProperties) {
			node = (NamedObjectWithProperties) newInput;
			node.eAdapters().add(this);
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return  node.getProperties().toArray();
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
		if (newTarget instanceof NamedObjectWithProperties)
		node = (NamedObjectWithProperties) newTarget;
	}

	@Override
	public boolean isAdapterForType(Object type) {
		// No idea.
		return true;
	}

}
