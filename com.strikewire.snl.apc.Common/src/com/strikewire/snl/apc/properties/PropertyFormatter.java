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
 *  Copyright (C) 2012
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

package com.strikewire.snl.apc.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Stack;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * Handles formatting Strings with may include embedded
 * ${XXX} properties
 * @author kholson
 *
 */
public class PropertyFormatter
{
  private final String EMPTY_STRING = "";
  private final static PropertyFormatter _this = new PropertyFormatter();
  
  private final Pattern _patHasSubs = Pattern.compile(".*(\\$\\{[^\\}]+\\}).*");
  
  private PropertyFormatter()
  {
  }
  
  
  public static PropertyFormatter getDefault()
  {
    return _this;
  }
  
  public static PropertyFormatter getInstance()
  {
    return getDefault();
  }
  
  
  /**
   * @param input An input String which may included embedded properties
   * in the form of ${property} where property is a name which may be
   * found in the props Map, and will be changed with the value
   * for that property from the Map. 
   * @param props A Map containing a key of a property which may be
   * found in the specified input String, and a value which will be
   * substituted for the ${property} substring
   * @return The resolved String, unless the inbound String is null in
   * which case null is returned. If the inbound String is an empty
   * String, an empty String is returned
   * @author kholson
   * <p>
   * Initial Javadoc date: Aug 16, 2012
   * <p>
   * Permission Checks:
   * <p>
   * History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   *<br />
   */
  public String resolveEmbeddedProperties(final String input,
                                          Map<String,String> props)
    throws CoreException
  {
    if (StringUtils.isBlank(input)) {
      return input;
    }
    
    //
    // see if we have any substitution possibilities at all; if not
    // we will just return the String
    //
    Matcher matcher = _patHasSubs.matcher(input);
    
    if (! matcher.matches() && !matcher.find()) {
      return input;
    }
    
    
    //
    // we need an absolutely random key; so here it is:
    //  random number + cryptographically secure UUID generator
    //
    Random rnd = new Random();
    UUID uuid = UUID.randomUUID();
    String rndKey = uuid.toString() + rnd.nextInt();
    
    // make a copy of the inbound properties map
    Map<String,String> tempProps = new HashMap<String,String>(props);
    
    tempProps.put(rndKey, input);
    
    
    resolveProperties(tempProps);
    
    return tempProps.get(rndKey);
  } //resolveEmbeddedProperites
  
  
  /**
   * Method resolves any references in any of the values of the given map to other keys within the map.  The method
   * returns whether or not any changes were made to the map.  An exception is thrown if a cycle in the variable
   * references is detected.  Variable references follow the ANT language syntax for referencing variables: they
   * should be wrapped by ${variableName}, where 'variableName' is the actual name of the variable.
   * 
   * @param props - the map containing all variables and their values
   * @return true if changes were made to the map, false otherwise
   * @throws CoreException - for cyclic references
   */
  public boolean resolveProperties(Map<String, String> props) throws CoreException
  {
    boolean anyChange = false;
    
    // go through all the entries, and for each go through all the keys and do a search and replace on the entry's
    // value with the value associated with the given key.  If we successfully do a replace/substitution, try to
    // drill down resolving that particular entry in a recursive manner such that we can wach for cycles in the
    // references which would lead to a recursive loop.
    
    Stack<Entry<String, String>> entryStack = new Stack<Map.Entry<String,String>>();
    for(Entry<String, String> entry : props.entrySet())
    {
      entryStack.push(entry);
      boolean changed = resolveProperty(entryStack, props);
      if(changed)
      {
        anyChange = true;
      }
    }
    
    return anyChange;
  }  
  
  
  private boolean resolveProperty(Stack<Entry<String, String>> entryStack, Map<String, String> props) throws CoreException
  {
    // TODO also check the eclipse variable manager
    
    String origValue = entryStack.peek().getValue();
    String value = origValue;
    
    for(Entry<String, String> entry : props.entrySet())
    {
      String newValue = resolve(value, entry);
      
      if(StringUtils.equals(value, newValue))
      {
        // if no change, next...
        continue;
      }
      
      
      
      // it must have changed
      
      // go recursive, drilling down on this guy to fully resolve it, check for cycle first
      addToStackCheckingForCycle(entryStack, entry);
      boolean changed = resolveProperty(entryStack, props);
      if(changed)
      {
        // if it changed, we had a recursive reference, so need to re-resolve this guy
        newValue = resolve(value, entry);
      }
      // save the change
      value = newValue;
    }
    
    // now resolve any eclipse variables possibly referenced
    IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
    value = manager.performStringSubstitution(value, false);
    
    // pop this entry off as it has been resolved
    Entry<String, String> entry = entryStack.pop();
    
    // if there were any changes
    boolean anyChange = !StringUtils.equals(origValue, value);
    if(anyChange)
    {
      entry.setValue(value);
    }
    
    return anyChange;
  }  
  
  
  private String resolve(String str, Entry<String, String> entry)
  {
    String key = entry.getKey();
    String value = entry.getValue();
    return str.replace("${"+key+"}", value);
  }
  
  
  private void addToStackCheckingForCycle(Stack<Entry<String, String>> stack, Entry<String, String> entry) 
      throws CoreException
  {
    // check for a cyclic references
    
    int cycleStart = stack.indexOf(entry);
    
    // if we don't have this guy yet
    if(cycleStart < 0)
    {
      // there's no cycle, so just push it and get out
      stack.push(entry);
      return;
    }
    
    // must have a cycle
    StringBuilder sb = new StringBuilder();
    sb.append("There is a circular variable dependency: ");
    for(int i=cycleStart; i<stack.size(); i++)
    {
      sb.append(stack.get(i).getKey());
      sb.append(" -> ");
    }
    sb.append(entry.getKey());
    
    throw new CoreException(CommonPlugin.getDefault().newErrorStatus(sb.toString(),
        new Exception()));
  }  
} //class PropertyFormatter
