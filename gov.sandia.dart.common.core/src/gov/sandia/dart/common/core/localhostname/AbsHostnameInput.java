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

import java.util.Optional;

/**
 * <p>Input for a Hostname Resolution Strategy; it is the
 * contract for specifying the input to a Strategy in order
 * to resolve the hostname for the local machine.</p>
 * @author kholson
 *
 */
public abstract class AbsHostnameInput
{
  private Optional<String> parameter = Optional.empty();
  
  /**
   * 
   */
  public AbsHostnameInput()
  {
  }

  
  protected void setParameter(String p)
  {
    parameter = Optional.ofNullable(p);
  }
  
  
  
  public Optional<String> getParameter()
  {
    return parameter;
  }
}
