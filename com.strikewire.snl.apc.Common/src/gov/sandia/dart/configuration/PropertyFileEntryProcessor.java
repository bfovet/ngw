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

import gov.sandia.dart.common.core.env.OS.EOperatingSystem;
import gov.sandia.dart.configuration.factory.SimpleExecEnvFactory;
import gov.sandia.dart.configuration.impl.PropMatchEntry;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>Processes a property file that has entries in the form of
 * Id.LAN.ENV.SITE.OS=value
 * @author kholson
 *
 */
public class PropertyFileEntryProcessor
{
  /**
   * _log -- A Logger instance for PropertyFileEntryProcessor
   */
  private static final Logger _log =
      LogManager.getLogger(PropertyFileEntryProcessor.class);
  
  private final String _pluginId;
  private final String _filename;
  
  /**
   * 
   */
  public PropertyFileEntryProcessor(final String pluginId,
                                    final String filename)
  {
    _pluginId = pluginId;
    _filename = filename;
  }
  
  
  /**
   * Loads the properties file and processes it, calling the
   * visitor if the entry in the properties file (having 
   * the correct format) matches the specified execEnv 
   */
  public Set<IPropMatch> process(IExecutionEnvironment execEnv,
                      IPropMatchVisitor visitor)
  {
    final Set<IPropMatch> ret = new HashSet<>();

    // not currently used, but save future typing if change
//    final ILan lan = execEnv.getLan();
//    final IEnv env = execEnv.getEnv();
//    final ISite site = execEnv.getSite();
//    final EOperatingSystem os = execEnv.getOS();
    
    //
    // get the properties
    //
    Properties props = PropertyLoader.loadProperties(_pluginId, _filename);
    
    if (props != null && ! props.isEmpty()) {
      for (String key : props.stringPropertyNames()) {
        String value = props.getProperty(key);
        
        // split the key into pieces
        String[] parts = key.split("\\.");
        
        if (parts.length != 5) {
          _log.trace("Not processing entry {}: {}", parts.length, key);
          continue;
        }
        
        //
        // make checking information
        //
        String id = parts[0];
        ILan chkLan = SimpleExecEnvFactory.getInstance().makeLan(parts[1]);
        IEnv chkEnv = SimpleExecEnvFactory.getInstance().makeEnv(parts[2]);
        ISite chkSite = SimpleExecEnvFactory.getInstance().makeSite(parts[3]);
        EOperatingSystem chkOS = EOperatingSystem.toOS(parts[4]);  

        
        IPropMatch pm = new PropMatchEntry().setId(id)
            .setLan(chkLan)
            .setEnv(chkEnv)
            .setSite(chkSite)
            .setOperatingSystem(chkOS)
            .setValue(value);

        if (visitor.accept(execEnv, pm)) {
          ret.add(pm);
        }

      } //for : process all entries in the properties file      
    } //if : we have something to process
    
    return ret;
  }
  
  
  public static boolean defaultMatcher(IExecutionEnvironment execEnv,
                                       IPropMatch pm)
  {
    boolean bRet = false;

    final ILan lan = execEnv.getLan();
    final IEnv env = execEnv.getEnv();
    final ISite site = execEnv.getSite();
    final EOperatingSystem os = execEnv.getOS();
    
    if (lan.test(pm.getLAN()) 
        && env.test(pm.getEnv())
        && site.test(pm.getSite())
        && os.test(pm.getOS())) {
      
      bRet = true;
    }
    
    
    return bRet;
  }
  
}
