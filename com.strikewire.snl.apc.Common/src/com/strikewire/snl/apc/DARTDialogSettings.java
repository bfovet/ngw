/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogSettings;



public class DARTDialogSettings implements IDARTDialogSettings
{
  
  /**
   * _mSectionsByName - Keeps an association of the Settings by the
   * keys. These sections are
   */
  private Map<String,IDARTDialogSettings> _mSectionsByName =
    new HashMap<String,IDARTDialogSettings>();
  
  
  /**
   * _mValuesByKey - Holds the values for this  
   */
  private Map<String,Object> _mValuesByKey =  new HashMap<String,Object>();
  
  private final String _sectionName;
  
  
  
  /**
   * Creates a new DialogSettings with the specified root name.
   * @param sectionName
   */
  public DARTDialogSettings(String sectionName)
  {
    _sectionName = sectionName;
  }

  
  /**
   * Get the Map containing the key/value pairs for this section.
   * @return
   * @author kholson
   * <p>
   * Initial Javadoc date: Sep 24, 2010
   * <p>
   * Permission Checks:
   * <p>
   * History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   *<br />
   */
  protected Map<String,Object> getSettingsMap()
  {
    return _mValuesByKey;
  }

  
  /**
   * Get the Map with all of the child sections for this section.
   * @return
   * @author kholson
   * <p>
   * Initial Javadoc date: Sep 24, 2010
   * <p>
   * Permission Checks:
   * <p>
   * History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   *<br />
   */
  protected Map<String,IDARTDialogSettings> getSectionsMap()
  {
    return _mSectionsByName;
  }


  
  @Override
  public IDARTDialogSettings addNewSection(String name)
  {
    IDARTDialogSettings section;
    if (! getSectionsMap().containsKey(name)) {
      section = new DARTDialogSettings(name);
      getSectionsMap().put(name, section);
    }
    else {
      section = getSectionsMap().get(name);
    }
    
    return section;
  }




  /**
   * Adds the specified section; if there is an existing section of
   * the same name, this section will replace it.
   * @param section
   * @author kholson
   * <p>
   * Initial Javadoc date: Sep 24, 2010
   * <p>
   * Permission Checks:
   * <p>
   * History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   *<br />
   */
  public void addSection(IDARTDialogSettings section)
  {
    if (section != null) {
     getSectionsMap().put(section.getName(), section);
    }
  }
  
  
  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.IDialogSettings#addSection(org.eclipse.jface.dialogs.IDialogSettings)
   */
  @Override
  public void addSection(IDialogSettings section)
    throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException("Must add subclass of " +
        this.getClass().getSimpleName());
  }



  @Override
  public String get(String key)
  {
    Object val = getSettingsMap().get(key); 
    return (val != null ? val.toString() : null);
  }



  @Override
  public String[] getArray(String key)
  {
    Object val = getSettingsMap().get(key);
    
    if (val == null) {
      return null;
    }
    
    if (val instanceof String[]) {
      return (String[])val;
    }
    else {
      String[] as = new String[1];
      as[0] = (String)val;
      
      return as;
    }
    

  }



  @Override
  public boolean getBoolean(String key)
  {
    boolean ret = false;
    
    Object val = getSettingsMap().get(key);
    
    if (val instanceof String) {
      ret = Boolean.valueOf((String)val);
    }
    
    return ret;
  }



  @Override
  public double getDouble(String key) throws NumberFormatException
  {
    double ret = 0;
    Object val = getSettingsMap().get(key);

    if (val instanceof String) {
      ret = Double.valueOf((String)val);
    }
    else {
      throw new NumberFormatException("Cannot convert non-String data");
    }
    
    return ret;
  }



  @Override
  public float getFloat(String key) throws NumberFormatException
  {
    float ret = 0;
    Object val = getSettingsMap().get(key);

    if (val instanceof String) {
      ret = Float.valueOf((String)val);
    }
    else {
      throw new NumberFormatException("Cannot convert non-String data");
    }
    
    return ret;
  }



  @Override
  public int getInt(String key) throws NumberFormatException
  {
    int ret = 0;
    Object val = getSettingsMap().get(key);

    if (val instanceof String) {
      ret = Integer.valueOf((String)val);
    }
    else {
      throw new NumberFormatException("Cannot convert non-String data");
    }
    
    return ret;
  }


  @Override
  public long getLong(String key) throws NumberFormatException
  {
    long ret = 0;
    Object val = getSettingsMap().get(key);

    if (val instanceof String) {
      ret = Long.valueOf((String)val);
    }
    else {
      throw new NumberFormatException("Cannot convert non-String data");
    }
    
    return ret;
  }


  @Override
  public String getName()
  {
    return _sectionName;
  }


  @Override
  public IDARTDialogSettings getSection(String sectionName)
  {
    return getSectionsMap().get(sectionName);
  }



  @Override
  public IDARTDialogSettings[] getSections()
  {
    Set<String> keySet = getSectionsMap().keySet();
    
    List<IDARTDialogSettings> lstTmp = new ArrayList<IDARTDialogSettings>();
    
    for (String key : keySet) {
      lstTmp.add(getSectionsMap().get(key));
    }

    return lstTmp.toArray(new IDARTDialogSettings[lstTmp.size()]);
  }


  @Override
  public void load(Reader reader) throws IOException
  {
    throw new IOException("Not implemented");

  }


  @Override
  public void load(String fileName) throws IOException
  {
    throw new IOException("Not implemented");

  }


  @Override
  public void put(String key, String[] value)
  {
    getSettingsMap().put(key, value);

  }


  @Override
  public void put(String key, double value)
  {
    getSettingsMap().put(key, Double.toString(value));

  }


  @Override
  public void put(String key, float value)
  {
    getSettingsMap().put(key, Float.toString(value));

  }

  @Override
  public void put(String key, int value)
  {
    getSettingsMap().put(key, Integer.toString(value));

  }


  @Override
  public void put(String key, long value)
  {
    getSettingsMap().put(key, Long.toString(value));

  }


  @Override
  public void put(String key, String value)
  {
    getSettingsMap().put(key, value);

  }


  @Override
  public void put(String key, boolean value)
  {
    getSettingsMap().put(key, Boolean.toString(value));

  }


  @Override
  public void save(Writer writer) throws IOException
  {
    throw new IOException("Not Implemented");

  }


  @Override
  public void save(String fileName) throws IOException
  {
    throw new IOException("Not Implemented");

  }


  @Override
  public Set<String> getKeys()
  {
    return Collections.unmodifiableSet(getSettingsMap().keySet());
  }


  @Override
  public Object removeSetting(String key) 
  {
    if (key == null || key.trim().length() == 0) {
      throw new IllegalArgumentException("Null/empty key specified");
    }
    
    if (! getSettingsMap().containsKey(key)) {
      throw new IllegalArgumentException("Key not in section: " + key + " " +
          getName());
    }
    
    return getSettingsMap().get(key);
      
  }

}
