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
 * Created by mjgibso on Oct 18, 2013 at 5:23:56 AM
 */
package com.strikewire.snl.apc.Common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

/**
 * @author mjgibso
 * 
 * This body of this class was ripped off from org.eclipse.ui.internal.ProductProperties.
 * Not only is ProductProperties discouraged access, but this mappings resolution stuff is
 * private and inaccessible.  
 *
 */
public class MappingsUtil
{
	private MappingsUtil()
	{
	}
	
    private static final String ABOUT_MAPPINGS = "$nl$/about.mappings"; //$NON-NLS-1$

    private static Map<Bundle, String[]> mappingsMap = new HashMap<Bundle, String[]>();
    
    // TODO make thread safe so we don't load the same bundle more than once by multiple
    // threads at the same time.
    
    private static String[] loadMappings(Bundle definingBundle)
    {
		URL location = FileLocator.find(definingBundle, new Path(ABOUT_MAPPINGS), null);
        PropertyResourceBundle bundle = null;
        InputStream is;
        if (location != null) {
            is = null;
            try {
                is = location.openStream();
                bundle = new PropertyResourceBundle(is);
            } catch (IOException e) {
                bundle = null;
            } finally {
                try {
                    if (is != null) {
						is.close();
					}
                } catch (IOException e) {
                    // do nothing if we fail to close
                }
            }
        }

        ArrayList<String> mappingsList = new ArrayList<String>();
        if (bundle != null)
        {
            boolean found = true;
            int i = 0;
            while (found) {
                try {
                    mappingsList.add(bundle.getString(Integer.toString(i)));
                } catch (MissingResourceException e) {
                    found = false;
                }
                i++;
            }
        }
        String[] mappings = (String[]) mappingsList.toArray(new String[mappingsList.size()]);
        mappingsMap.put(definingBundle, mappings);
        return mappings;
    }
    
    public static String[] getMappings(Bundle definingBundle)
    {
    	String[] mappings = mappingsMap.get(definingBundle);
    	if (mappings == null) {
    		mappings = loadMappings(definingBundle);
    	}
    	if (mappings == null) {
    		mappings = new String[0];
    	}
    	return mappings;
    }
	
}
