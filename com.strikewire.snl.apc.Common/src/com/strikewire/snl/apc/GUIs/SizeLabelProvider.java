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
 * On May 4, 2006 at 3:57:19 PM
 */
package com.strikewire.snl.apc.GUIs;

import gov.sandia.dart.common.core.formatting.FilesizeFmtOpts;
import gov.sandia.dart.common.core.formatting.FormatterFactory;
import gov.sandia.dart.common.core.formatting.IFilesizeFmtOpts;
import gov.sandia.dart.common.core.formatting.IFilesizeFormatter;
import gov.sandia.dart.common.core.formatting.IFilesizeFormatter.EUnits;

import java.math.BigDecimal;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * Formats a size into a human readable output. Allows for
 * specifying the input size (bytes, kilobytes, etc), and 
 * formats out into a standard formatting based upon
 * the FilesizeFormatter. Will handle input between 0
 * and a rather large number, though after a sufficient
 * size becomes "1000+ YB", though none of us will be around
 * for a file of that size to be transferred.
 * 
 * @see http://en.wikipedia.org/wiki/SI_prefix
 * @author Marcus Gibson
 *
 */
public class SizeLabelProvider extends LabelProvider
{
  /**
   * _fsFormatter - Formats filesizes
   */
  private final IFilesizeFormatter _fsFormatter =
      FormatterFactory.filesizeFormatter();
  /*
   * The units in which the output is to be expressed
   */
	public static enum Units { 
	  Bytes(0, "bytes"),
	  KB(1, "KB"),
	  MB(2, "MB"),
	  GB(3, "GB"),
	  TB(4, "TB"),
	  PB(5, "PB"),
	  EB(6, "EB"),
	  ZB(7, "ZB"),
	  YB(8, "YB"),
	  
	  ;
	
	  private final int _power;
	  private final String _suffix;
	  
	  private Units(int power, String suffix)
	  {
	    _power = power;
	    _suffix = suffix;
	  }
	  
	  public int getPower()
	  {
	    return _power;
	  }
	  
	  public String getSuffix()
	  {
	    return _suffix;
	  }
	};
	
	private Units units_;
	
	
	/**
	 * @param units The units that the inbound size is expressed in. 
	 * Filesizes, for example, are normally expressed in bytes.
	 */
	public SizeLabelProvider(Units units)
	{ 
	  this.units_ = units; 
	}
	
	
	@Override
	public String getText(Object element)
	{
		String origValue = super.getText(element);
		
		// clean up the number
		StringBuilder sb = new StringBuilder();
		for(char c : origValue.toCharArray())
		{
			if(Character.isDigit(c) || '.'==c || 'E'==c || '('==c || ')'==c || '-'==c || ','==c)
			{
				sb.append(c);
			}
		}
		String stringValue = sb.toString();
		double bytesValue;
		try {
			bytesValue = Double.parseDouble(stringValue);
		} catch (Exception e) {
			return origValue;
		}
		
		
		//
		// we will use binary as the base
		//
		final int base = IFilesizeFormatter.EUnits.BINARY.getBase();

		if(units_ != Units.Bytes)
		{
			//
			// convert the inbound and parsed value which was expressed in some
			// units into bytes. So, if the inbound value was being expressed
			// in Kilobytes, then to get to bytes we have to multiply the
			// initial value (e.g., 6) by the base (e.g., 1024) to get to 6144 bytes.
			//
			bytesValue *= (base * units_.getPower());
		}
		
		//
		// now that we are in bytes, we can format
		//
		BigDecimal bigBytes = new BigDecimal(bytesValue);

		String value = origValue;
		
		try {
		  IFilesizeFmtOpts fmtOpts =
		      new FilesizeFmtOpts().setByteAsWord(true)
		        .setReturnTrimmed(true);
		  value = _fsFormatter.bytesToHumanReadable(bigBytes, 
		      EUnits.BINARY,
		      fmtOpts);
		}
		catch (StringIndexOutOfBoundsException noop) {
		}
		
//		int count = 0;
//		double doubleValue = bytesValue;
//		while(doubleValue >= base)
//		{
//			doubleValue/=base;
//			count++;
//		}
//		NumberFormat formater = NumberFormat.getInstance();
//		formater.setMaximumFractionDigits(1);
//		String value = formater.format(doubleValue);
//		
//		String units;
//		if(count < unitStrings_.length)
//			units = unitStrings_[count];
//		else
//			units = "?";
//		
//		return value + " "+units;
		
		return value;
	}
}
