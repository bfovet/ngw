/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.GUIs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.strikewire.snl.apc.osgi.util.EventUtils;

public class AbstractPerspectiveWithDefaultViewstack {

	final String topic = UIEvents.UILifeCycle.TOPIC + UIEvents.TOPIC_SEP + "*";

	protected static class PerspectiveEventHandler implements EventHandler
	  {
	    final String exp = "^.*/per[s]*.*$";
	    final Pattern pat = Pattern.compile(exp);
	    final Logger _log;
	    final String _className;
		final String _stackName;
	
	    public PerspectiveEventHandler(Logger log, String className, String stackName) {
	    	_log = log;
	    	_className = className;
	    	_stackName = stackName;
	    }
	    
	    @Override
	    public void handleEvent(Event event)
	    {
	      Matcher m = pat.matcher(event.getTopic());
	      if (m.matches()) {
	        boolean updated = setDefaultViewFolder(_stackName);
	
	        // we only need to register once
	        if (updated) {
	          boolean unreg = false;
	          try {
	            unreg = EventUtils.unregisterEventHandler(this);
	          }
	          catch (Exception noop) {
	            _log.warn("Failed to unregister partstack handler for " + _className, noop);
	          }
	          if (unreg) {
	            _log.debug("Unregistered partstack handler for " + _className);
	          }
	          else {
	            _log.warn("Failed to unregister partstack handler for " + _className);
	          }
	        } // if updated: the TAG was added to the partstack
	      } // matches
	    } // handle event
	
	
	
	
	    protected boolean setDefaultViewFolder(String folderID)
	    {
	      final String TAG = "org.eclipse.e4.secondaryDataStack";
	
	      boolean bRet = false;
	      IEclipseContext ctx = EventUtils.getContext();
	      if (ctx != null) {
	        // collect the service and application
	        EModelService modelService = ctx.get(EModelService.class);
	        MApplication app = ctx.get(MApplication.class);
	
	        // find the partstack
	        MUIElement partStack = modelService.find(folderID, app);
	
	        if (partStack != null && partStack.getTags() != null) {
	          synchronized (partStack) {
	            if (partStack.getTags().contains(TAG)) {
	              bRet = true;
	            }
	            else {
	              bRet = partStack.getTags().add(TAG);
	            }
	          } // Synchronized
	
	        } // if : we can test the tags
	
	      } // we have a context
	
	      return bRet;
	    }
	  }

	protected void registerForPerspectiveInitialization(EventHandler handler, Logger log) {
	
	    if (EventUtils.registerForEvent(topic, handler)) {
	      log.debug("Registered to update partstack for " + getClass().getSimpleName());
	    }
	  }
}
