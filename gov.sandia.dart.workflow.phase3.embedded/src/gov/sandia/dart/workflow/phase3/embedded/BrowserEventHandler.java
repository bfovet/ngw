/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * This class provides the capability for a node to wait for some specifically named "event"
 * to occur in the browser.
 * 
 * An "event", in this case, just corresponds to the a call being made to a Javascript function
 * (named via the constructor for this object) with the event's name as the argument.
 * 
 * @author mrglick
 *
 */
public class BrowserEventHandler extends BrowserFunction {
	private Map<String, List<Runnable>> eventLocks = new ConcurrentHashMap<>();

	public BrowserEventHandler(Browser browser, String name) {
		super(browser, name);
	}
	
	public void registerEventLock(String eventName, Runnable lockObject) {
		System.err.println("BrowserEventHandler registering event " + eventName);
		if (!eventLocks.containsKey(eventName)) {
			eventLocks.put(eventName, new ArrayList<>());
		}
		List<Runnable> lockList = eventLocks.get(eventName);
		lockList.add(lockObject);
	}
	
	@Override
	public Object function(Object[] arguments) {
		if (arguments.length < 1) {
			System.err.println("BrowserEventHandler called with no arguments!");
			return null;
		}
		
		String eventName = (String) arguments[0];
		System.err.println("BrowserEventHandler got event " + eventName);
		
		List<Runnable> lockList = eventLocks.get(eventName);
		if (lockList != null) {
			for (Runnable lockObject : lockList) {
				System.err.println("BrowserEventHandler sending notification to object " + lockObject.toString());
				synchronized(lockObject) {
					lockObject.run();
				}
			}
			lockList.clear();
		} else
			System.err.println("BrowserEventHandler got unregistered event " + eventName);
		
		return null;
	}


}
