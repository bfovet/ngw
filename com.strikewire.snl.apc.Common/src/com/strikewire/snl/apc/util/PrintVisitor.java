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
package com.strikewire.snl.apc.util;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * @author mjgibso
 *
 */
public class PrintVisitor implements IResourceDeltaVisitor
{
	private interface NamedMask
	{
		public String getName();
		public int getMask();
	}
	
	public static enum KIND implements NamedMask
	{
		ADDED("Added", IResourceDelta.ADDED),
		REMOVED("Removed", IResourceDelta.REMOVED),
		CHANGED("Changed", IResourceDelta.CHANGED),
		NO_CHANGE("No Change", IResourceDelta.NO_CHANGE),
		ADDED_PHANTOM("Added Phantom", IResourceDelta.ADDED_PHANTOM),
		REMOVED_PHANTOM("Removed Phantom", IResourceDelta.REMOVED_PHANTOM);
		
		public final String name;
		public final int mask;
		
		private KIND(String name, int mask)
		{
			this.name = name;
			this.mask = mask;
		}
		
		/* (non-Javadoc)
		 * @see com.strikewire.snl.apc.util.PrintVisitor.type#getName()
		 */
		@Override
		public String getName()
		{
			return this.name;
		}
		
		/* (non-Javadoc)
		 * @see com.strikewire.snl.apc.util.PrintVisitor.type#getMask()
		 */
		@Override
		public int getMask()
		{
			return this.mask;
		}
	}
	
	public static enum FLAG implements NamedMask
	{
		CONTENT("Content", IResourceDelta.CONTENT),
		COPIED_FROM("Copied From", IResourceDelta.COPIED_FROM),
		DERIVED_CHANGED("Derived Changed", IResourceDelta.DERIVED_CHANGED),
		DESCRIPTION("Description", IResourceDelta.DESCRIPTION),
		ENCODING("Encoding", IResourceDelta.ENCODING),
		LOCAL_CHANGED("Local Changed", IResourceDelta.LOCAL_CHANGED),
		MARKERS("Markers", IResourceDelta.MARKERS),
		MOVED_FROM("Moved From", IResourceDelta.MOVED_FROM),
		MOVED_TO("Moved To", IResourceDelta.MOVED_TO),
		OPEN("Open", IResourceDelta.OPEN),
		REPLACED("Replaced", IResourceDelta.REPLACED),
		SYNC("Sync", IResourceDelta.SYNC),
		TYPE("Type", IResourceDelta.TYPE);
		
		
		public final String name;
		public final int mask;
		
		private FLAG(String name, int mask)
		{
			this.name = name;
			this.mask = mask;
		}
		
		/* (non-Javadoc)
		 * @see com.strikewire.snl.apc.util.PrintVisitor.type#getName()
		 */
		@Override
		public String getName()
		{
			return this.name;
		}
		
		/* (non-Javadoc)
		 * @see com.strikewire.snl.apc.util.PrintVisitor.type#getMask()
		 */
		@Override
		public int getMask()
		{
			return this.mask;
		}
	}

	private String indent_ = "";
	
	public PrintVisitor reset()
	{
		this.indent_ = "";
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException
	{
		StringBuilder sb = new StringBuilder(indent_);
		sb.append("RCE: ");
		sb.append(delta.getFullPath());
		sb.append("|(");
		int kinds = delta.getKind();
		sb.append(kinds);
		sb.append(')');
		writeTypes(KIND.values(), kinds, sb);
		sb.append("|(");
		int flags = delta.getFlags();
		sb.append(flags);
		sb.append(')');
		writeTypes(FLAG.values(), flags, sb);
		if(0 != (flags & IResourceDelta.MARKERS))
		{
			sb.append('[');
			IMarkerDelta[] mDeltas = delta.getMarkerDeltas();
			boolean firstHit = true;
			for(IMarkerDelta mDelta : mDeltas)
			{
				if(!firstHit)
				{
					sb.append(';');
				} else {
					firstHit = false;
				}
				sb.append(mDelta.getType());
				sb.append("|(");
				int markerKinds = mDelta.getKind();
				sb.append(markerKinds);
				sb.append(')');
				writeTypes(KIND.values(), markerKinds, sb);
			}
			sb.append(']');
		}
		System.out.println(sb);
		indent_ = indent_ + " ";
		return true;
	}
	
	public void writeTypes(NamedMask[] options, int mask, StringBuilder out)
	{
		boolean firstHit = true;
		for(NamedMask nm : options)
		{
			if((mask & nm.getMask()) != 0)
			{
				if(!firstHit)
				{
					out.append(",");
				} else {
					firstHit = false;
				}
				out.append(nm.getName());
			}
		}
	}

}
