/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.metrics;

/**
 * @author kholson
 *
 */
public enum MetricsEventKeys
{
  /**
   * Add - key to add a metric 
   */
  Add("gov/sandia/dart/metrics/add"),
  
  
  ;
  
  private final String key;
  
  private MetricsEventKeys(String k)
  {
    key = k;
  }
  
  
  @Override
  public String toString()
  {
    return key;
  }
  
  public String getKey()
  {
    return key;
  }
}
