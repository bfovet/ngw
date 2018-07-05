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
 * Created on Apr 29, 2008 at 11:55:43 AM
 */
package com.strikewire.snl.apc.GUIs;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ProgressMonitorWrapper;

/**
 * @author mjgibso
 *
 */
public class TimedProgressMonitor extends ProgressMonitorWrapper
{
	protected static final long MIN_SUB_TASK_UPDATE_INTERVAL = 100;
	
//	protected static final long AVG_RATE_SPAN = 10 * 1000;
	
	protected static final String CALCULATING = "Calculating";
	
	protected static final String ELAPSED_FORMAT = "HH:mm:ss";
	
	private long startTime_;
	private int totalWork_;
	private double worked_;
	private String baseSubTask_ = "";
	
	private long lastSubtaskUpdateTime_;
	
//	private LinkedList<Tuple<Long, Double>> work_ = new LinkedList<Tuple<Long,Double>>();
	
	private TimedProgressMonitor(IProgressMonitor monitor)
	{
		super(monitor);
	}
	
	public static IProgressMonitor getTimedProgressMonitor(IProgressMonitor monitor)
	{
		if(monitor instanceof TimedProgressMonitor)
		{
			return monitor;
		} else {
			return new TimedProgressMonitor(monitor);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#beginTask(java.lang.String, int)
	 */
	@Override
	public void beginTask(String name, int totalWork)
	{
		this.totalWork_ = totalWork;
		this.worked_ = 0;
		startTime_ = System.currentTimeMillis();
		super.beginTask(name, totalWork);
		updateSubTask();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)
	 */
	@Override
	public void internalWorked(double work)
	{
		this.addWork(work);
		super.internalWorked(work);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
	 */
	@Override
	public void worked(int work)
	{
		this.addWork(work);
		super.worked(work);
	}
	
	private void addWork(double work)
	{
		this.worked_ += work;
//		work_.add(new Tuple<Long, Double>(System.currentTimeMillis(), work));
		updateSubTask();
	}
	
//	public void setWorked(int totalWorked)
//	{
//		if(this.worked_ > totalWorked)
//		{
//			throw new IllegalArgumentException("Cannot do negative work...");
//		}
//		
//		if(this.worked_ == totalWorked)
//		{
//			return;
//		}
//		
//		worked(totalWorked - this.worked_);
//	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.ProgressMonitorWrapper#subTask(java.lang.String)
	 */
	@Override
	public void subTask(String name)
	{
		baseSubTask_ = name;
		updateSubTask();
	}
	
	private void updateSubTask()
	{
		long timeSinceLastUpdate = System.currentTimeMillis() - lastSubtaskUpdateTime_;
		if(timeSinceLastUpdate < MIN_SUB_TASK_UPDATE_INTERVAL)
		{
			return;
		}
		
		lastSubtaskUpdateTime_ = System.currentTimeMillis();
		
		super.subTask(addTimeInfo(baseSubTask_));
	}
	
	private String addTimeInfo(String baseString)
	{
		if(isCanceled())
		{
			return baseString;
		}
		
		String elapsed = calcElapsed();
		String timeRemaining = calcRemaining();
//		String timeRemaining = calcRemaining2();
		
		if(CALCULATING.equals(timeRemaining) && CALCULATING.equals(elapsed))
		{
			return baseString;
		}
		
		// while it's not empty and it ends with a '.' or whitespace, take off the last character
		while(baseString.length()>0 && (baseString.endsWith(".") || Character.isWhitespace(baseString.charAt(baseString.length()-1))))
		{
			baseString = baseString.substring(0, baseString.length()-1);
		}
		
		if(baseString.trim().length() < 1)
		{
			baseString = baseString.trim();
		} else {
			baseString = baseString + ".  ";
		}
		
		StringBuilder sb = new StringBuilder(baseString);
		sb.append("Elapsed: ");
		sb.append(elapsed);
		sb.append(".  Remaining: ");
		sb.append(timeRemaining);
		sb.append("...");
		return sb.toString();
	}
	
	private String calcElapsed()
	{
		long elapsed = System.currentTimeMillis() - startTime_;
		return DurationFormatUtils.formatDuration(elapsed, ELAPSED_FORMAT);
	}
	
	private String calcRemaining()
	{
		double percentComplete = worked_/(double) totalWork_;
//		System.out.println("worked: "+worked_+", total work: "+totalWork_);
//		System.out.println("Percent Complete: "+percentComplete);
		if(percentComplete>=1.0 || percentComplete<=0.0)
			return CALCULATING;
		
		long elapsedTime = System.currentTimeMillis()-startTime_;
		long totalTime = (long) (((double) elapsedTime)/percentComplete);
		long timeRemaining = totalTime-elapsedTime;
		
		return formatRemainingTime(timeRemaining);
	}
	
//	private String calcRemaining2()
//	{
//		if(work_.size() < 1)
//		{
//			return CALCULATING;
//		}
//		
//		Tuple<Long, Double> work;
//		long now = System.currentTimeMillis();
//		long startTime = now - AVG_RATE_SPAN;
//		while((work=work_.peekFirst())!=null && work.getLeft()<startTime)
//		{
//			work_.removeFirst();
//		}
//		
//		if(work_.size() < 1)
//		{
//			return CALCULATING;
//		}
//		
//		double workInSpan = 0;
//		long spanStart = work_.peekFirst().getLeft();
//		long spanEnd = now;
//		long actualSpan = spanEnd - spanStart;
//		for(Tuple<Long, Double> w : work_)
//		{
//			workInSpan += w.getRight();
//		}
//		
//		double rate = workInSpan / actualSpan;
//		
//		System.out.println("======Rate: "+rate+", work: "+workInSpan+", span: "+actualSpan);
//		
//		if(rate <= 0)
//		{
//			return CALCULATING;
//		}
//		
//		double workRemaining = totalWork_ - worked_;
//		
//		long timeRemaining = (long) (workRemaining / rate);
//		
//		return formatRemainingTime(timeRemaining);
//	}
	
	private static String formatRemainingTime(long miliseconds)
	{
		if(miliseconds <= 0)
		{
			return CALCULATING;
		}
		
		long secs = miliseconds/1000;
		if(secs < 60)
		{
			String sRet = secs+" second";
			if(secs != 1)
			{
				sRet += "s";
			}
			return sRet;
		}
		
		long mins = secs/60;
		if(mins < 60)
		{
			String sRet = mins+" minute";
			if(mins != 1)
			{
				sRet += "s";
			}
			return sRet;
		}
		
		long hours = mins/60;
		if(hours < 24)
		{
			String sRet = hours+" hour";
			if(hours != 1)
			{
				sRet += "s";
			}
			return sRet;
		}
		
		long days = hours/24;
		if(days < 7)
		{
			String sRet = days+" day";
			if(days != 1)
			{
				sRet += "s";
			}
			return sRet;
		}
		
		long weeks = days/7;
		if(days < 30)
		{
			String sRet = weeks+" week";
			if(weeks != 1)
			{
				sRet += "s";
			}
			return sRet;
		}
		
		long months = (long) (days/(((double) 365)/((double) 12)));
		if(months < 12)
		{
			String sRet = months+" month";
			if(months != 1)
			{
				sRet += "s";
			}
			return sRet;
		}
		
		long years = days/365;
		String sRet = years+" year";
		if(years != 1)
		{
			sRet += "s";
		}
		return sRet;
	}
}
