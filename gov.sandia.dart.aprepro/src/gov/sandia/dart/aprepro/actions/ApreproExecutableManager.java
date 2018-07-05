/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
///*******************************************************************************
// * Sandia Analysis Workbench Integration Framework (SAW)
// * Copyright 2016 Sandia Corporation. Under the terms of Contract
// * DE-AC04-94AL85000 with Sandia Corporation, the U.S. Government
// * retains certain rights in this software.
// * 
// * This software is distributed under the Eclipse Public License.
// * For more information see the files copyright.txt and license.txt
// * included with the software.
// ******************************************************************************/
//package gov.sandia.dart.aprepro.actions;
//
//import java.net.URL;
//
//import org.eclipse.core.filesystem.EFS;
//import org.eclipse.core.filesystem.IFileStore;
//import org.eclipse.core.filesystem.IFileSystem;
//import org.eclipse.core.resources.IProject;
//import org.eclipse.core.runtime.FileLocator;
//import org.eclipse.core.runtime.IPath;
//import org.eclipse.core.runtime.Path;
//import org.eclipse.core.runtime.Platform;
//import org.eclipse.jface.dialogs.MessageDialog;
//import org.osgi.framework.Bundle;
//
//import com.strikewire.snl.apc.util.ResourceUtils;
//
//import gov.sandia.dart.aprepro.ApreproPlugin;
//
//public class ApreproExecutableManager {
//	
//	private static final String COMMAND = "aprepro";
//	private static ApreproExecutableManager INSTANCE = new ApreproExecutableManager();
//
//	public static ApreproExecutableManager getInstance() {
//		return INSTANCE;
//	}
//	
//	public synchronized String getInternalCommand() {
//		try {
//			IProject project = ResourceUtils.getHiddenProject();
//			IPath loc = project.getLocation().append(getPlatformSpecificExecutableName());
//			if (loc.toFile().exists()) // if file exists in the hidden
//									   // project, return it
//				return loc.toOSString();
//	
//			IPath path = new Path("tools");
//			IPath locpath = path.append(Platform.getOS());
//			Bundle bundle = Platform.getBundle(ApreproPlugin.PLUGIN_ID);
//			URL url = FileLocator.find(bundle, locpath, null);
//	
//			if (url != null)
//				url = FileLocator.resolve(url);
//	
//			IFileSystem fileSystem = EFS.getLocalFileSystem();			
//			IFileStore sourceDir = fileSystem.getStore(new Path(url.getPath()).append(getPlatformSpecificExecutableName()));
//			IFileStore destDir = fileSystem.getStore(project.getLocation().append(getPlatformSpecificExecutableName()));
//			sourceDir.copy(destDir, EFS.OVERWRITE, null);
//	
//			// make sure it is executable
//			if(!Platform.getOS().toLowerCase().startsWith("win")) {
//				String[] command2 = new String[] {"chmod", "755", loc.toOSString()};
//				try {
//					Process chmodProc = Runtime.getRuntime().exec(command2);
//					chmodProc.waitFor();
//				} catch (Exception e) {
//					ApreproPlugin.getDefault().logError("Executing " + COMMAND, e);				
//				}
//			}
//	
//			return loc.toOSString();
//	
//		} catch (Exception e) {
//			ApreproPlugin.getDefault().logError("Locating " + COMMAND, e);
//			displayProcessError();
//		}  		
//				
//		return null;
//	}  //  getSierraApreproCommand()
//
//	public String getPlatformSpecificExecutableName()
//	{
//		return Platform.getOS().startsWith("win") ? COMMAND + ".exe" : COMMAND;
//	}
//
//	public void displayProcessError()
//	{
//		MessageDialog.openError(null, "Error", "Unsuccessful executing " + COMMAND.toUpperCase() + " command." +
//			"Try setting the path to a valid executable in the " + COMMAND.toUpperCase() + " preference page and " +
//			"try running the action again.");
//		
//	}  //  displayProcessError()
//
//}
