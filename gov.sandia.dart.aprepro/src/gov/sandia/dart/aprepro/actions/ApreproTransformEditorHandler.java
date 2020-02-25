/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.aprepro.actions;

import gov.sandia.dart.aprepro.ApreproPlugin;
import gov.sandia.dart.aprepro.actions.ApreproTransformHandler.DisplayOption;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.strikewire.snl.apc.osgi.util.EventKeys;
import com.strikewire.snl.apc.osgi.util.OSGIUtils;

public class ApreproTransformEditorHandler extends AbstractHandler {
	
	/**
	 * Called when a user invokes the "Run Aprepro Translation" feature. It calls
	 * ApreproTransformHandler.execute() after setting the display option.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException 
	{
		try {
			AbstractHandler abstractTransformHandler = new ApreproTransformHandler(DisplayOption.InEditor);
			abstractTransformHandler.execute(event);
			OSGIUtils.postEvent(EventKeys.METRICS, ApreproPlugin.class,
					"plugin", ApreproPlugin.PLUGIN_ID, 
					"capability", "aprepro_translate");
		} catch (ExecutionException e) {
			ApreproPlugin.getDefault().logError("Problem in aprepro transform", e);
		}
		
		return null;
	}  //  execute()
}




