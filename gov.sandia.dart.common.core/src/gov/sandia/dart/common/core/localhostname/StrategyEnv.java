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

import java.io.IOException;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>Uses an environment variable to obtain a value to be
 * used as the hostname.
 * </p>
 * @author kholson
 *
 */
public class StrategyEnv extends AbsStrategy implements IHostnameStrategy
{
  public static final String KEY = "LOCALHOST.STRATEGY.ENV";

  private static final String DESC = "By environment variable";
  
  
  /**
   * 
   */
  public StrategyEnv()
  {
    super(KEY, DESC);
  }

  


  
  @Override
  public boolean needsInput()
  {
    return true;
  }
  
  
  @Override
  public URI resolve(AbsHostnameInput input) throws IOException
  {
    final String envvar = input.getParameter().orElse("");
    
    if (StringUtils.isBlank(envvar)) {
      throw new IOException("No envvar specified");
    }
    
    String host = System.getenv(envvar);
    
    if (StringUtils.isBlank(host)) {
      logIt.log("Null/empty return for getenv on " + envvar, null);
      throw new IOException("No value for " + envvar);
    }
    
    return makeUri(host);
  }
}
