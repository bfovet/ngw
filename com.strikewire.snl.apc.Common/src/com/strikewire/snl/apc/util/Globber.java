/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*
 * Created by mjgibso on May 1, 2013 at 11:41:19 AM
 */
package com.strikewire.snl.apc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;

/**
 * A class that allows for creating a regex pattern from a glob pattern,
 * and then ascertaining whether a String matches.
 * 
 * @author mjgibso
 * @author kholson
 * 
 *         Ripped off from {@link gov.sandia.simba.tools.viewer.Globber},
 *         but substantially enhanced. Some input from:
 * <a href="http://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns">Stackoverflow</a>
 */
public class Globber
{
  private Pattern m_pattern;

  private boolean _caseInsensitive = true;



  public Globber(final String pattern) throws PatternSyntaxException
  {
    setGlobPattern(pattern);
  }




  public Globber(final String pattern, final boolean caseInsensitive)
      throws PatternSyntaxException
  {
    setCaseInsensitive(caseInsensitive);
    setGlobPattern(pattern);
  }




  public void setCaseInsensitive(final boolean caseInsensitive)
  {
    _caseInsensitive = caseInsensitive;
  }
  
  
  public void setGlobPattern(final String pattern)
  {
    m_pattern = Pattern.compile(createRegexFromGlob(pattern), getFlags());    
  }



  /**
   * Returns true if the specified arg matches the previously
   * defined glob match string.
   */
  public boolean glob(final String arg)
  {
    Matcher m = m_pattern.matcher(arg);
    return m.matches();
  }



  /**
   * Returns an array of String objects that have matched
   * the previously defined glob match. The return will
   * be a subset of the input array, and may be empty, but
   * not null.
   */
  public String[] glob(String[] array)
  {
    List<String> globbed = glob(Arrays.asList(array));
    return globbed.toArray(new String[globbed.size()]);
  }



  /**
   * Returns a List of String objects that match the
   * previously defined glob match; it will be a subset of
   * the input list, and may be an empty (but not null) list.
   */
  public List<String> glob(List<String> list)
  {
    List<String> globbed = new ArrayList<String>();
    for (String elem : list) {
      if (glob(elem)) {
        globbed.add(elem);
      }
    }
    return globbed;
  }

  
  public static String createRegexFromGlob(final String glob)
  {
    if (StringUtils.isBlank(glob)) {
      return "";
    }

    StringBuilder out = new StringBuilder();

    final String line = glob.trim();

    out.append("^");

    boolean escaping = false;
    int inCurlies = 0;
    for (char currentChar : line.toCharArray()) {
      switch (currentChar) {
        case '*':
          if (escaping) out.append("\\*");
          else
            out.append(".*");
          escaping = false;
          break;
        case '?':
          if (escaping) out.append("\\?");
          else
            out.append('.');
          escaping = false;
          break;
        case '.':
        case '(':
        case ')':
        case '+':
        case '|':
        case '^':
        case '$':
        case '@':
        case '%':
        case '[':
        case ']':
          out.append('\\');
          out.append(currentChar);
          escaping = false;
          break;
        case '\\':
          if (escaping) {
            out.append("\\\\");
            escaping = false;
          }
          else
            escaping = true;
          break;
        case '{':
          if (escaping) {
            out.append("\\{");
          }
          else {
            out.append('(');
            inCurlies++;
          }
          escaping = false;
          break;
        case '}':
          if (inCurlies > 0 && !escaping) {
            out.append(')');
            inCurlies--;
          }
          else if (escaping) out.append("\\}");
          else
            out.append("}");
          escaping = false;
          break;
        case ',':
          if (inCurlies > 0 && !escaping) {
            out.append('|');
          }
          else if (escaping) out.append("\\,");
          else
            out.append(",");
          break;
        default:
          escaping = false;
          out.append(currentChar);
      }
    }

    out.append('$');
    return out.toString();
  }




  private int getFlags()
  {
    int flags = Pattern.UNIX_LINES;
    if (_caseInsensitive) {
      flags |= Pattern.CASE_INSENSITIVE;
    }

    return flags;
  }  
}
