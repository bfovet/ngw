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
package com.strikewire.snl.apc.commands;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

/**
 * @author mjgibso
 *
 */
public abstract class CompoundContributionItemWithContext extends CompoundContributionItem implements IWorkbenchContribution
{
	protected IServiceLocator serviceLocator_;
	
	public CompoundContributionItemWithContext()
	{
		super();
	}
	
	public CompoundContributionItemWithContext(String id)
	{
		super(id);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.menus.IWorkbenchContribution#initialize(org.eclipse.ui.services.IServiceLocator)
	 */
	@Override
	public void initialize(IServiceLocator serviceLocator)
	{
		this.serviceLocator_ = serviceLocator;
	}
	
	protected IEvaluationContext getCurrentState()
	{
		IHandlerService ihs = getHandlerService();
		return ihs!=null ? ihs.getCurrentState() : null;
	}
	
	protected ISelection getActiveMenuSelection()
	{
		return getSelectionVariable("activeMenuSelection");
	}
	
	protected ISelection getSelection()
	{
		return getSelectionVariable("selection");
	}
	
	protected ISelection getSelectionVariable(String varName)
	{
		IEvaluationContext ctx = getCurrentState();
		if(ctx == null)
		{
			return null;
		}
		
		Object ams = ctx.getVariable(varName);
		return ams instanceof ISelection ? (ISelection) ams : null;
	}
	
	protected IHandlerService getHandlerService()
	{
		IServiceLocator srvLoc = serviceLocator_;
		if(srvLoc == null)
		{
			return null;
		}
		
		Object ihsObj = srvLoc.getService(IHandlerService.class);
		return ihsObj instanceof IHandlerService ? (IHandlerService) ihsObj : null;
	}
}
