/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.jobs;

import com.strikewire.snl.apc.Common.data.IAssociableData;

/**
 * An interface for constrained jobs
 */
public interface IConstrainedJob extends IAssociableData
{

  /**
   * Indicates whether the job is currently running.
   */
  public boolean isRunning();
}
