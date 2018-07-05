/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/

package gov.sandia.dart.configuration;

import gov.sandia.dart.common.core.env.OS.EOperatingSystem;

/**
 * <p>When parsing a property file that contains values
 * in the Identifier.LAN.ENV.SITE.OS=value format, this object
 * provides access to the various pieces after it has been parsed.
 * @author kholson
 *
 */
public interface IPropMatch
{
  public String getId();
  
  public ILan getLAN();
  
  public IEnv getEnv();
  
  public ISite getSite();
  
  public EOperatingSystem getOS();
  
  public String getValue();
}
