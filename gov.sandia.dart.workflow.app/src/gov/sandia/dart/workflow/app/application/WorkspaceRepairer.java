/*
 * Created by mjgibso on May 26, 2010 at 8:42:10 AM
 */
package gov.sandia.dart.workflow.app.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

import gov.sandia.dart.workflow.app.ApcWorkbench.WorkflowApplicationPlugin;

/**
 * @author mjgibso
 *
 */
public class WorkspaceRepairer
{
	private static final String PROPS_FILE_NAME = "workspaceRepair.properties";
	
	private static final String DELETE_FILE_PREFIX = "delete.file.";
	
	public static void repairWorkspace()
	{
		// TODO add logging
		
		Properties repairProps;
		try {
			repairProps = getRepairProperties();
		} catch (IOException e) {
			WorkflowApplicationPlugin.getDefault().logError("Error running workspace " +
					"repair.  Error loading repair properties: "+e.getMessage(), e);
			return;
		}
		
		List<File> deleteFiles = getDeleteFiles(repairProps);
		
		deleteFiles(deleteFiles);
	}
	
	private static void deleteFiles(List<File> files)
	{
		for(File file : files)
		{
			StringBuilder log = new StringBuilder();
			Throwable t = null;
			try {
				log.append("Repair Utility:\n  Looking for file: ");
				log.append(file.getAbsolutePath());
				
				log.append("\n    exists: ");
				boolean exists = file.exists();
				log.append(exists);
				if(!exists)
				{
					continue;
				}
				
				log.append("\n    isFile: ");
				boolean isFile = file.isFile();
				log.append(isFile);
				if(!isFile)
				{
					continue;
				}
				
				log.append("\n    Scheduling delete.");
				file.deleteOnExit();
//				boolean success;
//				try {
//					success = file.delete();
//				} catch (Throwable thrown) {
//					success = false;
//					t = thrown;
//				}
//				log.append("\n    Success: ");
//				log.append(success);
			} finally {
				logInfo(log.toString(), t);
			}
		}
	}
	
	private static void logInfo(String msg, Throwable t)
	{
		IStatus infoStatus = new Status(IStatus.INFO, WorkflowApplicationPlugin.ID, msg, t);
		WorkflowApplicationPlugin.getDefault().log(infoStatus);
	}
	
	private static List<File> getDeleteFiles(Properties repairProps)
	{
		IPath workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		
		List<File> files = new ArrayList<File>();
		
		for(Entry<Object, Object> entry : repairProps.entrySet())
		{
			Object keyObj = entry.getKey();
			if(keyObj==null || !(keyObj instanceof String))
			{
				continue;
			}
			
			String key = (String) keyObj;
			if(!key.startsWith(DELETE_FILE_PREFIX))
			{
				continue;
			}
			
			Object valObj = entry.getValue();
			if(valObj==null || !(valObj instanceof String))
			{
				continue;
			}
			
			String filePathStr = (String) valObj;
			IPath filePath = workspacePath.append(filePathStr);
			files.add(filePath.toFile());
		}
		
		return files;
	}
	
	private static Properties getRepairProperties() throws IOException
	{
		Bundle bundle = WorkflowApplicationPlugin.getDefault().getBundle();
		
		URL repairPropsFileURL = FileLocator.find(bundle, new Path(PROPS_FILE_NAME), null);
		repairPropsFileURL = FileLocator.toFileURL(repairPropsFileURL);
		
		File repairPropsFile = new File(repairPropsFileURL.getPath());
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(repairPropsFile);
			Properties props = new Properties();
			props.load(fis);
			return props;
		} finally {
			if(fis != null)
			{
				fis.close();
			}
		}
	}
}