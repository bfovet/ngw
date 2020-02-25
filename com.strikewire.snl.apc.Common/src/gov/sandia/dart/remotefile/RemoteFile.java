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
 *  Copyright (C) 2015
 *  Sandia National Laboratories
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  File originated by:
 *  kholson on Oct 16, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.remotefile;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @author kholson
 *
 */
public class RemoteFile
{
  private final URI _remoteLoc;
  
  /**
   * 
   */
  public RemoteFile(final URI remoteFile)
  {
    _remoteLoc = remoteFile;
    
    getModifiedTime();
  }
  
  
  
  private void getModifiedTime()
  {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(_remoteLoc.toString());

    RequestConfig toRequestCfg = getRequestConfig();
    httpGet.setConfig(toRequestCfg);
    
    try {
      RemoteFileInfo rfi = httpClient.execute(httpGet, new RemoteFileRspHndlr());
      
      System.out.println("Last Modified: " + rfi.getLastModified());
      System.out.println(rfi.getBody());
    }
    catch (ClientProtocolException e) {
      System.out.println("Bad protocol: " + e.getMessage());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    finally {
      IOUtils.closeQuietly(httpClient);
    }
  }
  
  
  private RequestConfig getRequestConfig()
  {
    RequestConfig requestConfig = RequestConfig.custom()
        .setCookieSpec(CookieSpecs.DEFAULT)
        .setExpectContinueEnabled(true)        
        .setSocketTimeout(5000)
        .setConnectTimeout(5000)
        .setConnectionRequestTimeout(5000)
        .build();
    
    return requestConfig;
  }
  
  
  public static void main(String[] args)
  {
    try {
      URI uri = new URI("https://dart-dev.ca.sandia.gov/machines/hpc-machines.xml");
      
      new RemoteFile(uri);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  private static class RemoteFileRspHndlr implements ResponseHandler<RemoteFileInfo>
  {

    @Override
    public RemoteFileInfo handleResponse(HttpResponse resp)
      throws ClientProtocolException, IOException
    {
      RemoteFileInfo ret = new RemoteFileInfo();
      
      final int status = resp.getStatusLine().getStatusCode();
      
      if (status >= 200 && status < 300) {
        Header hdr = resp.getFirstHeader("Last-Modified");
        if (hdr != null) {
          ret.setLastModified(hdr.getValue());
        }
        
        HttpEntity entity = resp.getEntity();
        ret.setBody(entity);
        
      }
      else {
        throw new ClientProtocolException("Cannot process response code " +
            status);
      }
      
      return ret;
    }
  }
  
  private static class RemoteFileInfo
  {
    /**
     * _dte - The parsed date; set to a time in the past
     */
    private LocalDateTime _dte = 
        LocalDateTime.of(1967, Month.NOVEMBER, 22, 10, 22, 00);
    
    private String _body = "";
    
    public void setLastModified(final String lm)
    {
      _dte = LocalDateTime.parse(lm, DateTimeFormatter.RFC_1123_DATE_TIME);
    }
    
    public LocalDateTime getLastModified()
    {
      return _dte;
    }
    
    public void setBody(HttpEntity entity)
    {
      if (entity != null) {
        try {
          _body = EntityUtils.toString(entity);
        }
        catch (ParseException | IOException e) {
          e.printStackTrace();
        }
      }
    }
    
    public String getBody()
    {
      return _body;
    }
  }
}
