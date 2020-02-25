/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*---------------------------------------------------------------------------*/
/*
 *  Copyright (C) 2014
 *  Sandia National Laboratories
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  File originated by:
 *  kholson on Feb 24, 2014
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart;

import java.util.Optional;
import java.util.UUID;

import com.google.gson.Gson;

/**
 * <p>
 * Represents a concrete Metrics Information object.
 * </p>
 * 
 * @author kholson
 * 
 */
public class MetricsInfo implements IMetricsInfo
{
  private String _plugin = "";
  private String _capability = "";
  private Optional<String> _data = Optional.empty();


  private final UUID OBJECT_ID = UUID.randomUUID();




  /**
   * <p>
   * If any parameter is null, it will be stored as an empty String
   * </p>
   * 
   * @param plugin
   *          The ID of the plugin
   * @param capability
   *          The capability that is being logged
   * @param data
   *          Any additional data
   */
  public MetricsInfo(final String plugin, final String capability,
                     final String data)
  {
    setPlugin(plugin);
    setCapability(capability);
    setData(data);
  }




  /**
   * 
   */
  public MetricsInfo()
  {
  }




  public MetricsInfo setPlugin(final String plugin)
  {
    _plugin = (plugin != null ? plugin : "");
    return this;
  }




  public MetricsInfo setCapability(final String capability)
  {
    _capability = (capability != null ? capability : "");
    return this;
  }




  public MetricsInfo setData(final String data)
  {
    _data = Optional.ofNullable(data);
    return this;
  }





  /**
   * Sets the data to the JSON String from the specified object. If the
   * specifled obj is null, then the data String will be an empty String. The
   * object should have private instance variables for the data to be converted.
   * Each private instance variable is added to the JSON output. See the
   * document on GSON for further information.
   * 
   * @param obj
   * @return
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Sep 26, 2016
   *         </p>
   *         <p>
   *         Permission Checks:
   *         </p>
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         </p>
   */
  public MetricsInfo setDataJSON(final Object obj)
  {
    if (obj != null) {
      Gson gson = new Gson();
      String json = gson.toJson(obj).replaceAll(",\"", ", \"");
      _data = Optional.ofNullable(json);
    }
    else {
      _data = Optional.empty();
    }

    return this;
  }




  /*
   * (non-Javadoc)
   * 
   * @see gov.sandia.dart.metrics.IMetricsInfo#getPlugin()
   */
  @Override
  public String getPlugin()
  {
    return _plugin;
  }




  /*
   * (non-Javadoc)
   * 
   * @see gov.sandia.dart.metrics.IMetricsInfo#getCapability()
   */
  @Override
  public String getCapability()
  {
    return _capability;
  }




  /*
   * (non-Javadoc)
   * 
   * @see gov.sandia.dart.metrics.IMetricsInfo#getData()
   */
  @Override
  public Optional<String> getData()
  {
    return _data;
  }




  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return OBJECT_ID.hashCode();
  }




  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof MetricsInfo)) {
      return false;
    }

    MetricsInfo mi = (MetricsInfo) obj;

    return (OBJECT_ID.equals(mi.OBJECT_ID));
  }




}
