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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class BrowserView extends ViewPart {
	public static final String ID = "gov.sandia.dart.workflow.phase3.embedded.BrowserView"; //$NON-NLS-1$
    public static Map<String, BrowserView> browserMap = new ConcurrentHashMap<>();

    private Composite parent = null;
    private volatile boolean loaded = false;
	private Browser browser;
	public BrowserEventHandler eventHandler;
        
    public BrowserView() {
            super();
    }

	public Browser getBrowser() {
		return browser;
	}
	
	public void registerBrowserName(String name) {
		browserMap.put(name, this);
	}
	
	public Object evaluate(String text) {
		return browser.evaluate(text);
	}
	
	public boolean setUrl(String url) {
		loaded = false;
		return browser.setUrl(url);
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	// none of this seems to do anything yet
	@Override
	public void setTitle(String title) {
		if (parent == null)
			return;
		
		Label helpLabel = new Label(parent, SWT.NONE);
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(helpLabel.getFont()).setStyle(SWT.BOLD).setHeight(12);
		Font boldFont = boldDescriptor.createFont(helpLabel.getDisplay());
		helpLabel.setFont( boldFont );
		helpLabel.setText(title);
		
		Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(separator);
	}
	
	private List<Object> loadWaiters = Collections.synchronizedList(new ArrayList<>());
	
	public synchronized boolean addLoadWaiter(Object listener) {
		if (!loaded) {
			loadWaiters.add(listener);
			return true;
		} else
			return false;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
        
		ScrolledComposite browserContainer = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.RESIZE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(browserContainer);
		browserContainer.setMinSize(275, 502);
		browserContainer.setExpandHorizontal(true);
		browserContainer.setExpandVertical(true);
		
		try {
			browser = new Browser(browserContainer, SWT.NONE);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(browser);
			
			browserContainer.setContent(browser);
			browser.addProgressListener(new ProgressAdapter() {
				@Override
				public void completed(ProgressEvent event) {
					loaded = true;
					for (Object waiter : loadWaiters)
						synchronized (waiter) {
							// EJFH SpotBugs warning is not a problem here
							waiter.notifyAll();
						}
					loadWaiters.clear();
				}
			});
			eventHandler = new BrowserEventHandler(browser, "eventHandler");
			
		} catch (Throwable t) {
			System.err.println("Can't initialize browser");
		}
	}

	@Override
	public void setFocus() {
	}

}
