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
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * Resolves a hostname using InetAddress resolution
 * </p>
 * 
 * @author kholson
 *
 */
public class StrategyInet extends AbsStrategy implements IHostnameStrategy
{
  public static final String KEY = "LOCALHOST.STRATEGY.INET";

  private static final String DESC = "By IP lookup";




  /**
   * 
   */
  public StrategyInet()
  {
    super(KEY, DESC);
  }




  @Override
  public boolean needsInput()
  {
    return false;
  }




  @Override
  public URI resolve(AbsHostnameInput input) throws IOException
  {
    String localHostname = "";

    InetAddress addr = InetAddress.getLocalHost();
    if (addr != null) {
      localHostname = addr.getHostName();
    }

    if (StringUtils.isBlank(localHostname)) {
      logIt.log("Null/empty hostname from Inet", null);
      throw new IOException("Unable to resolve hostname from Inet Address");
    }

    try {
      return new URI(SCHEME, localHostname, null, null);
    }
    catch (URISyntaxException e) {
      logIt.log("Error making URI for host " + localHostname, e);
      throw new IOException("Unable to generate uri for " + localHostname, e);
    }
  }

}
