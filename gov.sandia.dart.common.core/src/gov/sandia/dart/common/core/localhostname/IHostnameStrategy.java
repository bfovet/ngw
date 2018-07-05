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

/**
 * <p>
 * A Strategy (algorithm) for resolving the local hostname. The issue is that
 * a hostname may  not be straight forward to resolve. For example,
 * the standard way is via an InetAddress resolution, but there can
 * be issues with approach with multiple NICs, VPN changes, etc. Therefore,
 * different supporting different approaches is sensible.
 * </p> 
 * @author kholson
 *
 */
public interface IHostnameStrategy
{
  /**
   * Returns the key associated with this Strategy; a key must be
   * unique to a Strategy, as the key is stored in the preferences.
   */
  public String getKey();
  
  
  /**
   * Returns a description of this strategy to display
   */
  public String getDescription();
  
  
  /**
   * Returns whether this strategy needs an input, such as
   * an environment variable name
   */
  public boolean needsInput();
  
  /**
   * Returns a URI (with the scheme of host) for a hostname
   * @throws IOException if there is an issue with resolving the hostname
   */
  public URI resolve(AbsHostnameInput input) throws IOException;
  
  
  /**
   * SCHEME - The scheme to use for the URI
   */
  public static final String SCHEME = "host";  
}
