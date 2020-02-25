/*
 * Created on Feb 24, 2019 at 2:50:30 PM by mjgibso
 */
package com.strikewire.snl.apc.GUIs.settings;

import org.eclipse.core.runtime.IStatus;

/**
 * @author mjgibso
 *
 */
public interface IApplyableSettingsEditor<T> extends ISettingsEditor<T>
{
	public enum ApplyStatus
	{
		
		/**
		 * Green light to apply
		 */
		OK,
		
		/**
		 * There are out-of-sync changes in the underlying data-model, but it is believed
		 * the changes in the editor should be able to be merged in.
		 */
		CONFLICTING_MERGEABLE,
		
		/**
		 * There are out-of-sync changes in the underlying data-model, and there is no
		 * mechanism to merge, but it is believed the changes in the editor can overwrite
		 * the out-of-sync changes in the underlying data model.
		 */
		CONFLICTING_REPLACABLE,
		
		/**
		 * There are out-of-sync changes in the underlying data-model, and there is no
		 * mechanism to merge, or overwrite the changes to the underlying data-model.
		 * The changes in the editor will have to be aborted.
		 */
		CONFLICTING_IRRECONCILABLE,
		
		;
	}
	
	void setDirtyStateListener(IApplyableSettingsEditorListener listener);
	
	boolean isDirty();
	
	IStatus performApply();
	
	void performCancel();
	
	ApplyStatus getApplyStatus();
}
