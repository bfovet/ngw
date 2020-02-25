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
package gov.sandia.dart.common.preferences;

import gov.sandia.dart.common.preferences.date.EDateFormats;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author mjgibso
 *
 */
public class CommonPreferenceInitializer extends AbstractPreferenceInitializer
{
	
	/**
	 * 
	 */
	public CommonPreferenceInitializer()
	{}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IPreferenceStore prefs = CommonPreferencesPlugin.getDefault().getPreferenceStore();
		
		prefs.setDefault(EDateFormats.DATEFORMAT_SERVER.getPrefKey(),
		    "yyyy-MM-dd HH:mm:ss z");
		
		prefs.setDefault(EDateFormats.DATEFORMAT_CLIENT.getPrefKey(),
		    "yyyy-MM-dd HH:mm:ss z");
		
		prefs.setDefault(EDateFormats.DATEFORMAT_JOBS.getPrefKey(),
		    "yyyy-MM-dd HH:mm:ss z");
		
		initializeDefaultBrowseDirectory(prefs);
	}
	
	private void initializeDefaultBrowseDirectory(IPreferenceStore store)
	{
		String prefKey = DirectoryBrowsing.getDefaultDirectoryKey(null);
		
		store.setDefault(prefKey, getDefaultDirectory());
	}
	
	private String getDefaultDirectory()
	{
		if(workspaceIncludesOpenNonHiddenProjects())
		{
			return "";
		}
		
		String userHomeString = System.getProperty("user.home");
		File userHomeFile = new File(userHomeString!=null ? userHomeString : "");
		try {
			userHomeFile = userHomeFile.getCanonicalFile();
		} catch(Exception e) {
			userHomeFile = userHomeFile.getAbsoluteFile();
		}
		
		File retDir = userHomeFile;
		
		File docsDir = new File(userHomeFile, "Documents");
		if(docsDir.exists())
		{
			retDir = docsDir;
		}
		
		return retDir.getAbsolutePath();
	}
	
	
  private static boolean workspaceIncludesOpenNonHiddenProjects()
  {
    IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    for(IProject project : projects)
    {
      if(project.isOpen() && !project.isHidden())
      {
        return true;
      }
    }
    return false;
  }	
}
