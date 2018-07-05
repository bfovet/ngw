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
 * Created by mjgibso on Aug 27, 2012 at 6:04:26 AM
 */
package gov.sandia.dart.aprepro;

import gov.sandia.dart.aprepro.actions.ParameterHandler;
import gov.sandia.dart.aprepro.util.ApreproUtil;

import java.util.Collection;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;

/**
 * @author mjgibso
 *
 */
public class ApreproPropertyTester extends org.eclipse.core.expressions.PropertyTester
{
	private static final String CAN_CREATE_PARAMETER = "canCreateParameter";
	private static final String CAN_USE_PARAMETER = "canUseParameter";
	
	public ApreproPropertyTester()
	{}

	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
	{
//		System.out.println("Test: "+receiver+", "+property+", "+args+", "+expectedValue);
		
		if(CAN_CREATE_PARAMETER.equals(property))
		{
			TextSelection sel = getTextSelection(receiver);
			if(sel == null)
			{
				return false;
			}
			boolean expected = true;
			if(expectedValue instanceof Boolean)
			{
				expected = ((Boolean) expectedValue).booleanValue();
			} else if(expectedValue instanceof String) {
				expected = Boolean.parseBoolean((String) expectedValue);
			}
			
			return (sel.getLength()>0) == expected;
		} else if(CAN_USE_PARAMETER.equals(property)) {
			TextSelection sel = getTextSelection(receiver);
			if(sel == null)
			{
				return false;
			}
			boolean expected = true;
			if(expectedValue instanceof Boolean)
			{
				expected = ((Boolean) expectedValue).booleanValue();
			} else if(expectedValue instanceof String) {
				expected = Boolean.parseBoolean((String) expectedValue);
			}
			if(sel.getLength()>0 != expected)
			{
				return false;
			}
			// now, even though the TextSelection has the document, we can't get it, so we have to get it manually.
			// this is a lame solution, ripped off from useExistingParameter.
			// get document
			IDocument document = ParameterHandler.getDocument();

			//Check to see if there are variables already exisiting!
			try {
				return ApreproUtil.buildApreproVariableList(document).size()>0 == expected;
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// TODO Auto-generated method stub
		return false;
	}
	
	private TextSelection getTextSelection(Object receiver)
	{
		if(receiver instanceof TextSelection)
		{
			return (TextSelection) receiver;
		}
		
		if(receiver instanceof Collection<?>)
		{
			for(Object o : (Collection<?>) receiver)
			{
				if(o instanceof TextSelection)
				{
					return (TextSelection) o;
				}
			}
		}
		
		return null;
	}
}
