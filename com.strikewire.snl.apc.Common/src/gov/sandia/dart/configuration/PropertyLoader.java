/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/

package gov.sandia.dart.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IPath;

import com.strikewire.snl.apc.util.ResourceUtils;

/**
 * <p>Loads properties for the specified file in the specified plugin.</p>
 * @author kholson
 *
 */
public class PropertyLoader
{
  /**
   * _log -- A Logger instance for PropertyLoader
   */
  private static final Logger _log = LogManager.getLogger(PropertyLoader.class);
  /**
   * 
   */
  private PropertyLoader()
  {
  }
  
  /**
   * <p>Loads the properties file from the specified plugin id and
   * returns all of the entries in a Properties object</p>
   */
  public static Properties loadProperties(final String pluginId,
                                    final String filename)
  {
    Properties props = new Properties();

    IPath path = ResourceUtils.getPathToFile(pluginId, filename);


    if (path != null) {
      // use the autoclose Java 7 try approach
      try (InputStream is = new FileInputStream(path.toString())) {
        props.load(is);
      }
      catch (IOException e) {
        _log.error("Error reading file " + filename, e);
      }
    }

    return props;
  }  

}
