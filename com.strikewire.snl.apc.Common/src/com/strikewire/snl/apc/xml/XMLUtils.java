/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.xml;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Marcus J. Gibson
 *
 */
public class XMLUtils
{
	public static final String FIRST_ELEMENT_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
	
	public static final String TAB = "  ";
	
	public static void print(File outFile, XMLElement rootElement)
	{
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(outFile);
			pw.println(FIRST_ELEMENT_STRING);
			printElement(pw, rootElement, "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pw != null)
			{
				pw.flush();
				pw.close();
			}
		}
	}
	
	public static void printElement(PrintWriter pw, XMLElement element, String tab)
	{
		pw.print(tab+"<"+element.getName());
		Iterator<Map.Entry<String, String>> iter = element.getAttributes().entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry<String, String> entry = iter.next();
			pw.print(" "+entry.getKey()+"=\""+entry.getValue()+"\"");
		}
		
		List<XMLElement> children = element.getChildren();
		String innerText = element.getInnerText();
		if(children.size()>0 || innerText!=null)
		{
			pw.println(" >");
			if(innerText != null)
				pw.println(tab+TAB+TAB+element.getInnerText());
			for(XMLElement child : children)
				printElement(pw, child, tab+TAB);
			pw.println(tab+"</"+element.getName()+">");
		} else
			pw.println(" />");
		
		pw.flush();
	}
}
