/*
 * Created on Feb 24, 2019 at 2:48:32 PM by mjgibso
 */
package com.strikewire.snl.apc.GUIs.settings;

import com.strikewire.snl.apc.GUIs.settings.IApplyableSettingsEditor.ApplyStatus;

/**
 * @author mjgibso
 *
 */
public interface IApplyableSettingsEditorListener
{
	void dirtyStateChanged();
	
	void applyStatusChanged(ApplyStatus newStatus);
}
