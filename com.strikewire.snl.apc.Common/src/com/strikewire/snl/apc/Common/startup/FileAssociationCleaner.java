/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.Common.startup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.registry.EditorDescriptor;
import org.eclipse.ui.internal.registry.EditorRegistry;
import org.eclipse.ui.internal.registry.FileEditorMapping;

import com.strikewire.snl.apc.Common.CommonPlugin;
/**
 * This class is intended to be used for the migration of a workspace that lacks content type bindings
 * to one that has content type bindings.  If new bindings have been specified, the new "bound" extensions should
 * no longer have "bound" properties specified as the default.  e.g. If a new content describer for X-Files is created
 * that is applicable to the '*.x' extension, and the X-Files editor is bound to the X-Files contentType, then
 * the '*.x' extension should no longer have the X-Files editor listed as (default) in the file associations preference
 * page, as this can potentially cause an incorrect editor to be used when multiple content describers apply to a given 
 * file extension.
 * 
 */
@SuppressWarnings("restriction")
public class FileAssociationCleaner implements IStartup
{
	private static String VERSION = "com.strikewire.snl.apc.Common.startup.file_associations_version_cleaned";
	private int vnum = 0;

	@Override
  public void earlyStartup()
	{
		Job myJob = new Job("File Association Cleaner") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				return handleCleanAssociations();
			}
		};
		myJob.schedule(10*1000);
	}

	protected IStatus handleCleanAssociations() {
		EditorRegistry reg = null;
		Exception e = null;
		for(int i = 0; i < 10 && reg == null; i++)
		{
			try{
				reg = (EditorRegistry) PlatformUI.getWorkbench().getEditorRegistry();
			}catch(Exception e1){
				e = e1;
				try {
					Thread.sleep(1000*2);
				} catch (InterruptedException e2) {
					return CommonPlugin.getDefault().newErrorStatus("Thread waiting error", e2);
				}
			}
			
		}
		
		if(reg!=null)
		{
			IPreferenceStore prefs = PlatformUI.getPreferenceStore();
			cleanAssociations(prefs.getInt(VERSION));
			prefs.setValue(VERSION,vnum);
			return Status.OK_STATUS;
		}
		else{
			return CommonPlugin.getDefault().newWarningStatus("Failed to retrieve Editor Registry.  Cannot run file association cleaner", e);
		}
	}

	/**
	 * This method accepts a version number of the file associations from the preference store, and will clean any file associations
	 * added to newer versions.
	 * To add new file associations for cleaning, create a new "if" block to check the stored version number for the file associations with the new value being added.
	 * be sure to update 'vnum' accordingly after the correct editors and file extensions have been added to contentBoundEditors and contentManagedExtensions, respectively.
	 * @param versionNumber - the latest version of the File Associations from the preference store
	 */
	private void cleanAssociations(int versionNumber) {
		// TODO Auto-generated method stub
		
		final EditorRegistry reg = (EditorRegistry) PlatformUI.getWorkbench().getEditorRegistry();
		FileEditorMapping[] mappings = (FileEditorMapping[]) reg.getFileEditorMappings();
							
		final Collection<FileEditorMapping> newMappings = new ArrayList<FileEditorMapping>(Arrays.asList(mappings));
		List<String> contentManagedExtensions = new ArrayList<String>();
		List<String> contentBoundEditors = new ArrayList<String>();
		
		//Version 1
		if(versionNumber < ++vnum )
		{
			//Sierra Extensions
			contentManagedExtensions.add("i");
			contentManagedExtensions.add("in");
			contentManagedExtensions.add("inp");
			contentManagedExtensions.add("template");
			//Exodus extensions					
			contentManagedExtensions.add("e");
			contentManagedExtensions.add("e2");
			contentManagedExtensions.add("ex");
			contentManagedExtensions.add("ex2");
			contentManagedExtensions.add("exo");
			contentManagedExtensions.add("exo2");
			contentManagedExtensions.add("g");
			contentManagedExtensions.add("gen");
								
			//Content bound editors
			contentBoundEditors.add("gov.sandia.apc.editor.calore");			
			contentBoundEditors.add("gov.sandia.dart.simba.plugins.core.modeleditor");
			contentBoundEditors.add("gov.sandia.apc.seacas.blotEditor");
			contentBoundEditors.add("gov.sandia.apc.seacas.gropeEditor");
			contentBoundEditors.add("gov.sandia.apc.seacas.greposEditor");
			contentBoundEditors.add("gov.sandia.apc.seacas.exomatEditor");
			contentBoundEditors.add("gov.sandia.apc.DTATools.cubitEditor");
		}
	
		
		//Version 2
		if(versionNumber < ++vnum)
		{
			//Dakota extensions
			contentManagedExtensions.add("dak");
			contentManagedExtensions.add("i");
			contentManagedExtensions.add("in");
			
			//Jaguar Editor (for Dakota Files)
			contentBoundEditors.add("gov.sandia.dart.jaguar.editors.jaguarEditor");
		}
		
		
		//Add new content bindings here
//		if(versionNumber < ++vnum)
//		{
//			contentManagedExtensions.add(new_file_extension);
//			contentBoundEditors.add(new_editor_id);
//		}
//		
		
		
		
		
		
		
		//If there are no file associations to be cleaned, return
		if(contentBoundEditors.isEmpty() && contentManagedExtensions.isEmpty())
			return;		
		
		//Default Text editor applies to all file types
		contentBoundEditors.add("org.eclipse.ui.DefaultTextEditor");	
		
		for(FileEditorMapping mapping : mappings)
		{
			String ext = mapping.getExtension();
			// see if the mapping is for an extension we now manage through content types
			if(contentManagedExtensions.contains(ext))
			{
				IEditorDescriptor[] descriptors = mapping.getEditors();
				for(IEditorDescriptor descriptor : descriptors)
				{
						String id = descriptor.getId();
						if(contentBoundEditors.contains(id))
						{
							System.out.println("Removing editor bound to "+ext+", editor ID: "+id);
							mapping.removeEditor((EditorDescriptor) descriptor);
						}
				}
				
				descriptors = mapping.getEditors();
				if(descriptors.length < 1)
				{
					System.out.println("no editors left bound to "+ext+", removing mapping entirely");
					// remove this mapping
					newMappings.remove(mapping);
				} else {
					// there are still some, probably user defined, so update the default appropriately.
					// if the default was an editor other than the ones we now manage through content bindings
					// leave it alone.  If it was one that we now manage, set it null.
					EditorDescriptor defaultEditor = (EditorDescriptor) mapping.getDefaultEditor();
					String id = defaultEditor.getId();
					System.out.println("Remaining editors bound to "+ext+", default editor ID: "+id);
					if(contentBoundEditors.contains(id))
					{
						mapping.setDefaultEditor(null);
					}
				}
			}
			
		}
		Display.getDefault().asyncExec(
				new Runnable() {
					@Override
          public void run() {
						reg.setFileEditorMappings(newMappings.toArray(new FileEditorMapping[newMappings.size()]));
						reg.saveAssociations();
					}
				}
			);
	}
}
