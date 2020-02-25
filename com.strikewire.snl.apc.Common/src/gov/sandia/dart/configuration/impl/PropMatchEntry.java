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

import gov.sandia.dart.common.core.env.OS.EOperatingSystem;
import gov.sandia.dart.configuration.IEnv;
import gov.sandia.dart.configuration.ILan;
import gov.sandia.dart.configuration.IPropMatch;
import gov.sandia.dart.configuration.ISite;
import gov.sandia.dart.configuration.factory.SimpleExecEnvFactory;

/**
 * <p>
 * Concrete implementation of a Property Match Entry for a property file line
 * item. Id and value are defaulted to empty Strings, and Lan, Env, Site, and OS
 * are wildcarded.
 * 
 * @author kholson
 *
 */
public class PropMatchEntry implements IPropMatch
{
  private String _id = "";
  private ILan _lan = SimpleExecEnvFactory.WILDCARD_LAN;
  private IEnv _env = SimpleExecEnvFactory.WILDCARD_ENV;
  private ISite _site = SimpleExecEnvFactory.WILDCARD_SITE;
  private EOperatingSystem _os = EOperatingSystem.Any;
  private String _value = "";




  /**
   * 
   */
  public PropMatchEntry()
  {
  }




  public PropMatchEntry setId(String id)
  {
    if (id != null) {
      _id = id;
    }

    return this;
  }




  public PropMatchEntry setLan(ILan lan)
  {
    if (lan != null) {
      _lan = lan;
    }

    return this;
  }




  public PropMatchEntry setEnv(IEnv env)
  {
    if (env != null) {
      _env = env;
    }

    return this;
  }




  public PropMatchEntry setSite(ISite site)
  {
    if (site != null) {
      _site = site;
    }

    return this;
  }




  public PropMatchEntry setOperatingSystem(EOperatingSystem os)
  {
    if (os != null) {
      _os = os;
    }

    return this;
  }




  public PropMatchEntry setValue(String val)
  {
    if (val != null) {
      _value = val;
    }

    return this;
  }




  /**
   * @see gov.sandia.dart.configuration.IPropMatch#getId()
   */
  @Override
  public String getId()
  {
    return _id;
  }




  /**
   * @see gov.sandia.dart.configuration.IPropMatch#getLAN()
   */
  @Override
  public ILan getLAN()
  {
    return _lan;
  }




  /**
   * @see gov.sandia.dart.configuration.IPropMatch#getEnv()
   */
  @Override
  public IEnv getEnv()
  {
    return _env;
  }




  /**
   * @see gov.sandia.dart.configuration.IPropMatch#getSite()
   */
  @Override
  public ISite getSite()
  {
    return _site;
  }


  /**
   * @see gov.sandia.dart.configuration.IPropMatch#getOS()
   */
  @Override
  public EOperatingSystem getOS()
  {
    return _os;
  }

  /**
   * @see gov.sandia.dart.configuration.IPropMatch#getValue()
   */
  @Override
  public String getValue()
  {
    return _value;
  }

}
