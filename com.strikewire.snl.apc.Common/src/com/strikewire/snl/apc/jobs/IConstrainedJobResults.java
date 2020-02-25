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


/**
 * <p>When calling into a Constrained Job, that is a Job that may
 * be required to run on the server, that there will be a single
 * Job of that type running at a time, and that may require
 * asynchronous responses, a response may or may not have the
 * results.</p>
 * 
 * <p>This interface allow for checking if there are results. Implementing
 * classes are expected to provide the full manner for retrieving
 * the actual results of the Job, but the results type will vary by
 * the Job.</p>
 */
public interface IConstrainedJobResults
{
  public enum EResults
  {
    /**
     * Contained - The results are in the object;
     * the specific results may be obtained by making another call  
     */
    Contained,
    
    /**
     * Submitted - There are no current results in the object, but the
     * Job has submitted 
     */
    Submitted,
    
    /**
     * Shutdown - Indicates the request cannot be fulfilled due to
     * a shutdown request 
     */
    Shutdown,
    
    
    /**
     * Empty - Indicates no results are present 
     */
    Empty,
    
    /**
     * Error - Indicates an error has occurred 
     */
    Error,
    ;
  } //enum EResults
  
  
  /**
   * Indicates whether the object has results that may be retrieved
   */
  public EResults getResults();
  
} //interface IConstraitedResults
