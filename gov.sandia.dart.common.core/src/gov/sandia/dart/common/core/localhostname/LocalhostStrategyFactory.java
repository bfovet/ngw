/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.common.core.localhostname;

import gov.sandia.dart.common.core.env.OS;
import gov.sandia.dart.common.core.env.OS.EOperatingSystem;


/**
 * <p>
 * A static Factory for obtaining the Strategies that may be used
 * for resolving a localhost name. Generally there is Inet,
 * an environment variable, a command execution, and a static definition.
 * There may be other contributed strategies.
 * </p>
 * <p>
 * It would be possible to change to allow a contribution approach
 * </p>
 * @author kholson
 *
 */
public class LocalhostStrategyFactory
{
  private static final AbsLocalhostStrategyProvider _win32 =
      new Win32StrategyProvider();
  
  private static final AbsLocalhostStrategyProvider _macos =
      new MacosStrategyProvider();
  
  private static final AbsLocalhostStrategyProvider _linux =
      new LinuxStrategyProvider();

  
  /**
   * 
   */
  private LocalhostStrategyFactory()
  {
  }
  
  /**
   * Returns the resolver as appropriate
   */
  public static AbsLocalhostStrategyProvider getProvider()
  {
    final EOperatingSystem os = OS.getOS();
    
    switch (os) {
      case Windows: return _win32;
      case Mac: return _macos;
      case Linux: return _linux;
      
      default: throw new IllegalStateException("LocalhostStrategyFactory "
          + "only valid for Linux/Mac/Windows.  Current OS: "+os);      
    }
  }
  


}
