/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/**
 * 
 */
package com.strikewire.snl.apc.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;

/**
 * @author mjgibso
 *
 */
public class ValidationUtils
{
	public static final int ALL_SEVERITY_MASK = IStatus.INFO | IStatus.WARNING | IStatus.ERROR | IStatus.CANCEL;
	
	public static final Comparator<IStatus> SEVERITY_COMPARATOR = new Comparator<IStatus>() {
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(IStatus s1, IStatus s2)
		{
			return s2.getSeverity() - s1.getSeverity();
		}
	};
	
	public static IStatus getSeverest(IStatus status)
	{
		if(status == null)
		{
			return null;
		}
		
		IStatus severest = status;
		for(IStatus child : status.getChildren())
		{
			severest = getSeverest(severest, getSeverest(child));
		}
		return severest;
	}
	
	public static IStatus getSeverest(IStatus status1, IStatus status2)
	{
		if(status1==null && status2==null)
		{
			return null;
		} else if(status1 == null) {
			return status2;
		} else if(status2 == null) {
			return status1;
		}
		
		return status1.getSeverity()>status2.getSeverity() ? status1 : status2;
	}
	
	public static List<IStatus> getAllStatus(IStatus status, boolean includeMulti)
	{
		List<IStatus> allStatus = new ArrayList<IStatus>();
		if(status == null)
		{
			return allStatus;
		}
		if(!status.isMultiStatus() || includeMulti)
		{
			allStatus.add(status);
		}
		for(IStatus child : status.getChildren())
		{
			allStatus.addAll(getAllStatus(child, includeMulti));
		}
		return allStatus;
	}
	
	public static List<IStatus> getAllStatus(IStatus status, int severityMask, boolean includeMulti)
	{ return getAllStatus(getAllStatus(status, includeMulti), severityMask, includeMulti); }
	
	public static List<IStatus> getAllStatus(List<IStatus> status, int severityMask, boolean includeMulti)
	{
		List<IStatus> allStatus = new ArrayList<IStatus>();
		if(status==null || status.size()<1)
		{
			return allStatus;
		}
		for(IStatus s : status)
		{
			if(s.matches(severityMask) && (!s.isMultiStatus() || includeMulti))
			{
				allStatus.add(s);
			}
		}
		return allStatus;
	}
	
	public static List<IStatus> prependMessage(List<IStatus> status, String message)
	{
		List<IStatus> newStatus = new ArrayList<IStatus>();
		if(status == null)
		{
			return newStatus;
		}
		for(IStatus s : status)
		{
			newStatus.add(new Status(s.getSeverity(), s.getPlugin(), s.getCode(), message+s.getMessage(), s.getException()));
		}
		return newStatus;
	}
	
	public static void applyToStatusLine(DialogPage page, IStatus status)
	{
		applyToStatusLine(page, status, false);
	}
	
	/**
	 * Applies the status to the status line of a dialog page.
	 * 
	 * @param page the dialog page
	 * @param status the status
	 * @param useErrorMessage use the setErrorMessage method
	 */
	public static void applyToStatusLine(DialogPage page, IStatus status, boolean useErrorMessage)
	{
		String message= status.getMessage();
		switch (status.getSeverity()) {
			case IStatus.OK:
				page.setMessage(null, IMessageProvider.NONE);
				page.setErrorMessage(null);
				break;
			case IStatus.WARNING:
				page.setMessage(message, IMessageProvider.WARNING);
				page.setErrorMessage(null);
				break;
			case IStatus.INFO:
				page.setMessage(message, IMessageProvider.INFORMATION);
				page.setErrorMessage(null);
				break;
			default:
				if (message.length() == 0) {
					message= null;
				}
				if(useErrorMessage)
				{
					page.setErrorMessage(message);
					page.setMessage(null);
				} else {
					page.setMessage(message, IMessageProvider.ERROR);
				}
				break;
		}
	}
	
	public static String generateMessage(IStatus status, boolean recursive, boolean includeMulti, boolean prependType)
	{
		return generateMessage(status, recursive, includeMulti, prependType, -1);
	}
	
	public static String generateMessage(IStatus status, boolean recursive, boolean includeMulti, boolean prependType, int maxLines)
	{
		return generateMessage(status, recursive, ALL_SEVERITY_MASK, includeMulti, prependType, maxLines);
	}
	
	public static String generateMessage(IStatus status, boolean recursive, int severityMask, boolean includeMulti, boolean prependType)
	{
		return generateMessage(status, recursive, severityMask, includeMulti, prependType, -1);
	}
	
	public static String generateMessage(IStatus status, boolean recursive, int severityMask, boolean includeMulti, boolean prependType, int maxLines)
	{
		if(status == null)
		{
			return StringUtils.EMPTY;
		}
		
		if(status.isOK())
		{
			return StringUtils.EMPTY;
		}
		
		List<IStatus> statuses;
		if(recursive)
		{
			statuses = getAllStatus(status, severityMask, includeMulti);
		} else {
			statuses = new ArrayList<IStatus>();
			if((!status.isMultiStatus() || includeMulti) && status.matches(severityMask))
			{
				statuses.add(status);
			}
		}
		
		if(statuses.isEmpty())
		{
			return StringUtils.EMPTY;
		}
		
		Collections.sort(statuses, SEVERITY_COMPARATOR);
		
		StringBuilder sb = new StringBuilder();
		boolean truncated = false;
		if(maxLines>0 && statuses.size()>maxLines)
		{
			truncated = true;
			while(statuses.size() >= maxLines)
			{
				statuses.remove(statuses.size()-1);
			}
		}
		
		for(int i=0; i<statuses.size() && (maxLines<1 || i<maxLines); i++)
		{
			IStatus s = statuses.get(i);
			if(prependType)
			{
				switch(s.getSeverity())
				{
					case IStatus.ERROR:
						sb.append("ERROR: ");
						break;
					case IStatus.WARNING:
						sb.append("WARNING: ");
						break;
					case IStatus.INFO:
						sb.append("INFO: ");
						break;
				}
			}
			sb.append(s.getMessage());
			sb.append('\n');
		}
		if(truncated)
		{
			sb.append("...");
		} else {
			int len = sb.length();
			if(len>0 && sb.charAt(len-1)=='\n')
			{
				sb.deleteCharAt(len-1);
			}
		}
		return sb.toString();
	}
	
	public static int convertSeverityMarkerToStatus(int markerSeverity)
	{
		switch(markerSeverity)
		{
			case IMarker.SEVERITY_INFO: return IStatus.INFO;
			case IMarker.SEVERITY_WARNING: return IStatus.WARNING;
			case IMarker.SEVERITY_ERROR: return IStatus.ERROR;
			default: return -1;
		}
	}
	
	public static int convertSeverityStatusToMessageProvider(int statusSeverity)
	{
		switch(statusSeverity)
		{
			case IStatus.INFO: return IMessageProvider.INFORMATION;
			case IStatus.WARNING: return IMessageProvider.WARNING;
			case IStatus.ERROR: return IMessageProvider.ERROR;
			default: return IMessageProvider.NONE;
		}
	}
}
