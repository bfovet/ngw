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

import gov.sandia.dart.common.core.CommonCoreActivator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class LogIt
{
  /**
   * _log -- A Logger instance for LogIt
   */
  private static final Logger _log = LogManager.getLogger(LogIt.class);
  
  LogIt()
  {
  }

  void log(String msg, Throwable t)
  {
    _log.error(msg, t);
    try {
      new Runnable() {
        
        @Override
        public void run()
        {
          String tMsg = (t != null ? t.getMessage() : "");
          CommonCoreActivator.getDefault().logError(msg + ": " + tMsg, t);
        }
      }.run();
    }
    catch (Throwable noop) {
    }    
  }
}
