/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/**
 * 
 */
package com.strikewire.snl.apc.GUIs.settings;

import gov.sandia.dart.common.preferences.CommonPreferencesPlugin;
import gov.sandia.dart.common.preferences.settings.ISettingsViewPreferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;


/**
 * @author mjgibso
 *
 */
public abstract class AbstractSettingsEditor<E> implements ISettingsEditor<E>
{	
	protected IManagedForm mform;
	protected ScrolledForm form;
	protected FormToolkit toolkit;
	protected IMessageView messageView;
	
	@Override
	public void createPartControl(IManagedForm mform,
			IMessageView messageView,
			MultiControlSelectionProvider selectionProvider,
			IContextMenuRegistrar ctxMenuReg) {
		// if (true) throw new RuntimeException("Fake error");

		this.mform = mform;
		this.form = mform.getForm();
		this.toolkit = mform.getToolkit();
		IPreferenceStore store = CommonPreferencesPlugin.getDefault().getPreferenceStore();
		if (store.getBoolean(ISettingsViewPreferences.DRAW_BORDERS)) {
			toolkit.setBorderStyle(SWT.NULL);
			toolkit.paintBordersFor(form.getBody());
		}
		this.messageView = messageView;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof ISettingsEditor<?>))
		{
			return false;
		}
		
		if(obj == this)
		{
			return true;
		}
		
		return obj.getClass().equals(getClass());
	}
	
	@Override
	public int hashCode()
	{
		return getClass().hashCode();
	}
	
	@Override
	public boolean setFocus()
	{
		return false;
	}
	
	@Override
	public boolean isReusable() 
	{
		return true;
	}
}
