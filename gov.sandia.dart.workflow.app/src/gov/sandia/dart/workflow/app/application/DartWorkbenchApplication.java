package gov.sandia.dart.workflow.app.application;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.internal.ide.application.DelayedEventsProcessor;

/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/


import gov.sandia.dart.application.AbstractDARTApplication;
import gov.sandia.dart.workflow.app.ApcWorkbench.ApcWorkbenchOptions;
import gov.sandia.dart.workflow.app.ApcWorkbench.WorkflowApplicationPlugin;

/**
 * The "main program" for the Eclipse IDE.
 * 
 * @since 3.0
 */
@SuppressWarnings("restriction")
public class DartWorkbenchApplication extends AbstractDARTApplication implements IApplication, IExecutableExtension
{
    /**
     * Creates a new IDE application.
     */
    public DartWorkbenchApplication() {
        // There is nothing to do for IDEApplication
    }
    
    protected WorkbenchAdvisor createWorkbenchAdvisor(DelayedEventsProcessor processor)
    {
    	return new DartWorkbenchAdvisor(getNewWorkspace());
    }

    @Override
    protected Object doStart(IApplicationContext context) throws Exception
    {
    	parseCommandLineArguments();
    	
    	// what's the difference between getting app args from platform vs from
    	// provided context argument?
		String[] args = Platform.getApplicationArgs();

		// TODO Migrate to new CommandLineParser
		// if no gui option is set, execute headless workbench
		ApcWorkbenchOptions options = null;
		try {
			options = new ApcWorkbenchOptions(Arrays.copyOf(args,  args.length));
		} catch(Exception e) {
			// log it or something?
		}
		if(options!=null && options.getNoGui())
		{
			executeHeadlessWorkbench(options);
			
			return IApplication.EXIT_OK;
		}
		
		return startHeadedClient(context);
    }

    private void executeHeadlessWorkbench(ApcWorkbenchOptions options)
    {
    	System.out.println("Executing headless workbench...");
    	String script = options.getPythonScript();
    	if(script != null) {
    		IWorkspace workspace = ResourcesPlugin.getWorkspace();
    		IWorkspaceRoot root = workspace.getRoot();
    		IPath location = root.getLocation();
    		System.out.println("Using workspace: " + location.toOSString());
    		String projectName = options.getProject();
    		boolean usingTempProject = false;
    		if(projectName == null) {
    			usingTempProject = true;
    			projectName = "TempProject" + System.currentTimeMillis();
    			System.out.println("No project specified, using temp project " + projectName);
    		}

    		IProject project = root.getProject(projectName);
    		if(!project.exists()) {
    			try {
    				System.out.println("Project " + projectName + " does not exist. Creating project...");
    				project.create(null);
    			} catch (CoreException e) {
    				System.err.println("Error creating new project " + projectName);
    				e.printStackTrace();
    				WorkflowApplicationPlugin.getDefault().logError("Error creating " +
    						"new project " + projectName, e);
    				return;
    			}
    		}

    		if(usingTempProject) {
    			try {
    				System.out.println("Deleting temp project " + projectName);
					project.delete(true, true, null);
				} catch (CoreException e) {
					System.err.println("Error deleting temp project " + projectName);
					e.printStackTrace();
					WorkflowApplicationPlugin.getDefault().logError("Error deleting temp " +
    						"project " + projectName, e);
    				return;
				}
    		}
    	}
    }
	
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
     */
    public void setInitializationData(IConfigurationElement config,
            String propertyName, Object data) {
        // There is nothing to do for IDEApplication
    }
}
