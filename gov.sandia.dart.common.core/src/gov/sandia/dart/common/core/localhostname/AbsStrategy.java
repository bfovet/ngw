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
import java.util.Objects;

/**
 * <p>
 * An abstract class for a Hostname Resolution strategy; allows
 * for some simplification of the class hierarchy by providing
 * default implementations for some behavior.
 * </p>
 * @author kholson
 *
 */
public abstract class AbsStrategy implements IHostnameStrategy
{

  protected static final LogIt logIt = new LogIt();

  protected static final IHostnameStrategy LOCALHOST_STRAT =
      new StrategyLocalhost();


  /**
   * KEY - The key for the strategy
   */
  private final String KEY;
  
  /**
   * DESC - The description for the strategy
   */
  private final String DESC;



  protected AbsStrategy(String key,
                        String description)
  {
    Objects.requireNonNull(key, "Null Key for strategy!");
    Objects.requireNonNull(description, "Null Description for strategy");
    KEY = key;
    DESC = description;
  }




  @Override
  public String getKey()
  {
    return KEY;
  }


  @Override
  public String getDescription()
  {
    return DESC;
  }
  

  /**
   * Convenience method to make a host URI for a specified host;
   * will return LOCALHOST if cannot create the URI
   */
  protected URI makeUri(String host) throws IOException
  {
    try {
      return new URI(SCHEME, host, null, null);
    }
    catch (URISyntaxException e) {
      logIt.log("Error making URI for host " + host, e);
      return LOCALHOST_STRAT.resolve(new EmptyHostnameInput());
    }
  }




  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((KEY == null) ? 0 : KEY.hashCode());
    return result;
  }




  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof IHostnameStrategy)) {
      return false;
    }
    IHostnameStrategy other = (IHostnameStrategy) obj;
    if (KEY == null) {
      if (other.getKey() != null) {
        return false;
      }
    }
    else if (!KEY.equals(other.getKey())) {
      return false;
    }
    return true;
  }

  
  @Override
  public String toString()
  {
    return String.format("%s (%s)", DESC, KEY);
  }
}
