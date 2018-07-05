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
import java.net.URISyntaxException;

/**
 * <p>
 * Returns "localhost" regardless
 * </p>
 * 
 * @author kholson
 *
 */
public class StrategyLocalhost extends AbsStrategy implements IHostnameStrategy
{
  private static final String KEY = "LOCALHOST.STRATEGY.LOCALHOST";
  private static final String DESC = "host: localhost";




  /**
   * 
   */
  public StrategyLocalhost()
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
    return localhostURI();
  }




  private static URI localhostURI()
  {
    try {
      return new URI(SCHEME, "localhost", null, null);
    }
    catch (URISyntaxException e) {
      logIt.log("Error making localhost URI", e);
      return null;
    }
  }
}
