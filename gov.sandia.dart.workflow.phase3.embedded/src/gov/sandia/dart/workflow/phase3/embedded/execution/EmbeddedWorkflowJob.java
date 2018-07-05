/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.execution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;

import com.strikewire.snl.apc.util.ExtensionPointUtils;

import gov.sandia.dart.aprepro.util.ApreproUtil;
import gov.sandia.dart.workflow.phase3.embedded.EmbeddedWorkflowPlugin;
import gov.sandia.dart.workflow.phase3.embedded.GraphicalWorkflowMonitor;
import gov.sandia.dart.workflow.phase3.embedded.preferences.EmbeddedExecutionEnvironmentVariables;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.WorkflowProcess;

public class EmbeddedWorkflowJob extends Job {
	
    private static final String CONSOLE_NAME = "Workflow Output";

	private IFile file;
	private WorkflowProcess workflow;
	private String startNode = null;

	private IPath directory;
	
	public EmbeddedWorkflowJob(String name, IFile file, IPath directory) {
		super(name);
		this.file = file;
		this.directory = directory;
	}
	
	public EmbeddedWorkflowJob(String name, IFile file, IPath directory, String startNode) {
		this(name, file, directory);
		this.startNode = startNode;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			EmbeddedRunDatabase.INSTANCE.put(file, this);
			File iwfFile = file.getLocation().toFile();
			File homeDir = iwfFile.getParentFile();
			File workDir = directory.toFile();

			 MessageConsole myConsole = findConsole(CONSOLE_NAME);
		     OutputStream out = myConsole.newMessageStream();

			File log = new File(workDir, FilenameUtils.getBaseName(iwfFile.getName()) + ".log");
			FileOutputStream fos = new FileOutputStream(log);
			TeeOutputStream tos = new TeeOutputStream(out, fos);
			try (GraphicalWorkflowMonitor m = new GraphicalWorkflowMonitor(file.getName(), monitor);
				 PrintWriter err = new  PrintWriter(myConsole.newMessageStream(), true);
			     PrintWriter writer = new PrintWriter(tos, true)) {
				workflow = new WorkflowProcess().setMonitor(m) 
				.setWorkflowFile(iwfFile)
				.setHomeDir(homeDir)
				.setWorkDir(workDir)
				.setOut(writer)
				.setErr(err)
				.setStartNode(startNode);
				Map<String, Class<? extends SAWCustomNode>> customNodes = getCustomNodes();
				for (Entry<String, Class<? extends SAWCustomNode>> entry: customNodes.entrySet()) {
					workflow.addCustomNode(entry.getKey(), entry.getValue());
				}
				workflow.addEnvVar("APREPRO_PATH", getApreproPath());
				EmbeddedExecutionEnvironmentVariables.getInstance().
					getAllProperties(false).forEach(p-> workflow.addEnvVarWithSystemSubs(p.getName(), p.getValue()));
				workflow.run();
				file.getParent().refreshLocal(IResource.DEPTH_INFINITE, monitor);
				return Status.OK_STATUS;
			}
		} catch (OperationCanceledException e) {
			Display.getDefault().asyncExec(() -> MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Workflow cancelled", "The workflow has been cancelled."));
			return Status.CANCEL_STATUS;
		} catch (Exception e) {
			// return 
			Display.getDefault().asyncExec(() -> {
				ErrorDialog dlg = new ErrorDialog(Display.getDefault().getActiveShell(),
						"Workflow error",
						"Error while executing workflow",						
						createMultiStatus(e),
						0xFFFF);
				dlg.open();
			});						
			return Status.CANCEL_STATUS;
		} finally {
			EmbeddedRunDatabase.INSTANCE.remove(file);
		}
	}
	
	private IStatus createMultiStatus(Throwable t) {
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
	    t.printStackTrace(pw);

	    final String trace = sw.toString(); // stack trace as a string

	    // Temp holder of child statuses
	    List<Status> childStatuses = new ArrayList<>();

	    // Split output by OS-independent new-line
	    for (String line : trace.split(System.getProperty("line.separator"))) {
	        // build & add status
	        childStatuses.add(new Status(IStatus.ERROR, EmbeddedWorkflowPlugin.PLUGIN_ID, line));
	    }

	   return new MultiStatus(EmbeddedWorkflowPlugin.PLUGIN_ID, IStatus.ERROR,
	            childStatuses.toArray(new Status[] {}), // convert to array of statuses
	            t.getLocalizedMessage(), t);
	}

	@Override
	protected void canceling() {
		workflow.cancel();
	}
	
	private String getApreproPath()
	{
		return ApreproUtil.getApreproCommand();
	}
	
	private Map<String, Class<? extends SAWCustomNode>> getCustomNodes() {
		Map<String, Class<? extends SAWCustomNode>> results = new HashMap<>();
		List<IConfigurationElement> elements = ExtensionPointUtils.getConfigurationElements("gov.sandia.dart.workflow.phase3.embedded", "nodeDefinitionContributor");
		for (IConfigurationElement element: elements) {
			String name = element.getAttribute("name");
			String clazz = element.getAttribute("nodeClass");
			try {
				SAWCustomNode node = (SAWCustomNode) element.createExecutableExtension("nodeClass");
				results.put (name, node.getClass());
			} catch (CoreException e) {
				EmbeddedWorkflowPlugin.getDefault().logError("Couldn't load class " + clazz, e);			
			}			
		}
		return results;
	}
	
	  private MessageConsole findConsole(String name)
	  {
	    ConsolePlugin plugin = ConsolePlugin.getDefault();
	    IConsoleManager conMan = plugin.getConsoleManager();
	    IConsole[] existing = conMan.getConsoles();
	    for (int i = 0; i < existing.length; i++)
	      if (name.equals(existing[i].getName())) {
	    	  MessageConsole myConsole = (MessageConsole) existing[i];
	    	  conMan.showConsoleView(myConsole);
	    	  return myConsole;
	      }
	    // no console found, so create a new one
	    MessageConsole myConsole = new MessageConsole(name, null);
	    conMan.addConsoles(new IConsole[] { myConsole });
	    conMan.showConsoleView(myConsole);
	    IConsoleView view = getConsoleView();
	    if (view != null) {
	      view.display(myConsole);
	    }
	    return myConsole;
	  }

	  private IConsoleView getConsoleView()
	  {
	    if (!PlatformUI.isWorkbenchRunning()) {
	      return null;
	    }

	    IWorkbench wb = PlatformUI.getWorkbench();
	    if (wb != null) {
	      IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
	      if (win != null) {
	        IWorkbenchPage page = win.getActivePage();
	        try {
	          return (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW, null, IWorkbenchPage.VIEW_VISIBLE);
	        }
	        catch (PartInitException e) {
	          // We tried
	        }
	      }
	    }
	    return null;
	  }


}
