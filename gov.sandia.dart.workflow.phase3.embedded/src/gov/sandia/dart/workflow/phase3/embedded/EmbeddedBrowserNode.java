/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded;

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;


/**
 * Not sure quite how to factor this out, so instead I'm overloading this one browser
 * node type with a few different functions, each activated depending on if/which values
 * are assigned to various properties.
 * 
 * If the "instantiate workflow" property is true:
 * 		a new BrowserView is created and associated with the specified browser ID,
 * otherwise:
 * 		the browser for this node becomes the one specified via the "browser ID" property
 * 
 * If a value is specified for the URL property:
 * 		the requested URL is loaded in the browser
 * 
 * If a value is specified for the "wait for event" property:
 * 		the current thread waits for the named event to be signaled from Javascript.
 * 
 * If a value is specified for the "expression" property OR input port:
 * 		the specified expression is evaluated in the browser and the
 * 		result placed on the "f" output port
 * otherwise:
 * 		an empty string is placed on the "f" output port
 * 
 * @author mrglick
 *
 */
public class EmbeddedBrowserNode extends SAWCustomNode {
	
	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties,
			WorkflowDefinition workflow, RuntimeData runtime) {
		BrowserView[] browserView = new BrowserView[1];
		Object[] jsValue = new Object[1];

		final String browserName = properties.getOrDefault("browser ID", "Workflow Browser Component");
		if (browserName == null || browserName.isEmpty()) {
			throw new SAWWorkflowException(getName() + ": no browser name");
		}
		
		final Boolean instantiateBrowser = Boolean.valueOf(properties.get("instantiate browser"));
		final String title = properties.get("title");		
		final String urlText = properties.get("URL");
		final String waitForEvent = getStringFromPortOrProperty(runtime, properties, "wait for event");
		
		WorkflowDefinition.Node node = workflow.getNode(getName());		
		String expressionText;
		WorkflowDefinition.Property expressionProperty = node.properties.get("expression");
		File componentWorkDir = getComponentWorkDir(runtime, properties);
		if (expressionProperty == null) {
			WorkflowDefinition.InputPort expressionPort = node.inputs.values().stream()
					.filter(p -> "expression".equals(p.name)).findFirst().orElse(null);
			if (expressionPort == null)
				throw new SAWWorkflowException("No expression or property or port!");
			if ("input_file".equals(expressionPort.type)) {
				String fileName = (String) runtime.getInput(getName(), expressionPort.name, String.class);
				File f = new File(fileName);
				if (!f.isAbsolute())
					f = new File(componentWorkDir, fileName);
				try {
					expressionText = new String(FileUtils.readFileToByteArray(f));
				} catch (IOException e) {
					throw new SAWWorkflowException("Exception reading from file on expression input port.", e);
				}
			} else
				expressionText = (String) runtime.getInput(getName(), expressionPort.name, String.class);
		} else if (isHomeFile(expressionProperty)) {
			try {
				String fileName = expressionProperty.value;
				File f = new File(fileName);
				if (!f.isAbsolute())
					f = new File(runtime.getHomeDir(), fileName);
				expressionText = new String(FileUtils.readFileToByteArray(f));
			} catch (Exception e) {
				throw new SAWWorkflowException("Exception reading from file specified via expression property.", e);
			}
		} else {
			expressionText = properties.get("expression");
			runtime.log().debug("will evaluate expression " + expressionText);
		}

		if (instantiateBrowser) {
			// not yet clear on if/how you can instantiate multiple browsers
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					try {
						runtime.log().debug("about to try to get page");
						IWorkbenchPage page = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage();
						runtime.log().debug("about to try to make browser view");
						browserView[0] = (BrowserView) page.showView(BrowserView.ID, null, IWorkbenchPage.VIEW_VISIBLE);
					} catch (PartInitException e) {
						runtime.log().error(String.format("%s: couldn't create browser view", getName()), e);
					}
				}				
			});
			if (browserView[0] == null) { 
				throw new SAWWorkflowException(this.getName() + ": Browser view creation failed");
			}
			browserView[0].registerBrowserName(browserName);

			if (title != null) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						browserView[0].setTitle(title); // DOESN'T DO ANYTHING YET
					}				
				});
			} 

		} else {
			browserView[0] = BrowserView.browserMap.get(browserName);
			if (browserView[0] == null) {
				throw new SAWWorkflowException(this.getName() + ": Browser not open");
			} else {
				runtime.log().debug(this.getName() + ": GOT BROWSER");
			}
		}
		
		Object lockUntilLoaded = new Object();
		if (urlText != null && !urlText.isEmpty()) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					browserView[0].setUrl(urlText);
				}				
			});
			
			if (browserView[0].addLoadWaiter(lockUntilLoaded)) {
				synchronized(lockUntilLoaded) {
					try {
						runtime.log().debug("browserNode load wait on object " + lockUntilLoaded.toString());
						while (!runtime.isCancelled() && !browserView[0].isLoaded()) {
							lockUntilLoaded.wait(300); 
						}
						runtime.log().debug("browserNode returning from load wait");
					} catch (InterruptedException e) {
						throw new SAWWorkflowException(this.getName() + ": Browser load interrupted");
					}					
				}	
			}
		}

		if (!StringUtils.isEmpty(waitForEvent)) {
			Object eventLock = new Object();
			boolean[] complete = new boolean[1];
			Runnable listener = new Runnable() {
				@Override
				public void run() {
					synchronized(eventLock) {
						complete[0] = true;
						eventLock.notify();
					}
				};
			};
			
			browserView[0].eventHandler.registerEventLock(waitForEvent, listener);
			synchronized(eventLock) {
				try {
					runtime.log().debug("browserNode going into event wait on object " + eventLock.toString());
					while (!runtime.isCancelled() && !complete[0]) {
						eventLock.wait(300);
					}
					runtime.log().debug("browserNode returning from wait on event");
				} catch (InterruptedException e) {
					throw new SAWWorkflowException(this.getName() + ": Browser wait interrupted");
				}
			}
		}
		
		if (expressionText != null && !expressionText.isEmpty()) {
			runtime.log().debug("about to evaluate expression {0}", expressionText);
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					jsValue[0] = browserView[0].evaluate(expressionText);
					runtime.log().debug("expression returned " + jsValue[0]);
				}				
			});
			return Collections.singletonMap("f", jsValue[0]);
		} else
			return Collections.singletonMap("f", "");
	}
	
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo("instantiate browser"), new PropertyInfo("browser ID"), new PropertyInfo("URL"), new PropertyInfo("wait for event"), new PropertyInfo("expression")); }
	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo("x")); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo("f")); }
	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.UI); }

}
