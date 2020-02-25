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
 * Created on Sep 30, 2008 at 11:10:06 AM
 */
package com.strikewire.snl.apc.GUIs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.GUIs.SizeLabelProvider.Units;


/**
 * @author mjgibso
 *
 */
public abstract class TransferMonitor<E>
{
	// Can't set this too high.  At a billion, the progress bar doesn't move properly
	private static final int TOTAL_TICKS = 10*1000;
	
	private static final int BYTES_PER_FILE = 512*1024;
	
	private static final SizeLabelProvider sizeFormatter_ = new SizeLabelProvider(Units.Bytes);
	
	private final IProgressMonitor monitor_;
	
	private final int totalFiles_;
	private final long totalBytes_;
	private final int ticksPerFile_;
	
	private final String taskName_;
	private final long startTime_;
	
	private boolean transferLogged_ = false;
	
	private Map<E, FileMonitor> files_ = new HashMap<E, FileMonitor>();
	
	private int done_ = 0;
	private int unknownDone_ = 0;
	
	protected TransferMonitor(IProgressMonitor monitor, Collection<E> files, String taskName)
	{
		this.monitor_ = TimedProgressMonitor.getTimedProgressMonitor(monitor);
//		System.out.println("new transfer monitor, new timed monitor: "+(monitor==this.monitor_));
		
		this.totalFiles_ = files.size();
		
//		System.out.println("  num files: "+files.size());
		
		long totalBytes = 0;
		// cache the sizes in case they aren't quick to lookup
		Map<E, Long> sizes = new HashMap<E, Long>(files.size());
		for(E file : files)
		{
			long size = getSize(file);
			sizes.put(file, size);
			totalBytes += size;
		}
		totalBytes_ = totalBytes;
//		System.out.println("  total bytes: "+totalBytes_);
		
		this.taskName_ = taskName!=null ? taskName : "transferring files";
		
		this.monitor_.beginTask(taskName, TOTAL_TICKS);
		
		int fileBytes = totalFiles_ * BYTES_PER_FILE;
		double filePlusTotalBytes = fileBytes + totalBytes_;
		double fileBytesFraction = filePlusTotalBytes!=0 ? fileBytes / filePlusTotalBytes : fileBytes;
		int totalFileTicks = (int) (TOTAL_TICKS * fileBytesFraction);
		int totalSizeTicks = TOTAL_TICKS - totalFileTicks;
		ticksPerFile_ = totalFiles_!=0 ? totalFileTicks / totalFiles_ : totalFileTicks;
		
		for(E file : files)
		{
			long size = sizes.get(file);
			double percentOfTotalTicks = ((double) size) / ((double) totalBytes_);
			int useTicks = (int) (totalSizeTicks * percentOfTotalTicks);
//			System.out.println(" making new file mon for file: "+file+", size: "+size+", percent of total ticks: "+percentOfTotalTicks+", ticks to use: "+useTicks);
			files_.put(file, new FileMonitor(size, useTicks+ticksPerFile_));
		}
		
		startTime_ = System.currentTimeMillis();
//		System.out.println("  start time: "+startTime_);
	}
	
	protected abstract long getSize(E file);
	
	public synchronized Collection<E> getFiles()
	{ return files_.keySet(); }
	
	public synchronized void fileComplete(E file, IStatus status)
	{
		FileMonitor mon = this.files_.get(file);
		if(mon != null)
		{
			mon.doneInternal();
			mon.status_ = status;
			done_++;
			
			checkForDone();
		}
		
		updateMonitor();
	}
	
	public synchronized void unknownFileComplete()
	{
		done_++;
		unknownDone_++;
		checkForDone();
		updateMonitor();
	}
	
	private synchronized void checkForDone()
	{
		if(monitor_.isCanceled())
		{
			return;
		}
		
		if(logTransferCompletion())
		{
			monitor_.done();
		}
	}
	
	protected synchronized boolean logTransferCompletion()
	{
		if(done_ < this.files_.size())
		{
			return false;
		}
		
		if(transferLogged_)
		{
			return true;
		}
		
		int errors = 0;
		int cancels = 0;
		int skips = 0;
		long successBytes_ = 0L;
		
		int unfinishedFMs = 0;
		for(FileMonitor fm : this.files_.values())
		{
			if(!fm.done_ || fm.status_==null)
			{
				if(++unfinishedFMs > unknownDone_)
				{
					return false;
				} else {
					errors++;
					continue;
				}
			}
			
			if(fm.status_.matches(IStatus.CANCEL))
			{
				cancels++;
			} else if(fm.status_.matches(IStatus.ERROR)) {
				errors++;
			} else if(fm.status_.matches(IStatus.INFO)) {
				String msg = fm.status_.getMessage().toLowerCase();
				if(msg.contains("skipping") && msg.contains("checksum"))
				{
					skips++;
				}
			} else {
				successBytes_ += fm.size_;
			}
		}
		
		int problems = errors + cancels;
		long finishTime = System.currentTimeMillis();
		long timeElapsed = finishTime - startTime_;
		double transferRate = (successBytes_)
				/ ((timeElapsed) / 1000.0);
		StringBuilder msg = new StringBuilder();
		if(problems > 0)
		{
			msg.append("Problems occurred ");
		} else {
			msg.append("Completed ");
		}
		msg.append(taskName_);
		if(!taskName_.endsWith("."))
		{
			msg.append(".");
		}
		msg.append("\n  Elapsed time: ");
		msg.append(DurationFormatUtils.formatDurationHMS(timeElapsed));
		msg.append(".\n  Total Files: ");
		msg.append(totalFiles_);
		msg.append(".\n    Successfully Transferred: ");
		msg.append(totalFiles_-problems-skips);
		msg.append(".\n    Checksum Skipped: ");
		msg.append(skips);
		msg.append(".\n    Errors: ");
		msg.append(errors);
		msg.append(".\n    Cancelled: ");
		msg.append(cancels);
		msg.append(".\n  Total size: ");
		msg.append(sizeFormatter_.getText(totalBytes_));
		msg.append(".\n  Succesfully transferred: "+sizeFormatter_.getText(successBytes_));
		msg.append(".\n  Transfer rate: ");
		msg.append(sizeFormatter_.getText(transferRate));
		msg.append("/s.");
		int severity = problems > 0 ? (problems==totalFiles_ ? IStatus.ERROR : IStatus.WARNING) : IStatus.INFO;
		IStatus timingStatus = new Status(severity, CommonPlugin.ID, msg.toString());
		try {
			CommonPlugin.getDefault().log(timingStatus);
			transferLogged_ = true;
			return true;
		} catch (Exception eCannotLog) {
			return false;
			// nothing to report;
		}
	}

	private synchronized void updateMonitor()
	{
		int filesComplete = 0;
		long bytesComplete = 0;
		for(FileMonitor file : this.files_.values())
		{
			if(file.done_)
			{
				bytesComplete += file.getTotalBytes();
				filesComplete++;
			} else {
				bytesComplete += file.getBytesComplete();
			}
		}
		
		monitor_.subTask("Completed "+filesComplete+" of "+totalFiles_+" files ("+sizeFormatter_.getText(bytesComplete)+" of "+sizeFormatter_.getText(totalBytes_)+").");
	}
	
	public synchronized FileMonitor getFileMonitor(E file)
	{ return files_.get(file); }
	
	public class FileMonitor extends SubProgressMonitor
	{
		private final long size_;
		
		private int totalWork_;
		private int worked_ = 0;
		private boolean done_ = false;
		private IStatus status_ = null;
		
		/**
		 * @param monitor
		 * @param ticks
		 */
		public FileMonitor(long size, int useTicks)
		{
			super(TransferMonitor.this.monitor_, useTicks, SubProgressMonitor.SUPPRESS_SUBTASK_LABEL);
			
			this.size_ = size;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.SubProgressMonitor#beginTask(java.lang.String, int)
		 */
		@Override
		public void beginTask(String name, int totalWork)
		{
//			System.out.println("File Monitor begin task: "+name+", total work: "+totalWork);
			super.beginTask(name, totalWork);
			
			this.totalWork_ = totalWork;
			
			updateMonitor();
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.SubProgressMonitor#worked(int)
		 */
		@Override
		public void worked(int work)
		{
//			System.out.println("File monitor worked: "+work+", done: "+done_);
			super.worked(work);
			
			this.worked_ += work;
			
			if(worked_ >= totalWork_)
			{
//				System.out.println("done by work");
				doneInternal();
			}
			
			updateMonitor();
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.SubProgressMonitor#done()
		 */
		@Override
		public void done()
		{
//			System.out.println("File Monitor: Done");
			super.done();
			
			updateMonitor();
			
			doneInternal();
		}
		
		void doneInternal()
		{
//			System.out.println("File Monitor: Done internal");
			this.done_ = true;
			
//			checkForDone();
		}
		
		long getBytesComplete()
		{
			double percentComplete = ((double) this.worked_)/totalWork_;
			
			return (long) (percentComplete*getTotalBytes());
		}
		
		long getTotalBytes()
		{ return this.size_; }
	}
}
