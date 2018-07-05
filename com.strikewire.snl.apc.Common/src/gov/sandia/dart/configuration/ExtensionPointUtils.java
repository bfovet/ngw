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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * <p>Utilities to assist with Extension Points</p>
 * @author kholson
 *
 */
public class ExtensionPointUtils
{
  /**
   * _log -- A Logger instance for ExtensionPointUtils
   */
  private static final Logger _log =
      LogManager.getLogger(ExtensionPointUtils.class);
  
  
  private static final String EMPTY_STRING = new String("");

  /**
   * 
   */
  private ExtensionPointUtils()
  {
  }
  

  /**
   * Returns the configuration elements appropriate for specified plugin
   * and extension id; will return an empty array if nothing is found;
   * will log and return and empty array if nothing is registered.
   */
  public static IConfigurationElement[] getExtensionElements(final String pluginId,
                                                             final String extId)
  {
    IConfigurationElement[] eles = null;

    // get the class from the extension point
    IExtensionRegistry reg = Platform.getExtensionRegistry();
    IExtensionPoint extPoint =
        reg.getExtensionPoint(pluginId, extId);

    if (extPoint != null) {
      eles = extPoint.getConfigurationElements();
    }
    else {
      String msg =
          "No registered contributors to the " + "extension point "
              + extId;
      _log.warn(msg);
      IStatus status = CommonPlugin.getDefault().newWarningStatus(msg);
      CommonPlugin.getDefault().log(status);
    }


    if (eles == null) {
      eles = new IConfigurationElement[0];
    }

    return eles;
  }  

  
  /**
   * For the specified configuration elements (may be gathered
   * via getExensionElements), returns a List of Lists.
   * Each List&lt;String&gt; is the  attribute value (may be null) from
   * the specified attrs. The List&lt;&gt; matches the configuration
   * elements
   */
  public static List<List<String>> getContributors(final IConfigurationElement[] eles,
                                           final Collection<String> attrs)
  {
    List<List<String>> ret = new ArrayList<>();
    
    if (eles != null && eles.length > 0) {
      for (IConfigurationElement ele : eles) {
        List<String> attrVals = new ArrayList<>();
        for (String attr : attrs) {
          String val = ele.getAttribute(attr);
          attrVals.add(val);
        } //for : the attributes to collect
        
        ret.add(attrVals);
      } // for : all of the configuration elements
    } // if : there is anything to process
    
    
    return ret;
  }  
  
  
  public static String[][] convertToArray(List<List<String>> vals)
  {
    String[][] ret = new String[0][0];
    
    final int numVals = (vals != null ? vals.size() : 0);
    
    int numEles = 0;

    
    if (numVals > 0) {
      for (List<String> lstEles : vals) {
        numEles = Math.max(numEles, lstEles.size());
      }
          
      
      ret = new String[numVals][numEles];
      
      // keep track of outer counter
      int i = 0;
      
      // process all of the value entries
      for (List<String> lstEles : vals) {
        
        // set all of the elements in the return string to an empty string
        for (int q = 0; q < numEles; ++q) {
          ret[i][q] = EMPTY_STRING;
        }
        
        // keep track of inner counter
        int j = 0;
        
        // process all of the elements
        for (String ele : lstEles) {
          ret[i][j] = ele;
          
          // increment inner counter
          ++j;
        } // for : process all elements
        
        // increment outer counter
        ++i;
      } // for : process all of the value entries
      
    } // if : we have something to process
    
    
    return ret;
  } // convertToArray()
  
  

  /**
   * @param ele
   *          A configuration element; may be null
   * @param propertyName The name of the property on the element that
   * should be used for the instantiation
   * @return An instantiated object from the executionExtention name defined in
   *         the configuration; may return null
   * @throws CoreException
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Jun 2, 2015
   *         </p>
   *         <p>
   *         Permission Checks:
   *         </p>
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         </p>
   */
  public static Object instantiateElement(final IConfigurationElement ele,
                                      final String propertyName)
    throws CoreException
  {
    Object ext = null;

    if (ele != null) {
      ext = ele.createExecutableExtension(propertyName);
    }

    return ext;
  }  
}
