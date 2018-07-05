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
 * <p>
 * Allows specifying a static value to use as the host name
 * </p>
 * @author kholson
 *
 */
public class StrategyStaticName extends AbsStrategy implements
    IHostnameStrategy
{
  public static final String KEY = "LOCALHOST.STRATEGY.UserDefined";

  private static final String DESC = "Static Entry";




  /**
   * 
   */
  public StrategyStaticName()
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
    final String userHost = input.getParameter().orElse("");

    if (StringUtils.isBlank(userHost)) {
      throw new IOException("No user specified hostname");
    }

    return makeUri(userHost);
  }
}
