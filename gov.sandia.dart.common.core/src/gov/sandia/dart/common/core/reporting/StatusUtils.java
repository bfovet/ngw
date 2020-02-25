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
package gov.sandia.dart.common.core.reporting;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author mjgibso
 *
 */
public class StatusUtils
{
	public static final String DEFAULT_MULTI_MESSAGE = "Process completed with multiple warnings or errors.";
	public static final boolean DEFAULT_ALLOW_MERGE = false;
	
	private StatusUtils()
	{}

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
	
	public static IStatus mergeStatus(List<IStatus> stati, String pluginID, String multiMessage)
	{
		if(stati==null || stati.isEmpty())
		{
			return null;
		}
		
		List<IStatus> filteredStati = stati.stream().filter(s -> !s.isOK()).collect(Collectors.toList());
		if(filteredStati.isEmpty())
		{
			return Status.OK_STATUS;
		}
		if(filteredStati.size() == 1)
		{
			return filteredStati.iterator().next();
		}
		
		return new MultiStatus(pluginID, IStatus.OK, filteredStati.toArray(new IStatus[filteredStati.size()]), multiMessage, null);
	}
}
