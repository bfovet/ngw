/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*
 * Created by mjgibso on Dec 17, 2009 at 8:12:23 PM
 */
package com.strikewire.snl.apc.status;

import java.util.ArrayList;
import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.progress.ProgressMessages;
import org.eclipse.ui.internal.statushandlers.IStatusDialogConstants;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.statushandlers.AbstractStatusAreaProvider;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.WorkbenchStatusDialogManager;

import com.ibm.icu.text.DateFormat;

/**
 * @author mjgibso
 *
 */
public class HTMLStatusAreaProvider extends AbstractStatusAreaProvider
{
	private WorkbenchStatusDialogManager workbenchStatusDialog;

	public HTMLStatusAreaProvider(WorkbenchStatusDialogManager wsd)
	{
		this.workbenchStatusDialog = wsd;
	}
	
	/*
	 * All statuses should be displayed.
	 */
	protected static final int MASK = IStatus.CANCEL | IStatus.ERROR | IStatus.INFO | IStatus.WARNING;

	/*
	 * New child entry in the list will be shifted by two spaces.
	 */
	private static final Object NESTING_INDENT = "  "; //$NON-NLS-1$
	
	private static final String LINE_SEPERATOR = System.getProperty("line.separator"); //$NON-NLS-1$

	/*
	 * Displays statuses.
	 */
	private Browser browser_;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.statushandlers.AbstractStatusAreaProvider#createSupportArea(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.statushandlers.StatusAdapter)
	 */
	public Control createSupportArea(Composite parent, StatusAdapter statusAdapter)
	{
		Composite area = createArea(parent);
		setStatusAdapter(statusAdapter);
		return area;
	}

	protected Composite createArea(Composite parent)
	{
		parent = new Composite(parent, SWT.NONE);
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		browser_ = new Browser(parent, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.widthHint = 250;
		gd.heightHint = 100;
		browser_.setLayoutData(gd);
		// There is no support for triggering commands in the dialogs. I am
		// trying to emulate the workbench behavior as exactly as possible.
		IBindingService binding = (IBindingService) PlatformUI.getWorkbench()
				.getService(IBindingService.class);
		//find bindings for copy action
		final TriggerSequence ts[] = binding
				.getActiveBindingsFor(ActionFactory.COPY.getCommandId());
		browser_.addKeyListener(new KeyListener() {
			
			ArrayList keyList = new ArrayList();

			public void keyPressed(KeyEvent e) {
				// get the character. reverse the ctrl modifier if necessary
				char character = e.character;
				boolean ctrlDown = (e.stateMask & SWT.CTRL) != 0;
				if (ctrlDown && e.character != e.keyCode && e.character < 0x20
						&& (e.keyCode & SWT.KEYCODE_BIT) == 0) {
					character += 0x40;
				}
				// do not process modifier keys
				if((e.keyCode & (~SWT.MODIFIER_MASK)) == 0){
					return;
				}
				// if there is a character, use it. if no character available,
				// try with key code
				KeyStroke ks = KeyStroke.getInstance(e.stateMask,
						character != 0 ? character : e.keyCode);
				keyList.add(ks);
				KeySequence sequence = KeySequence.getInstance(keyList);
				boolean partialMatch = false;
				for (int i = 0; i < ts.length; i++) {
					if (ts[i].equals(sequence)) {
						copyToClipboard();
						keyList.clear();
						break;
					}
					if (ts[i].startsWith(sequence, false)) {
						partialMatch = true;
					}
					for (int j = 0; j < ts[i].getTriggers().length; j++) {
						if (ts[i].getTriggers()[j].equals(ks)) {
							partialMatch = true;
						}
					}
				}
				if (!partialMatch) {
					keyList.clear();
				}
			}

			public void keyReleased(KeyEvent e) {
				//no op
			}
		});
		createDNDSource();
		createCopyAction(parent);
		Dialog.applyDialogFont(parent);
		return parent;
	}

	protected void setStatusAdapter(StatusAdapter adapter)
	{
		StringBuilder buffer = new StringBuilder();
		generateText(buffer, adapter.getStatus(), 0);
		if(workbenchStatusDialog.getStatusAdapters().size() == 1)
		{
			Long timestamp = (Long) adapter.getProperty(IStatusAdapterConstants.TIMESTAMP_PROPERTY);

			if(timestamp != null)
			{
				String date = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date(timestamp.longValue()));
				buffer.append(LINE_SEPERATOR);
				buffer.append(NLS.bind(ProgressMessages.JobInfo_Error,(new Object[] { "", date }))); //$NON-NLS-1$
			}
		}
		String contents = transformToHTML(buffer);
		browser_.setText(contents);
	}
	
	private String transformToHTML(StringBuilder buffer)
	{
		boolean html = false;
		if(buffer.toString().contains("<html"))
		{
			html = true;
		}
		
		if(!html)
		{
			buffer.insert(0, "<html>");
			buffer.append("</html>");
		}
		
		String s = buffer.toString();
		
		s = s.replaceAll("\n", "<br/>");
		
		return s;
	}
	
	/**
	 * Creates DND source for the list
	 */
	private void createDNDSource()
	{
		DragSource ds = new DragSource(browser_, DND.DROP_COPY);
		ds.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		ds.addDragListener(new DragSourceListener() {
			public void dragFinished(DragSourceEvent event) {
			}

			public void dragSetData(DragSourceEvent event) {
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					event.data = prepareCopyString();
				}
			}
			
			public void dragStart(DragSourceEvent event) {
//				list.selectAll();
			}
		});
	}

	private void createCopyAction(final Composite parent)
	{
		Menu menu = new Menu(parent.getShell(), SWT.POP_UP);
		MenuItem copyAction = new MenuItem(menu, SWT.PUSH);
		copyAction.setText(JFaceResources.getString("copy")); //$NON-NLS-1$
		copyAction.addSelectionListener(new SelectionAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				copyToClipboard();
				super.widgetSelected(e);
			}

		});
		browser_.setMenu(menu);
	}

	private String prepareCopyString()
	{
		if(browser_==null || browser_.isDisposed())
		{
			return ""; //$NON-NLS-1$
		}
		return browser_.getText();
	}

	private void generateText(StringBuilder buffer, IStatus status, int nesting)
	{
		if(!status.matches(MASK) && !(isDialogHandlingOKStatuses() && status.isOK()))
		{
			return;
		}
		
		if(buffer.length() > 0)
		{
			buffer.append(LINE_SEPERATOR);
		}
		
		for(int i=0; i<nesting; i++)
		{
			buffer.append(NESTING_INDENT);
		}
		
		buffer.append(status.getMessage());

		// Look for a nested core exception
		Throwable t = status.getException();
		if(t instanceof CoreException)
		{
			CoreException ce = (CoreException) t;
			generateText(buffer, ce.getStatus(), nesting + 1);
		} else if(t != null) {
			// Include low-level exception message
			buffer.append(LINE_SEPERATOR);
			for(int i=0; i<nesting; i++)
			{
				buffer.append(NESTING_INDENT);
			}
			
			String message = t.getLocalizedMessage();
			if(message == null)
			{
				message = t.toString();
			}
			buffer.append(message);
		}

		IStatus[] children = status.getChildren();
		for(IStatus child : children)
		{
			generateText(buffer, child, nesting + 1);
		}
	}

	/**
	 * @return Returns the browser.
	 */
	public Browser getBrowser()
	{ return browser_; }
	
	private void copyToClipboard()
	{
		Clipboard clipboard = null;
		try {
			clipboard = new Clipboard(browser_.getDisplay());
			clipboard.setContents(new Object[] { prepareCopyString() },
					new Transfer[] { TextTransfer.getInstance() });
		} finally {
			if (clipboard != null) {
				clipboard.dispose();
			}
		}
	}

	private boolean isDialogHandlingOKStatuses()
	{ return ((Boolean) workbenchStatusDialog.getProperty(IStatusDialogConstants.HANDLE_OK_STATUSES)).booleanValue(); }
}
