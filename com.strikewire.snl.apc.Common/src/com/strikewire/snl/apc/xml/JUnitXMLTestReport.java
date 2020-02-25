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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author Marcus J. Gibson
 *
 */
public class JUnitXMLTestReport
{
	private XMLElement suite_;
	private XMLElement properties_;
	private String name_;
	private long totalTime_ = 0;
	public static final String PATTERN = "###0.000";
	private DecimalFormat format_ = new DecimalFormat(PATTERN);
	
	public JUnitXMLTestReport(String packageName, String suiteName)
	{
		this.name_ = packageName+"."+suiteName;
		
		suite_ = new XMLElement("testsuite");
		suite_.setAttribute("name", this.name_);
		suite_.setAttribute("tests", "0");
		suite_.setAttribute("failures", "0");
		suite_.setAttribute("errors", "0");
		addToTotalTime(0);
		updateTests();
		
		properties_ = new XMLElement("properties");
		suite_.addChild(properties_);
	}
	
	public void setProperties(Map<String, String> properties)
	{
		properties_.clearChildren();
		Iterator<Map.Entry<String, String>> iter = properties.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry<String, String> entry = iter.next();
			addProperty(entry.getKey(), entry.getValue());
		}
	}
	
	public void addProperty(String name, String value)
	{
		XMLElement property = new XMLElement("property");
		property.setAttribute("name", name);
		property.setAttribute("value", value);
		properties_.addChild(property);
	}
	
	public TestElement addTest(String testName, long time)
	{
		TestElement testObj = new TestElement(testName, time);
		suite_.addChild(testObj.getElement());
		addToTotalTime(time);
		updateTests();
		
		return testObj;
	}
	
	public void addFailedTest(String testName, long time, Throwable cause)
	{
		TestElement test = addTest(testName, time);
		test.setFailure(cause);
	}
	
	public void addErrorTest(String testName, long time, Throwable cause)
	{
		TestElement test = addTest(testName, time);
		test.setError(cause);
	}
	
	public void addCustomErrorTest(String testName, long time, String message, String type, String innerText)
	{
		XMLElement test = addTest(testName, time).getElement();
		XMLElement error = new XMLElement("error");
		test.addChild(error);
		error.setAttribute("message", message);
		error.setAttribute("type", type);
		error.setInnerText(innerText);
	}
	
	private String getTimeString(long time)
	{
		double dtime = time;
		return format_.format(dtime/1000);
	}
	
	private void addToTotalTime(long time)
	{
		this.totalTime_ += time;
		suite_.setAttribute("time", getTimeString(this.totalTime_));
	}
	
	private void updateTests()
	{
		List<XMLElement> children = suite_.getChildren();
		int numTests = children.size();
		suite_.setAttribute("tests", String.valueOf(numTests));
		
		int numErrors = 0;
		int numFailures = 0;
		for(XMLElement child : children)
		{
			List<XMLElement> grandChildren = child.getChildren();
			if(grandChildren.size() > 0)
			{
				XMLElement grandChild = grandChildren.get(0);
				if(grandChild.getName().equals("failure"))
					numFailures++;
				else if(grandChild.getName().equals("error"))
					numErrors++;
			}
		}
		
		suite_.setAttribute("failures", String.valueOf(numFailures));
		suite_.setAttribute("errors", String.valueOf(numErrors));
	}
	
	public void WriteToFile(File file)
	{ XMLUtils.print(file, suite_); }
	
	public class TestElement
	{
		private XMLElement test_;
		
		private TestElement(String name, long time)
		{
			test_ = new XMLElement("testcase");
			test_.setAttribute("classname", name_);
			test_.setAttribute("name", name);
			test_.setAttribute("time", getTimeString(time));
		}
		
		private XMLElement getElement()
		{ return test_; }
		
		public void setError(Throwable cause)
		{
			test_.clearChildren();
			XMLElement error = new XMLElement("error");
			test_.addChild(error);
			
			setupFromCause(error, cause);
			updateTests();
		}
		
		public void setFailure(Throwable cause)
		{
			test_.clearChildren();
			XMLElement failure = new XMLElement("failure");
			test_.addChild(failure);
			
			setupFromCause(failure, cause);
			updateTests();
		}
		
		public void setCustomError(String message, String type, String innerText)
		{
			
			test_.clearChildren();
			XMLElement error = new XMLElement("error");
			test_.addChild(error);
			
			error.setAttribute("message", message);
			error.setAttribute("type", type);
			error.setInnerText(innerText);
			updateTests();
		}
		
		private void setupFromCause(XMLElement element, Throwable cause)
		{
			element.setAttribute("message", cause.getMessage());
			element.setAttribute("type", cause.getClass().getName());
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(os, true);
			cause.printStackTrace(ps);
			ps.flush();
			ps.close();
			element.setInnerText(os.toString());
		}
	}
}
