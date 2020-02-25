/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
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

import com.strikewire.snl.apc.Common.MappingsUtil;
import com.strikewire.snl.apc.util.ExtensionPointUtils;

import gov.sandia.dart.aprepro.util.ApreproUtil;
import gov.sandia.dart.workflow.phase3.embedded.EmbeddedWorkflowPlugin;
import gov.sandia.dart.workflow.phase3.embedded.GraphicalWorkflowMonitor;
import gov.sandia.dart.workflow.phase3.embedded.preferences.EmbeddedExecutionEnvironmentVariables;
import gov.sandia.dart.workflow.phase3.embedded.preferences.IEmbeddedExecutionPreferenceConstants;
import gov.sandia.dart.workflow.runtime.core.LoggingWorkflowMonitor;
import gov.sandia.dart.workflow.runtime.core.ResponsesOutWriter;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.WorkflowProcess;

public class EmbeddedWorkflowJob extends Job {
	
	private static final String CONSOLE_NAME = "Workflow Output";

	private static final String ECLIPSE_PROJECT = "eclipse.project";

	private static final String ECLIPSE_WORKSPACE = "eclipse.workspace";

	private IFile file;
	private WorkflowProcess workflow;
	private String startNode = null;
	private File globalParametersFile = null;
	private List<String> breakPoints;
	private boolean runThroughBreakpoints = false;
	private IPath directory;

	
	public EmbeddedWorkflowJob(String name, IFile file, IPath directory) {
		super(name);
		this.file = file;
		this.directory = directory;
		setUser(false);
	}
		
	public EmbeddedWorkflowJob setGlobalParametersFile(File file) {
		globalParametersFile = file;
		return this;
	}
	
	public EmbeddedWorkflowJob setStartNode(String node) {
		startNode = node;
		return this;		
	}
	
	public EmbeddedWorkflowJob setBreakpoints(String... breakpoints) {
		breakPoints = Arrays.asList(breakpoints);
		return this;
	}
	
	public EmbeddedWorkflowJob setBreakpoints(Collection<String> breakpoints) {
		breakPoints = new ArrayList<>(breakpoints);
		return this;
	}
	
	public EmbeddedWorkflowJob setRunThroughBreakpoints(boolean runThroughBreakpoints) {
		this.runThroughBreakpoints = runThroughBreakpoints;
		return this;
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {		
		try {
			EmbeddedRunDatabase.INSTANCE.put(file, this);
			File iwfFile = file.getLocation().toFile();
			File homeDir = iwfFile.getParentFile();
			File workDir = directory.toFile();
			
			Map<String, String> eclipseProperties = new HashMap<>();
			eclipseProperties.put(ECLIPSE_PROJECT, file.getProject().getName());
			eclipseProperties.put(ECLIPSE_WORKSPACE, Platform.getLocation().toFile().getAbsolutePath());

			MessageConsole myConsole = findConsole(CONSOLE_NAME);
			if (EmbeddedWorkflowPlugin.getDefault().getPreferenceStore().getBoolean(IEmbeddedExecutionPreferenceConstants.CLEAR_CONSOLE))
				myConsole.clearConsole();
			
			OutputStream out = myConsole.newMessageStream();		    
		     
			File log = new File(workDir, FilenameUtils.getBaseName(iwfFile.getName()) + ".log");
			File status = new File(workDir, LoggingWorkflowMonitor.DEFAULT_NAME);

			FileOutputStream fos = new FileOutputStream(log);
			TeeOutputStream tos = new TeeOutputStream(out, fos);
			// TODO URL for workflow file here!
			Set<String> starts = (startNode == null) ? Collections.emptySet() : Collections.singleton(startNode);
			try (GraphicalWorkflowMonitor m1 = new GraphicalWorkflowMonitor(file, workDir, starts, monitor);
				 LoggingWorkflowMonitor m2 = new LoggingWorkflowMonitor(status);
				 PrintWriter err = new  PrintWriter(myConsole.newMessageStream(), true);
			     PrintWriter writer = new PrintWriter(tos, true)) {
				WorkflowProcess.setWorkflowVersion(getWorkflowVersion());
				workflow = new WorkflowProcess()
				.addMonitor(m1)
				.addMonitor(m2)
				.setWorkflowFile(iwfFile)
				.setHomeDir(homeDir)
				.setWorkDir(workDir)
				.setOut(writer)
				.setErr(err)
				.setStartNode(startNode)
				.setBreakpoints(breakPoints)
				.setRunThroughBreakpoints(runThroughBreakpoints)
				.setGlobalParameterFile(globalParametersFile)
				.preloadProperties(eclipseProperties);
				
				
				final IPreferenceStore preferenceStore = EmbeddedWorkflowPlugin.getDefault().getPreferenceStore();
				boolean validate = preferenceStore.getBoolean(IEmbeddedExecutionPreferenceConstants.VALIDATE_UNDEFINED);
				workflow.setValidateUndefined(validate);
				
				Map<String, Class<? extends SAWCustomNode>> customNodes = getCustomNodes();
				for (Entry<String, Class<? extends SAWCustomNode>> entry: customNodes.entrySet()) {
					workflow.addCustomNode(entry.getKey(), entry.getValue());
				}

				workflow.addEnvVar("APREPRO_PATH", getApreproPath());
				EmbeddedExecutionEnvironmentVariables.getInstance().
					getAllProperties(false).forEach(p-> workflow.addEnvVarWithSystemSubs(p.getName(), p.getValue()));

				workflow.run();
				writer.flush();
				Map<String, Object> responses = workflow.getRuntime().getResponses();
				PrintWriter console = new PrintWriter(out, true);
				if (responses.size() > 0)
					console.println("Responses:");
				
				for (String name: responses.keySet()) {
					console.println("  " + name + " = " + ResponsesOutWriter.format(responses.get(name)));
				}
				file.getParent().refreshLocal(IResource.DEPTH_INFINITE, monitor);
				return Status.OK_STATUS;
			}
		} catch (OperationCanceledException | InterruptedException e) {
			return new Status(Status.CANCEL, EmbeddedWorkflowPlugin.PLUGIN_ID, "The workflow has been cancelled.");
		} catch (Exception e) {			
			return new Status(Status.CANCEL, EmbeddedWorkflowPlugin.PLUGIN_ID, "Error while executing workflow", e);
		} finally {
			EmbeddedRunDatabase.INSTANCE.remove(file);
		}
	}
	
	private static volatile String workflowVersion;
	private static String getWorkflowVersion() {
		if (workflowVersion == null) {
			workflowVersion = "Embedded (unknown)";
			IProduct product = Platform.getProduct();
			String app = null;
			String ver = null;
			if (product != null) {
				app = product.getName();
				ver = product.getProperty("appVersion");
				if (StringUtils.isNotEmpty(app) && StringUtils.isNotEmpty(ver)) {
					Object[] mappings = MappingsUtil.getMappings(product.getDefiningBundle());
					ver = MessageFormat.format(ver, mappings);
					workflowVersion = app + " (" + ver + ")";
				}
			}
		}
		return workflowVersion;
	}

	private static IStatus createMultiStatus(Throwable t) {
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
		Thread runThread = getThread();
		if (runThread != null)
			runThread.interrupt();
	}
	
	private String getApreproPath()
	{
		return ApreproUtil.getApreproCommand();
	}
	
	private Map<String, Class<? extends SAWCustomNode>> getCustomNodes() {
		Map<String, Class<? extends SAWCustomNode>> results = new HashMap<>();
		List<IConfigurationElement> elements =
				ExtensionPointUtils.getConfigurationElements("gov.sandia.dart.workflow.phase3.embedded", "nodeDefinitionContributor", "nodeDefinition");
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

	  /**
	   * A job listner that posts appropriate dialogs if a job terminates abnormally.
	   * @author ejfried
	   *
	   */
	  
	  public static class DialogChangeListener extends JobChangeAdapter {
		  @Override
		  public void done(IJobChangeEvent event) {
			  IStatus status = event.getResult();
			  if (status != null) {
				  switch (status.getSeverity()) {
				  case IStatus.CANCEL:
				  case IStatus.ERROR:
					  if (status.getException() != null) {
						  Display.getDefault().asyncExec(() -> {
							  ErrorDialog dlg = new ErrorDialog(Display.getDefault().getActiveShell(),
									  "Workflow error",
									  "Error while executing workflow",						
									  createMultiStatus(status.getException()),
									  0xFFFF);
							  dlg.open();
						  });	  
					  } else {
						  Display.getDefault().asyncExec(() -> {							  
							  MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Workflow cancelled", "The workflow has been cancelled.");
						  });
  
					  }					  				
					  break;
				  }
			  }
		  }
	  }
}
