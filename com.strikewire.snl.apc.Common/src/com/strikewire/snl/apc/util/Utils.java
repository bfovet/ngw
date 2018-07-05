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
 * Created by Marcus Gibson
 * On Mar 31, 2006 at 5:48:51 PM
 */
package com.strikewire.snl.apc.util;

import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.ICountable;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.internal.expressions.CountExpression;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

import com.strikewire.snl.apc.Common.CommonPlugin;

import gov.sandia.dart.env.HostnameProvider;

/**
 * @author Marcus Gibson
 *
 */
public class Utils
{
	public static final String DEFAULT_ATTRIBUTE_DELIMITER = ",";
	public static final String DEFAULT_VALUE_DELIMITER = "=";
	private static final int NUMBER_MAX_LENGTH = String.valueOf(Long.MAX_VALUE).length();
	
	public static final String DEFAULT_MULTI_MESSAGE = "Process completed with multiple warnings or errors.";
	public static final boolean DEFAULT_ALLOW_MERGE = false;

    private final static String Digits     = "(\\p{Digit}+)";
    private final static String HexDigits  = "(\\p{XDigit}+)";
    // an exponent is 'e' or 'E' followed by an optionally 
    // signed decimal integer.
    private final static String Exp        = "[eE][+-]?"+Digits;
    private final static String fpRegex    =
        ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
         "[+-]?(" + // Optional sign character
         "NaN|" +           // "NaN" string
         "Infinity|" +      // "Infinity" string

         // A decimal floating-point string representing a finite positive
         // number without a leading sign has at most five basic pieces:
         // Digits . Digits ExponentPart FloatTypeSuffix
         // 
         // Since this method allows integer-only strings as input
         // in addition to strings of floating-point literals, the
         // two sub-patterns below are simplifications of the grammar
         // productions from the Java Language Specification, 2nd 
         // edition, section 3.10.2.

         // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
         "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

         // . Digits ExponentPart_opt FloatTypeSuffix_opt
         "(\\.("+Digits+")("+Exp+")?)|"+

   // Hexadecimal strings
   "((" +
    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
    "(0[xX]" + HexDigits + "(\\.)?)|" +

    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
    "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

    ")[pP][+-]?" + Digits + "))" +
         "[fFdD]?))" +
         "[\\x00-\\x20]*");// Optional trailing "whitespace"

	
	public static boolean stringsEqual(String a, String b)
	{ return stringsEqual(a, b, false); }
	
	public static boolean stringsEqual(String a, String b, boolean ignoreCase)
	{
		// if they're both null, it's a match
		if(a==null && b==null)
		{
			return true;
		}
		
		// they're not both null, so if one's null, they're not a match
		if(a==null || b==null)
		{
			return false;
		}
		
		// at this point, neither must be null
		return ignoreCase ? a.equalsIgnoreCase(b) : a.equals(b);
	}
	
	public static IStatus mergeStatus(IStatus status1, IStatus status2, String pluginID)
	{
		return mergeStatus(status1, status2, pluginID, DEFAULT_MULTI_MESSAGE);
	}
	
	public static IStatus mergeStatus(IStatus status1, IStatus status2, String pluginID, boolean allowMerge)
	{
		return mergeStatus(status1, status2, pluginID, DEFAULT_MULTI_MESSAGE, allowMerge);
	}
	
	public static IStatus mergeStatus(IStatus status1, IStatus status2, String pluginID, String multiMessage)
	{
		return mergeStatus(status1, status2, pluginID, multiMessage, DEFAULT_ALLOW_MERGE);
	}
	
	public static IStatus mergeStatus(IStatus status1, IStatus status2, String pluginID, String multiMessage, boolean allowMerge)
	{
		if(status1 == null)
		{
			return status2;
		}
		
		if(status2 == null)
		{
			return status1;
		}
		
		if(status1.isOK())
		{
			return status2;
		}
		
		if(status2.isOK())
		{
			return status1;
		}
		
		// ok, neither are ok, so we need to do multi status.  make sure and preserve order
		if(allowMerge && (status1 instanceof MultiStatus))
		{
			((MultiStatus) status1).merge(status2);
			return status1;
		} else {
			// status 2 may be multi, and if it is, we could just merge status1 into it, but that
			// would mess up ordering
			return new MultiStatus(pluginID, IStatus.OK, new IStatus[] {status1, status2}, multiMessage, null);
		}
	}
	
	public static <K, V> List<?> reversLookup(Map<K, V> map, Object value)
	{
		List<K> keys = new ArrayList<K>();
		
		if(!map.containsValue(value))
		{
			return keys;
		}
		
		Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry<K, V> entry = iter.next();
			
			if(entry.getValue().equals(value))
			{
				keys.add(entry.getKey());
			}
		}
		
		return keys;
	}
	
	/**
	 * Returns whether the given String is composed solely of digits
	 */
	// TODO what about floats, decimals, scientific notation?  Hex?
	public static boolean isNumber(String string)
	{
	    if (string == null || string.isEmpty()) {
	        return false;
	    }
	    if (string.length() >= NUMBER_MAX_LENGTH) {
	        try {
	            Long.parseLong(string);
	        } catch (Exception e) {
	            return false;
	        }
	    } else {
	        int i = 0;
	        if (string.charAt(0) == '-') {
	            if (string.length() > 1) {
	                i++;
	            } else {
	                return false;
	            }
	        }
	        for (; i < string.length(); i++) {
	            if (!Character.isDigit(string.charAt(i))) {
	                return false;
	            }
	        }
	    }
	    return true;
	}
	
	public static boolean isDouble(String string)
	{
		 return Pattern.matches(fpRegex, string);
	}
	
	public static String ensureExtension(String fileName, String extension)
	{
		if(StringUtils.isBlank(fileName) || StringUtils.isBlank(extension))
		{
			return fileName;
		}
		
		if(fileName.toLowerCase().endsWith('.'+extension.toLowerCase()))
		{
			return fileName;
		}
		
		if(fileName.endsWith("."))
		{
			return fileName + extension;
		} else {
			return fileName + '.' + extension;
		}
	}
	
	/**
	 * Returns the given string with the extension removed if there is one.
	 */
	public static String removeExtension(String string)
	{
		int firstCharIndex = StringUtils.indexOfAnyBut(string, ".");
		int lastDot;
		if(firstCharIndex == -1)
			lastDot = -1;
		else{
			lastDot = StringUtils.lastIndexOf(string.substring(firstCharIndex, string.length()), ".");
			if(lastDot != -1)
				lastDot += firstCharIndex;
		}
		return takeStringToIndex(string, lastDot);
	}
	
	/**
	 *  Returns the input string with all extensions removed
	 *  @param string Filename without path 
	 */
	public static String removeAllExtensions(String string)
	{
		int firstCharIndex = StringUtils.indexOfAnyBut(string, ".");
		int firstDot;
		if(firstCharIndex == -1)
			firstDot = -1;
		else
			firstDot = StringUtils.indexOf(string, ".", firstCharIndex);
		return takeStringToIndex(string, firstDot);
	}
	
	private static String takeStringToIndex(String string, int index)
	{
		if(index < 0)
		{
			return string;
		} else if(index == 0) {
			return "";
		} else {
			return string.substring(0, index);
		}
	}
	
	/**
	 * Returns the text following the last '.' character of the given string
	 */
	public static String getExtension(String string)
	{
		int lastDot = string.lastIndexOf(".");
		if(lastDot < 0) // if a dot wasn't found, return the original string
			return string;
		else if(lastDot+1 > string.length())
			return "";
		else
			return string.substring(lastDot+1);
	}
	
	// we're temporarily adding the tilde '~' character.  See DTA-2181.  We probably only need to test for it
	// at the beginning of the name, but until we fully understand the nature of the bug,
	// we're going to ban tildes all together.
//	public static final char[] INVALID_FILE_CHARACTERS = {'\\', '/', '\"', '\'', '?', '*', '<', '>', ':', '|', '\t', '\n', '\r', '@', '$', '~'};

  public static final char[] INVALID_FILE_CHARACTERS = {'\\', '/', '\"', '\'', '?', '*', '<', '>', ':', '|', '\t', '\n', '\r', '@', '$'};

	public static String isValidAPCName(String name)
	{
		if(name==null || name.trim().equals(""))
			return "The name cannot be null or blank";
		
		String illegalChar = containsIllegalFileCharacter(name);
		if(illegalChar != null)
			return "The name cannot contain the character '"+illegalChar+"'";
		
		// names cannot end with a period (i think this is only a bug on windows, but keep
		// in mind that we need names to be valid on all platforms because what someone
		// commits on one platform someone else could download on another platform
		if(name.charAt(name.length()-1) == '.')
			return "The name cannot end with a period '.'";
		
		return null;
	}
	
	/**
	 * Method tests the given string to see if it is a valid file/folder name.
	 *   
	 * @param value - the string to test.
	 * @return null if the given string is valid, or the invalid charcter if one is found.
	 */
	public static String containsIllegalFileCharacter(String value)
	{
		if(value==null || value.length()<1)
			return null;
		
		for(char c : value.toCharArray())
			for(char invalid : INVALID_FILE_CHARACTERS)
				if(c == invalid)
					return ""+c;
		
		return null;
	}
	
	public static String trimTrailingWhitespace(String str)
	{
		int length = str.length();
		int start = 0;

		while(length>start && str.charAt(length-1)<=' ')
		    length--;
		
		return (start>0 || length<str.length()) ? str.substring(start, length) : str;
	}
	
	public static String getUniqueName(Collection<String> existingNames, String baseName)
	{
		int newNum;
		
		// if the name ends with numbers, start incrementing from there
		if(Character.isDigit(baseName.charAt(baseName.length()-1)))
		{
			// find all the last digits
			char[] chars = baseName.toCharArray();
			
			StringBuilder sb = new StringBuilder();
			for(int i=chars.length-1; i>=0 && Character.isDigit(chars[i]); i--)
			{
				sb.append(chars[i]);
			}
			
			// update the basename to not include the number suffix
			baseName = baseName.substring(0, baseName.length()-sb.length());
			
			// reverse the numbers we built up backwards
			String newNumS = StringUtils.reverse(sb.toString());
			
			newNum = Integer.parseInt(newNumS);
			
			// add one to it, since we know this one already exists
			newNum++;
		} else {
			// otherwise, start with 2
			newNum = 2;
		}
		
		String newName;
		
		do {
			// Do we want to maybe make the new name "basename-##", i.e. insert a dash between the base name and unique number?
			newName = baseName + newNum++;
		} while(existingNames.contains(newName));
		
		return newName;
	}

	public static String replaceAllLineDelimiters(String originalString, String delimiterReplacement)
	{
		if(originalString == null)
			return null;
		
//		System.err.println("WARNING! original string already contains the delimiter replacement.");
		
		String newString = originalString.replace("\r\n", delimiterReplacement);
		newString = newString.replace("\r", delimiterReplacement);
		newString = newString.replace("\n", delimiterReplacement);
		return newString;
	}
	
	/**
	 * Method parses the given String value as a boolean.  In the event the
	 * value doesn't match (ignoring case) the strings of "true" or "false" exactly,
	 * the given boolean default value will be returned.  This provides the caller
	 * the opportunity to effectively invert the default behavior of
	 * {@link Boolean#parseBoolean(String)} by passing a default value of true.
	 * That is, the caller can toggle via the given default value if the given
	 * string should be matched against "false" or "true".
	 * 
     * Example: {@code #parseBoolean("True", true} returns <tt>true</tt>.<br>
     * Example: {@code #parseBoolean("True", false} returns <tt>true</tt>.<br>
     * Example: {@code #parseBoolean("yes", true} returns <tt>true</tt>.
     * Example: {@code #parseBoolean("yes", false} returns <tt>false</tt>.
     * Example: {@code #parseBoolean("fred", true} returns <tt>true</tt>.
     * Example: {@code #parseBoolean("fred", false} returns <tt>false</tt>.
     * Example: {@code #parseBoolean(null, true} returns <tt>true</tt>.
     * Example: {@code #parseBoolean(null, false} returns <tt>false</tt>.
	 *   
	 * @param value - the string value to be parsed - may be null
	 * @param defaultValue - the default value to return in the event the
	 * 						 given value doesn't match exactly (ignoring
	 * 						 case) one of Strings "true" or "false"
	 * @return the parsed value of the given String value as a boolean
	 */
	public static boolean parseBoolean(String value, boolean defaultValue)
	{
		if(defaultValue)
		{
			return !"false".equalsIgnoreCase(value);
		} else {
			return "true".equalsIgnoreCase(value);
		}
	}
	
	public static int parseInt(String sVal, int defaultValue)
	{
		if(sVal == null)
			return defaultValue;
		try {
			return Integer.parseInt(sVal);
		} catch (NumberFormatException nfe) {
		}
		return defaultValue;
	}
	
	/**
	 * Checks for a JVM Define of "testclient" (needs only to be set,
	 * so -Dtestclient or -Dtestclient=ANYTHING is fine)
	 */
	public static boolean isTestClientRunning()
	{
	  boolean bRet = false;
	  
	  String val = System.getProperty("testclient");
	  
	  // if val != null, then it is set in some fashion
	  bRet = (null != val);
	  
	  return bRet;
	}
	
	  /**
	   * @deprecated callers should get this themselves. It could in theory change
	   *             in a given session, and shouldn't be initialized, saved, and
	   *             referenced in this static way.
	   */
	  @Deprecated
	  public static String LOCAL_HOSTNAME = initLocalHostname();

	  /**
	   * Returns the name of the local machine, or "localhost" if an error occurs;
	   * typically only used during the original instantiation of the class.
	   */
	  private static String initLocalHostname()
	  {
	  	String localHostname = "localhost";
	    try {
	  	  URI uri = HostnameProvider.getLocalHostname();
	  	  localHostname = uri.getHost();
	    }
	    catch (Throwable t) {
	      CommonPlugin.getDefault().logError("Error obtaining local hostname: "
	          + t.getMessage(),
	          t);
	    }

	    return localHostname;
	  }
	  
	public static boolean isLocalHost(String host)
	{
		if(StringUtils.isBlank(host))
		{
			return false;
		}
		
		if("localhost".equalsIgnoreCase(host))
		{
			return true;
		}
		
		if("127.0.0.1".equals(host))
		{
			return true;
		}
		
		if(StringUtils.equals(host, LOCAL_HOSTNAME))
		{
			return true;
		}
		
		
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			if(host.equalsIgnoreCase(localHost.getCanonicalHostName()) ||
					host.equalsIgnoreCase(localHost.getHostName()) ||
					host.equalsIgnoreCase(localHost.getHostAddress()))
			{
				return true;
			}
		} catch (Throwable t) {
			CommonPlugin.getDefault().logError("Error obtaining local host: "+t.getMessage(), t);
		}
		
		return false;
	}
	
	public static int compare(String s1, String s2)
	{
		if(s1!=null && s2!=null)
		{
			return s1.compareTo(s2);
		}
		
		if(s1==null && s2==null)
		{
			return 0;
		}
		
		return s1==null ? -1 : 1;
	}
	
	public static boolean evaluateCountExpression(String sizeExpression, Collection<?> countable) throws CoreException
	{
		return evaluateCountExpression(sizeExpression, countable.size());
	}
	
	public static boolean evaluateCountExpression(String sizeExpression, int count) throws CoreException
	{
		return evaluateCountExpression(sizeExpression, new StaticCountable(count));
	}
	
	@SuppressWarnings("restriction")
	public static boolean evaluateCountExpression(String sizeExpression, ICountable countable) throws CoreException
	{
		CountExpression countExp = new CountExpression(sizeExpression);
		EvaluationResult result = countExp.evaluate(new MyEvaulationContext(countable));
		return EvaluationResult.TRUE == result;
	}
	
	public static Map<String, String> asMap(Properties props)
	{
		Map<String, String> propsMap = new HashMap<String, String>();
		if(props == null)
		{
			return propsMap;
		}
		for(Entry<Object, Object> entry : props.entrySet())
		{
			Object key = entry.getKey();
			Object value = entry.getValue();
			if(key instanceof String && value instanceof String)
			{
				propsMap.put((String) key, (String) value);
			}
		}
		return propsMap;
	}
	
	public static String titleCase(String s)
	{
		s = s.replace('_', ' ');
		if(StringUtils.isBlank(s))
		{
			return s;
		}
		
		final StringBuilder result = new StringBuilder(s.length());
		String[] words = s.split("\\s");
		for(int i=0,l=words.length;i<l;++i) {
		  if(i>0) result.append(" ");      
		  result.append(Character.toUpperCase(words[i].charAt(0)))
		        .append(words[i].substring(1));

		}
		return result.toString();
	}
	
	private static class MyEvaulationContext implements IEvaluationContext
	{
		private final ICountable defaultVariable_;
		
		private MyEvaulationContext(ICountable defaultVariable)
		{
			this.defaultVariable_ = defaultVariable;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.expressions.IEvaluationContext#getDefaultVariable()
		 */
		@Override
		public Object getDefaultVariable()
		{
			return this.defaultVariable_;
		}

		@Override
    public IEvaluationContext getParent() { return null; }
		@Override
    public IEvaluationContext getRoot() { return null; }
		@Override
    public void setAllowPluginActivation(boolean value) {}
		@Override
    public boolean getAllowPluginActivation() { return false; }
		@Override
    public void addVariable(String name, Object value) {}
		@Override
    public Object removeVariable(String name) { return null; }
		@Override
    public Object getVariable(String name) { return null; }
		@Override
    public Object resolveVariable(String name, Object[] args) throws CoreException { return null; }
	}
}
