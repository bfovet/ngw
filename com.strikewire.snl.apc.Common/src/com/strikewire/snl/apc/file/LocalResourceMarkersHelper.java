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
 * Created by mjgibso on Mar 14, 2017 at 4:36:01 PM
 */
package com.strikewire.snl.apc.file;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * @author mjgibso
 *
 */
public class LocalResourceMarkersHelper {

	/**
	 * 
	 */
	public LocalResourceMarkersHelper()
	{
	}

	/**
	 * Sets the value for the key on the specified resource.
	 * 
	 * @return true if the value was changed
	 * @since 2.10
	 */
	public boolean setAttribute(IResource res, String typeID, String key, String value)
	{
		if(key == null)
		{
			return false;
		}

		try
		{
			IMarker infoMarker = getVersionInfoMarker(res, typeID, true);
			if(infoMarker == null)
			{
				System.err.println("null marker");
				return false;
			}
			
			Object oldVal = infoMarker.getAttribute(key, null);
			if(oldVal==null && value==null)
			{
				return false;
			}
			
			if(oldVal!=null && value!=null && oldVal.equals(value))
			{
				return false;
			}
			
			infoMarker.setAttribute(key, value);
			
			return true;
		} catch (CoreException e) {
			CommonPlugin.getDefault().logError("Error storing version info: " + e.getMessage(), e);
			return false;
		}
	}

	/**
	 * For the specified resource, obtain the value of the marker for the
	 * specified key. May return null.
	 * 
	 * @since 2.10
	 */
	public String getAttribute(IResource res, String typeID, String key)
	{
		if(key == null)
		{
			return null;
		}

		try {
			IMarker infoMarker = getVersionInfoMarker(res, typeID, false);
			return infoMarker!=null ? (String) infoMarker.getAttribute(key, null) : null;
		} catch (CoreException e) {
			CommonPlugin.getDefault().logError("Error retrieving version info: " + e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Obtains the Marker for the specified resource, creating the marker if
	 * absent if create is true.
	 * 
	 * @since 2.10
	 */
	private IMarker getVersionInfoMarker(IResource res, String typeID, boolean create) throws CoreException
	{
		if(res == null)
		{
			return null;
		}

		if(!res.exists())
		{
			return null;
		}

		IMarker[] markers;
		if(create)
		{
			// TODO synchronize not around this, but on a lock corresponding to
			// the path
			// of the given resource. Right now, if two thread have unique
			// instances of
			// this, and they both call in here, they could step on each other
			// creating
			// markers.
			synchronized(this)
			{
				markers = res.findMarkers(typeID, false, IResource.DEPTH_ZERO);
				if(markers==null || markers.length<1)
				{
					return create ? res.createMarker(typeID) : null;
				}
			}
		} else {
			markers = res.findMarkers(typeID, false, IResource.DEPTH_ZERO);
		}

		if(markers!=null && markers.length>0)
		{
			if(markers.length > 1)
			{
				String msg = "Too many version info markers for the resource: "
						+ res.getFullPath() + ".  There should only be one.  "
						+ "There are " + markers.length + ".";
				CommonPlugin.getDefault().logError(msg, new IllegalStateException());
			}

			return markers[0];
		}

		return null;
	}
}
