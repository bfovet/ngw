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
 * @author kholson
 *
 */
public class HostnameInput extends AbsHostnameInput
{

  /**
   * 
   */
  public HostnameInput(String input)
  {
    super.setParameter(input);
  }

  
  @Override
  public Optional<String> getParameter()
  {
    return super.getParameter();
  }
}
