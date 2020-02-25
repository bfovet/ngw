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
 *  Copyright (C) 2013
 *  Sandia National Laboratories
 *
 *  File originated by:
 *  StrikeWire, LLC
 *  149 South Briggs St., #102-A
 *  Erie, CO 80516
 *  (720) 890-8590
 *  support@strikewire.com
 *
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.validation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IInputValidator;

/**
 * Validates that the supplied text is a valid directory name
 * 
 * @author kholson
 * 
 */
public class SNLDirectoryNameValidator implements IInputValidator
{
  private boolean _bAllowStartWithDot = false;

  private boolean _bErrorOnEmpty = true;

  private static final Set<String> _reservedNames = initReservedNames();

  private boolean _bAllowDirectorySeparator = false;


  private boolean _bAllowTrailingSpace = false;




  /**
   * 
   */
  public SNLDirectoryNameValidator()
  {
  }




  private static final Set<String> initReservedNames()
  {
    Set<String> retSet = new HashSet<String>();

    retSet.add("CON");
    retSet.add("PRN");
    retSet.add("AUX");
    retSet.add("NUL");
    retSet.add("NULL");

    for (int i = 0; i < 10; ++i) {
      retSet.add("COM" + i);
      retSet.add("LPT" + i);
    }


    return retSet;
  }




  /**
   * Set to allow a leading dot on the directory name
   */
  public void setAllowLeadingDot(boolean allowLeadingDot)
  {
    _bAllowStartWithDot = allowLeadingDot;
  }




  /**
   * If true, then empty string validation displays error
   */
  public void setAllowEmptyName(boolean allowEmptyName)
  {
    _bErrorOnEmpty = !allowEmptyName;
  }




  public void setAllowDirectorySeparator(boolean allowSlash)
  {
    _bAllowDirectorySeparator = allowSlash;
  }




  public void setAllowTrailingSpace(boolean allowTrailingSpace)
  {
    _bAllowTrailingSpace = allowTrailingSpace;
  }




  /**
   * Returns a pattern to ensure the starting of a directory is OK; allows
   * <ul>
   * <li>\w (character, number, _)</li>
   * <li>. (a period, if allowable)</li>
   * <li>/ (directory separator if allowed)</li> </u/>
   */
  private Pattern getDirNameStartPattern()
  {
    StringBuilder sb = new StringBuilder();

    sb.append("^(");

    sb.append("\\w"); // may always be number, letter, or _

    if (_bAllowStartWithDot) {
      sb.append("\\.");
    }

    if (_bAllowDirectorySeparator) {
      sb.append("/");
      sb.append("\\");
    }

    sb.append(").*$");


    Pattern pat = Pattern.compile(sb.toString());

    return pat;
  }




  /**
   * Returns a pattern which looks for other bad characters in a directory name,
   * allowing anything as the starting character. This approach is a white list
   * approach, where we are specifying the characters we want to allow.
   */
  private Pattern getDirectoryNamePattern()
  {
    StringBuilder sb = new StringBuilder();

    sb.append("^."); // assume the first character has been validated

    sb.append("[");

    sb.append("\\w-\\."); // alpha and underscore, plus a period

    sb.append(" "); // space, yuck

    sb.append("\\#\\^\\-\\+\\[\\]{}~\\(\\)&");

    sb.append("]*$");


    Pattern pat = Pattern.compile(sb.toString());

    return pat;
  }




  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
   */
  @Override
  public String isValid(String newText)
  {
    // null indicates no error
    String msg = null;

    // if we do not have anything in the inbound variable,
    // handle as appropriate: either we will display an error or
    // return null indicating no error
    if (StringUtils.isEmpty(newText)) {
      if (_bErrorOnEmpty) {
        return "Name may not be empty";
      }
      else {
        // whether newText is null or empty, no other tests will work,
        // so leave from here w/o error
        return null;
      }
    } //if : we are null or empty string


    // see if the name starts with a "." and we don't want it to
    if (!_bAllowStartWithDot && newText.startsWith(".")) {
      return "Name may not start with a \".\"";
    }


    // get the first & last character
    char chFirst = newText.charAt(0);
    char chLast = newText.charAt(newText.length() - 1);


    // we will be buggers and not allow the name to start with a space
    if (Character.isWhitespace(chFirst)) {
      return "Name may not start with a space";
    }


    // if we are disallowing trailing spaces, say so
    if (!_bAllowTrailingSpace && Character.isWhitespace(chLast)) {
      return "Name may not end with a space";
    }


    // if we are not allowing slash, then avoid it
    if (!_bAllowDirectorySeparator) {
      if (newText.contains("/") || newText.contains("\\")) {
        return "Name may not contain directory separator (/ or \\)";
      }
    }

    // a directory must start with a letter or a number, or _
    Pattern dirStartPat = getDirNameStartPattern();
    Matcher dirStartMatcher = dirStartPat.matcher(newText);

    if (!dirStartMatcher.matches()) {
      return "Invalid starting character: " + newText.charAt(0);
    }


    // a directory must only have certain characters in it
    Pattern dirNamePat = getDirectoryNamePattern();
    Matcher dirNameMatcher = dirNamePat.matcher(newText);

    if (!dirNameMatcher.matches()) {
      return "Invalid character in name";
    }

    // check for reserved file names
    Iterator<String> it = _reservedNames.iterator();
    while (it.hasNext()) {
      String chk = it.next();
      if (newText.equalsIgnoreCase(chk)) {
        return "Name may not be a reserved name of " + newText;
      }
    }

    return msg;
  }

}
