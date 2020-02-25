/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author kholson
 *
 */
@SuppressWarnings({ "PMD.ClassNamingConventions",
"PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal" })
public class VersionInfo
{
  /**
   * BUNDLE_NAME - 
   */
  private static final String BUNDLE_NAME =
      "gov.sandia.dart.workflow.phase3.version"; //$NON-NLS-1$
  
  private static final String KEY = "ngw.core.version";

  /**
   * RESOURCE_BUNDLE - 
   */
  private static final ResourceBundle RESOURCE_BUNDLE =
      ResourceBundle.getBundle(BUNDLE_NAME);




  private VersionInfo()
  {
  }


  /**
   * @return
   * @author kholson
   * @since Feb 12, 2019
   */
  public static String getVersion()
  {
    try {
      return RESOURCE_BUNDLE.getString(KEY);
    }
    catch (MissingResourceException e) {
      return '!' + KEY + '!';
    }
    
  }

}
