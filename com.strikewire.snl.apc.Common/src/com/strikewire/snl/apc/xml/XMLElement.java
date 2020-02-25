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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marcus J. Gibson
 *
 */
public class XMLElement
{
	private String name_;
	private Map<String, String> attributes_;
	private List<XMLElement> children_;
	private String innerText_;
	
	public XMLElement(String name)
	{ this.name_ = name; }
	
	public XMLElement(String name, List<XMLElement> children)
	{
		this.name_ = name;
		setChildren(children);
	}
	
	public XMLElement(String name, Map<String, String> attributes)
	{
		this.name_ = name;
		setAttributes(attributes);
	}
	
	public XMLElement(String name, List<XMLElement> children, Map<String, String> attributes)
	{
		this.name_ = name;
		setChildren(children);
		setAttributes(attributes);
	}
	
	public void setAttribute(String name, String value)
	{
		if(name==null || value==null)
			throw new IllegalArgumentException("Attribute names and values must not be null.");
		
		if(attributes_ == null)
			attributes_ = new HashMap<String, String>();
		
		attributes_.put(name, value);
	}
	
	public void setAttributes(Map<String, String> attributes)
	{
		if(attributes == null)
			throw new IllegalArgumentException("Attributes map must not be null.");
		
		if(attributes.size() == 0)
		{
			clearAttributes();
			return;
		}
		
		if(attributes_ == null)
			attributes_ = new HashMap<String, String>();
		
		attributes_.putAll(attributes);
	}
	
	public void addChild(XMLElement child)
	{
		if(child == null)
			throw new IllegalArgumentException("Child elements must not be null.");
		
		if(children_ == null)
			children_ = new ArrayList<XMLElement>();
		
		children_.add(child);
	}
	
	public void setChildren(List<XMLElement> children)
	{
		if(children == null)
			throw new IllegalArgumentException("Child elements list must not be null.");
		
		if(children.size() == 0)
		{
			clearChildren();
			return;
		}
		
		if(children_ == null)
			children_ = new ArrayList<XMLElement>();
		
		children_.addAll(children_);
	}
	
	public void clearChildren()
	{ children_ = null; }
	
	public void clearAttributes()
	{ attributes_ = null; }
	
	public Map<String, String> getAttributes()
	{
		if(attributes_ == null)
			return new HashMap<String, String>();
		
		return new HashMap<String, String>(attributes_);
	}
	
	public List<XMLElement> getChildren()
	{
		if(children_ == null)
			return new ArrayList<XMLElement>();
		
		return new ArrayList<XMLElement>(children_);
	}
	
	public void setInnerText(String text)
	{ innerText_ = text; }
	
	public String getInnerText()
	{ return innerText_; }
	
	public String getName()
	{ return name_; }
}
