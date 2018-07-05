/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.configuration.impl;

import gov.sandia.dart.configuration.IMode;


/**
 * <p>
 * Represents a Mode (shared, standalone) for an Execution Environment.
 * </p>
 * 
 * @author kholson
 *
 */
public class ExecEnvMode extends AbsExecEnv implements IMode
{

  public ExecEnvMode(final String mode)
  {
    super(mode);
  }




  @Override
  public String getMode()
  {
    return getValue();
  }




  @Override
  public boolean test(IMode mode)
  {
    if (mode == null) {
      return false;
    }

    return super.test(mode.getMode());
  }




  @Override
  public boolean equals(Object obj)
  {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof IMode)) {
      return false;
    }

    return (((IMode) obj).getMode().equals(getValue()));
  }


}
