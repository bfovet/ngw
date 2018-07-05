/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.common.preferences;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * <p>
 * This class contains static methods to assist with the retrieving and storage
 * of directories that are used as the default location in the dialogs.
 * </p>
 * 
 * @author kholson
 * @author mjgibso
 *
 */
public class DirectoryBrowsing
{
  /**
   * DEFAULT_DIR - Default directory to initialize file browsers to
   */
  private static final String DEFAULT_DIR =
      "gov.sandia.dart.sierraui.vtkGraphicsViewer.defaultBrowseDir";




  /**
   * Prevent instantiation (*sigh*)
   */
  private DirectoryBrowsing()
  {
  }



  /**
   * Returns a the default directory for browsing
   */
  public static String getDefaultDirectory()
  {
    return getDefaultDirectory(null);
  }



  /**
   * For the specified context, returns the default directory
   * as stored in the preferences; if no context has been
   * established, then returns the defaultDirectory()
   */
  public static String getDefaultDirectory(String context)
  {
    final IPreferenceStore store =
        CommonPreferencesPlugin.getDefault().getPreferenceStore();
    
    String defaultDir = "";
    
    //
    // if we have a potential context, then attempt to get the
    // value from the preference store based upon the
    // context
    //
    if (StringUtils.isNotBlank(context)) {
      String prefKey = getDefaultDirectoryKey(context);

      defaultDir = store.getString(prefKey);
    }

    //
    // if we did not find the preference, or the context was null/empty,
    // then just use the DEFAULT_DIR
    //
    if (StringUtils.isBlank(defaultDir)) {
      defaultDir = store.getString(DEFAULT_DIR);
    }

    return defaultDir;
  }



  /**
   * 
   */
  public static void setDefaultDirectory(String dir)
  {
    setDefaultDirectory(null, dir);
  }




  public static String getDefaultDirectoryKey(String context)
  {
    String prefKey = DEFAULT_DIR;
    if (StringUtils.isNotBlank(context)) {
      prefKey += '.' + context;
    }
    return prefKey;
  }




  public static void setDefaultDirectory(String context, String dir)
  {
    String prefKey = getDefaultDirectoryKey(context);
    CommonPreferencesPlugin.getDefault()
        .getPreferenceStore()
        .setValue(prefKey, dir);
  }

}
