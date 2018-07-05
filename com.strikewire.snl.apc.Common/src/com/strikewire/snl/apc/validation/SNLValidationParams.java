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
 *
 * Copyright (C) 2005-2011
 *   Sandia National Laboratories
 *    
 *  All Rights Reserved
 *
 * Developed under contract by:
 *  StrikeWire, LLC
 *  149 South Briggs St., Suite 102-A
 *  Erie, CO 80516
 *  (720) 890-8591
 *  support@strikewire.com
 *
 *
 */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/*
 *
 *  $Author$
 *  $Date$
 *  
 * FILE: 
 *  $Source$
 *
 *
 * Description ($Revision$):
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.validation;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Various parameters that are set for validation. Not all parameters are valid
 * for all types.
 * <ul>
 * <li>allowEmpty (true) -- allow empty String; it is possible to set a minimum
 * length && allow empty, which would mean that there is either no entry, or if
 * there is an entry it must be at least <i>N</i> characters long</li>
 * <li>minLength (0) -- the minimum length</li>
 * <li>maxLength (Integer.MAX_VALUE) -- the maximum length</li>
 * <li>allowLeadingWhitespace (false)</li>
 * <li>allowTrailingWhitespace (false)</li>
 * <li>allowSpaces (true) -- allow spaces in the entry</li>
 * <li>asEmailAddress (false) -- validate per an e-mail address</li>
 * <br/>
 * <li>filterFileCharacters (false) : block on characters that are bad for file
 * names</li>
 * <li>filterStandardCharacters (false)
 * <li>
 * <br/>
 * <li>minElements (0)</li>
 * <li>maxElements (Integer.MAX_VALUE)</li>
 * <br/>
 * <li>badFileCharactersPattern (.*)([;\\\\/<>?\"\'{}*\\|]+)(.*)$</li>
 * </ul>
 * 
 * @author kholson
 * 
 */
public class SNLValidationParams implements Serializable
{

  /**
   * serialVersionUID -
   */
  private static final long serialVersionUID = -1944252060618110080L;


  /**
   * allowEmpty - Whether an empty String is allowed
   */
  private boolean allowEmpty = true;

  /**
   * filterFileCharacters - Whether filtering for file characters should be
   * applied
   */
  private boolean filterFileCharacters = false;

  /**
   * filterStandardCharacters - Whether standard character filtering should be
   * applied
   */
//  private boolean filterStandardCharacters = false;

  /**
   * minLength - The minimum length of a String
   */
  private int minLength = 0;

  /**
   * maxLength - The maximum length of a String
   */
  private int maxLength = Integer.MAX_VALUE;

  /**
   * minElements - The minimum number of elements
   */
  private int minElements = 0;

  /**
   * maxElements - The maximum number of elements
   */
//  private int maxElements = Integer.MAX_VALUE;

  /**
   * allowLeadingWhitespace - Whether leading white space on a String is allowed
   */
  private boolean allowLeadingWhitespace = false;

  /**
   * allowTrailingWhitespace - whether whitespace may be present at the end 
   */
  private boolean allowTrailingWhitespace = false;

  /**
   * allowSpaces - Whether spaces are allowed internally
   */
  private boolean allowSpaces = true;

  /**
   * asEmailAddress - Whether to validate as an e-mail address
   */
  private boolean asEmailAddress = false;


  private Pattern patLeadingWhitespace = Pattern.compile("^(\\s)+(.*)");

  private Pattern patTrailingWhitespace = Pattern.compile("^(.*)(\\s)+$");

  private Pattern patBadFileCharacters =
      Pattern.compile("(.*)([;\\\\/<>?\"\'{}*\\|]+)(.*)$");

  
  private Pattern patEmailAddress =
      Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[_A-Za-z0-9-]+)");

  public SNLValidationParams()
  {
  }




  /**
   * True : will validate the entered data as an e-mail address,
   * which means it must be a valid user@domain.tld; though the
   * tld is not checked (it may be .com, .gov, etc., which are valid,
   * but will also allow .nowhere which is not a valid tld; if you are
   * concerned about the final tld, it is necessary to add own 
   * checking).
   */
  public SNLValidationParams asEmailAddress(boolean asEmail)
  {
    asEmailAddress = asEmail;
    return this;
  }




  public boolean getAsEmailAddress()
  {
    return asEmailAddress;
  }




  /**
   * Whether empty Strings are allowed
   */
  public SNLValidationParams allowEmpty(boolean empty)
  {
    allowEmpty = empty;
    return this;
  }




  public boolean getAllowEmpty()
  {
    return allowEmpty;
  }




  public SNLValidationParams allowSpaces(boolean allow)
  {
    allowSpaces = allow;
    return this;
  }




  public boolean getAllowSapces()
  {
    return allowSpaces;
  }




  public SNLValidationParams allowLeadingWhitespace(boolean allow)
  {
    allowLeadingWhitespace = allow;
    return this;
  }




  public boolean getAllowLeadingWhitespace()
  {
    return allowLeadingWhitespace;
  }




  public SNLValidationParams allowTrailingWhitespace(boolean allow)
  {
    allowTrailingWhitespace = allow;
    return this;
  }




  public boolean getAllowTrailingWhitespace()
  {
    return allowTrailingWhitespace;
  }




  /**
   * Whether the value should be filtered against the bad file characters
   */
  public SNLValidationParams filterFileCharacters(boolean filter)
  {
    filterFileCharacters = filter;
    return this;
  }




  public boolean getFilterFileCharacters()
  {
    return filterFileCharacters;
  }




  /**
   * The minimum length for the String value
   */
  public SNLValidationParams minLength(int min)
  {
    minLength = min;
    return this;
  }




  public int getMinLength()
  {
    return minLength;
  }




  /**
   * The maximum length for a String
   */
  public SNLValidationParams maxLength(int max)
  {
    maxLength = max;
    return this;
  }




  public int getMaxLength()
  {
    return maxLength;
  }




  /**
   * @return the minElements
   */
  public int getMinElements()
  {
    return minElements;
  }




  /**
   * @param minElements
   *          the minElements to set
   */
  public SNLValidationParams minElements(int minElements)
  {
    this.minElements = minElements;
    return this;
  }




  /**
   * @return the patLeadingWhitespace
   */
  public Pattern getPatLeadingWhitespace()
  {
    return patLeadingWhitespace;
  }




  /**
   * @param patLeadingWhitespace
   *          the patLeadingWhitespace to set
   */
  public SNLValidationParams patLeadingWhitespace(Pattern patLeadingWhitespace)
  {
    this.patLeadingWhitespace = patLeadingWhitespace;
    return this;
  }




  /**
   * @return the patBadFileCharacters
   */
  public Pattern getPatBadFileCharacters()
  {
    return patBadFileCharacters;
  }




  /**
   * @param patBadFileCharacters
   *          the patBadFileCharacters to set
   */
  public SNLValidationParams patBadFileCharacters(Pattern patBadFileCharacters)
  {
    this.patBadFileCharacters = patBadFileCharacters;
    return this;
  }

  
  public Pattern getPatTrailingWhitespace()
  {
    return patTrailingWhitespace;
  }

  public Pattern getPatEmailAddress()
  {
    return patEmailAddress;
  }

} // SNLValidationParams
