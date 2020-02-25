package gov.sandia.dart.workflow.app.application;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.model.application.ui.advanced.MAdvancedFactory;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.e4.compatibility.ModeledPageLayout;
import org.eclipse.ui.internal.ide.AboutInfo;
import org.eclipse.ui.internal.ide.EditorAreaDropAdapter;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.dialogs.WelcomeEditorInput;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.eclipse.ui.part.EditorInputTransfer;
import org.eclipse.ui.part.MarkerTransfer;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.update.configurator.ConfiguratorUtils;
import org.eclipse.update.configurator.IPlatformConfiguration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
import gov.sandia.dart.application.AbstractDARTWorkbenchWindowAdvisor;
import gov.sandia.dart.workflow.app.ApcWorkbench.WorkflowApplicationPlugin;

/**
 * Window-level advisor for the IDE.
 */
@SuppressWarnings("restriction")
public class DartWorkbenchWindowAdvisor extends AbstractDARTWorkbenchWindowAdvisor
{
	private static final String WELCOME_EDITOR_ID = "org.eclipse.ui.internal.ide.dialogs.WelcomeEditor"; //$NON-NLS-1$

	private DartWorkbenchAdvisor wbAdvisor;
	private boolean editorsAndIntrosOpened = false;
	private IEditorPart lastActiveEditor = null;
	private IPerspectiveDescriptor lastPerspective = null;

	private IWorkbenchPage lastActivePage;
	private String lastEditorTitle = ""; //$NON-NLS-1$
	
	private IPropertyListener editorPropertyListener = new IPropertyListener() {
		@Override
		public void propertyChanged(Object source, int propId) {
			if (propId == IWorkbenchPartConstants.PROP_TITLE) {
				if (lastActiveEditor != null) {
					String newTitle = lastActiveEditor.getTitle();
					if (!lastEditorTitle.equals(newTitle)) {
						recomputeTitle();
					}
				}
			}
		}
	};

	private IAdaptable lastInput;

	private IWorkbenchAction openPerspectiveAction;

	/**
	 * Crates a new IDE workbench window advisor.
	 * 
	 * @param wbAdvisor
	 *            the workbench advisor
	 * @param configurer
	 *            the window configurer
	 */
	public DartWorkbenchWindowAdvisor(DartWorkbenchAdvisor wbAdvisor,
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
		this.wbAdvisor = wbAdvisor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#createActionBarAdvisor(org.eclipse.ui.application.IActionBarConfigurer)
	 */
	@Override
	public ActionBarAdvisor doCreateActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new DartWorkbenchActionBuilder(configurer);
	}

	/**
	 * Returns the workbench.
	 * 
	 * @return the workbench
	 */
	private IWorkbench getWorkbench() {
		return getWindowConfigurer().getWorkbenchConfigurer().getWorkbench();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#preWindowShellClose
	 */
	@Override
	public boolean doPreWindowShellClose() {
		if (getWorkbench().getWorkbenchWindowCount() > 1) {
			return true;
		}
		// the user has asked to close the last window, while will cause the
		// workbench to close in due course - prompt the user for confirmation
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault()
				.getPreferenceStore();
		boolean promptOnExit = store
				.getBoolean(IDEInternalPreferences.EXIT_PROMPT_ON_CLOSE_LAST_WINDOW);

		if (promptOnExit) {
			String message;

			String productName = null;
			IProduct product = Platform.getProduct();
			if (product != null) {
				productName = product.getName();
			}
			if (productName == null) {
				message = IDEWorkbenchMessages.PromptOnExitDialog_message0;
			} else {
				message = NLS.bind(
						IDEWorkbenchMessages.PromptOnExitDialog_message1,
						productName);
			}

			MessageDialogWithToggle dlg = MessageDialogWithToggle
					.openOkCancelConfirm(getWindowConfigurer().getWindow()
							.getShell(),
							IDEWorkbenchMessages.PromptOnExitDialog_shellTitle,
							message,
							IDEWorkbenchMessages.PromptOnExitDialog_choice,
							false, null, null);
			if (dlg.getReturnCode() != IDialogConstants.OK_ID) {
				return false;
			}
			if (dlg.getToggleState()) {
				store
						.setValue(
								IDEInternalPreferences.EXIT_PROMPT_ON_CLOSE_LAST_WINDOW,
								false);
				IDEWorkbenchPlugin.getDefault().savePluginPreferences();
			}
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#preWindowOpen
	 */
	@Override
	public void doPreWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

		// show the shortcut bar and progress indicator, which are hidden by
		// default
		configurer.setShowPerspectiveBar(true);
		configurer.setShowFastViewBars(true);
		configurer.setShowProgressIndicator(true);
		// up the default width a little to accommodate our classification status bar
		configurer.setInitialSize(new Point(1280, 1024));

		// add the drag and drop support for the editor area
		configurer.addEditorAreaTransfer(EditorInputTransfer.getInstance());
		configurer.addEditorAreaTransfer(ResourceTransfer.getInstance());
		configurer.addEditorAreaTransfer(FileTransfer.getInstance());
		configurer.addEditorAreaTransfer(MarkerTransfer.getInstance());
		configurer.configureEditorAreaDropListener(new EditorAreaDropAdapter(
				configurer.getWindow()));

		hookTitleUpdateListeners(configurer);
	}

	/**
	 * Hooks the listeners needed on the window
	 * 
	 * @param configurer
	 */
	private void hookTitleUpdateListeners(IWorkbenchWindowConfigurer configurer) {
		// hook up the listeners to update the window title
		configurer.getWindow().addPageListener(new IPageListener() {
			@Override
			public void pageActivated(IWorkbenchPage page) {
				updateTitle();
			}

			@Override
			public void pageClosed(IWorkbenchPage page) {
				updateTitle();
			}

			@Override
			public void pageOpened(IWorkbenchPage page) {
				// A one-off hack to remove shortcuts for an old perspective
				IPerspectiveDescriptor[] sortedPerspectives = page.getSortedPerspectives();
				IPerspectiveDescriptor pd = null;
				for(IPerspectiveDescriptor p: sortedPerspectives)
				{
					String id = p.getId();
					if("gov.sandia.dart.rpp.ui.perspective.requirements".equals(id))
					{
						pd = p;
					}
				}
				if(pd != null)
				{
					page.closePerspective(pd, false, false);
				}
			}
		});
		configurer.getWindow().addPerspectiveListener(new PerspectiveAdapter() {
			@Override
			public void perspectiveActivated(IWorkbenchPage page,
					IPerspectiveDescriptor perspective) {
				updateTitle();
			}

			@Override
			public void perspectiveSavedAs(IWorkbenchPage page,
					IPerspectiveDescriptor oldPerspective,
					IPerspectiveDescriptor newPerspective) {
				updateTitle();
			}

			@Override
			public void perspectiveDeactivated(IWorkbenchPage page,
					IPerspectiveDescriptor perspective) {
				updateTitle();
			}
		});
		configurer.getWindow().getPartService().addPartListener(
				new IPartListener2() {
					@Override
					public void partActivated(IWorkbenchPartReference ref) {
						if (ref instanceof IEditorReference) {
							updateTitle();
						}
					}

					@Override
					public void partBroughtToTop(IWorkbenchPartReference ref) {
						if (ref instanceof IEditorReference) {
							updateTitle();
						}
					}

					@Override
					public void partClosed(IWorkbenchPartReference ref) {
						updateTitle();
					}

					@Override
					public void partDeactivated(IWorkbenchPartReference ref) {
						// do nothing
					}

					@Override
					public void partOpened(IWorkbenchPartReference ref) {
						// do nothing
					}

					@Override
					public void partHidden(IWorkbenchPartReference ref) {
						// do nothing
					}

					@Override
					public void partVisible(IWorkbenchPartReference ref) {
						// do nothing
					}

					@Override
					public void partInputChanged(IWorkbenchPartReference ref) {
						// do nothing
					}
				});
	}

	private String computeTitle() {
		StringJoiner sj = new StringJoiner(" - "); //$NON-NLS-1$

		IPreferenceStore ps = IDEWorkbenchPlugin.getDefault().getPreferenceStore();

		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		IWorkbenchPage currentPage = configurer.getWindow().getActivePage();
		IEditorPart activeEditor = null;
		if (currentPage != null) {
			activeEditor = lastActiveEditor;
		}

		// show workspace name
		if (ps.getBoolean(IDEInternalPreferences.SHOW_LOCATION_NAME)) {
			String workspaceName = ps.getString(IDEInternalPreferences.WORKSPACE_NAME);
			if (workspaceName != null && workspaceName.length() > 0) {
				sj.add(workspaceName);
			}
		}

		// perspective name
		if (ps.getBoolean(IDEInternalPreferences.SHOW_PERSPECTIVE_IN_TITLE)) {
			IPerspectiveDescriptor persp = currentPage.getPerspective();
			if (persp != null) {
				sj.add(persp.getLabel());
			}
		}

		// active editor
		if (currentPage != null) {
			if (activeEditor != null) {
				sj.add(activeEditor.getTitleToolTip());
			}
		}

		// workspace location is non null either when SHOW_LOCATION is true or
		// when forcing -showlocation via command line
		String workspaceLocation = wbAdvisor.getWorkspaceLocation();
		if (workspaceLocation != null) {
			sj.add(workspaceLocation);
		}

		// Application (product) name
		if (ps.getBoolean(IDEInternalPreferences.SHOW_PRODUCT_IN_TITLE)) {
			IProduct product = Platform.getProduct();
			if (product != null) {
				sj.add(product.getName());
			}
		}

		return sj.toString();
	}

	private void recomputeTitle() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		String oldTitle = configurer.getTitle();
		String newTitle = computeTitle();
		if (!newTitle.equals(oldTitle)) {
			configurer.setTitle(newTitle);
		}
	}

	/**
	 * Updates the window title. Format will be: [pageInput -]
	 * [currentPerspective -] [editorInput -] [workspaceLocation -] productName
	 */
	private void updateTitle() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		IWorkbenchWindow window = configurer.getWindow();
		IEditorPart activeEditor = null;
		IWorkbenchPage currentPage = window.getActivePage();
		IPerspectiveDescriptor persp = null;
		IAdaptable input = null;

		if (currentPage != null) {
			activeEditor = currentPage.getActiveEditor();
			persp = currentPage.getPerspective();
			input = currentPage.getInput();
		}

		// Nothing to do if the editor hasn't changed
		if (activeEditor == lastActiveEditor && currentPage == lastActivePage
				&& persp == lastPerspective && input == lastInput) {
			return;
		}

		if (lastActiveEditor != null) {
			lastActiveEditor.removePropertyListener(editorPropertyListener);
		}

		lastActiveEditor = activeEditor;
		lastActivePage = currentPage;
		lastPerspective = persp;
		lastInput = input;

		if (activeEditor != null) {
			activeEditor.addPropertyListener(editorPropertyListener);
		}

		recomputeTitle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#postWindowRestore
	 */
	public void doPostWindowRestore() throws WorkbenchException {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		IWorkbenchWindow window = configurer.getWindow();

		int index = getWorkbench().getWorkbenchWindowCount() - 1;

		AboutInfo[] welcomePerspectiveInfos = wbAdvisor
				.getWelcomePerspectiveInfos();
		if (index >= 0 && welcomePerspectiveInfos != null
				&& index < welcomePerspectiveInfos.length) {
			// find a page that exist in the window
			IWorkbenchPage page = window.getActivePage();
			if (page == null) {
				IWorkbenchPage[] pages = window.getPages();
				if (pages != null && pages.length > 0) {
					page = pages[0];
				}
			}

			// if the window does not contain a page, create one
			String perspectiveId = welcomePerspectiveInfos[index]
					.getWelcomePerspectiveId();
			if (page == null) {
				IAdaptable root = wbAdvisor.getDefaultPageInput();
				page = window.openPage(perspectiveId, root);
			} else {
				IPerspectiveRegistry reg = getWorkbench()
						.getPerspectiveRegistry();
				IPerspectiveDescriptor desc = reg
						.findPerspectiveWithId(perspectiveId);
				if (desc != null) {
					page.setPerspective(desc);
				}
			}

			// set the active page and open the welcome editor
			window.setActivePage(page);
			page.openEditor(new WelcomeEditorInput(
					welcomePerspectiveInfos[index]), WELCOME_EDITOR_ID, true);
		}
	}

	/**
	 * Tries to open the intro, if one exists and otherwise will open the legacy
	 * Welcome pages.
	 * 
	 * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#openIntro()
	 */
	@Override
	public void doOpenIntro() {
		if (editorsAndIntrosOpened) {
			return;
		}

		editorsAndIntrosOpened = true;

		// don't try to open the welcome editors if there is an intro
		if (wbAdvisor.hasIntro()) {
			super.doOpenIntro();
		} else {
			openWelcomeEditors(getWindowConfigurer().getWindow());
			// save any preferences changes caused by the above actions
			IDEWorkbenchPlugin.getDefault().savePluginPreferences();
		}
	}

	/*
	 * Open the welcome editor for the primary feature and for any newly
	 * installed features.
	 */
	private void openWelcomeEditors(IWorkbenchWindow window) {
		if (IDEWorkbenchPlugin.getDefault().getPreferenceStore().getBoolean(
				IDEInternalPreferences.WELCOME_DIALOG)) {
			// show the welcome page for the product the first time the
			// workbench opens
			IProduct product = Platform.getProduct();
			if (product == null) {
				return;
			}

			AboutInfo productInfo = new AboutInfo(product);
			URL url = productInfo.getWelcomePageURL();
			if (url == null) {
				return;
			}

			IDEWorkbenchPlugin.getDefault().getPreferenceStore().setValue(
					IDEInternalPreferences.WELCOME_DIALOG, false);
			openWelcomeEditor(window, new WelcomeEditorInput(productInfo), null);
		} else {
			// Show the welcome page for any newly installed features
			List welcomeFeatures = new ArrayList();
			for (Iterator it = wbAdvisor.getNewlyAddedBundleGroups().entrySet()
					.iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				String versionedId = (String) entry.getKey();
				String featureId = versionedId.substring(0, versionedId
						.indexOf(':'));
				AboutInfo info = (AboutInfo) entry.getValue();

				if (info != null && info.getWelcomePageURL() != null) {
					welcomeFeatures.add(info);
					// activate the feature plug-in so it can run some install
					// code
					IPlatformConfiguration platformConfiguration = ConfiguratorUtils
							.getCurrentPlatformConfiguration();
					IPlatformConfiguration.IFeatureEntry feature = platformConfiguration
							.findConfiguredFeatureEntry(featureId);
					if (feature != null) {
						String pi = feature.getFeaturePluginIdentifier();
						if (pi != null) {
							// Start the bundle if there is one
							Bundle bundle = Platform.getBundle(pi);
							if (bundle != null) {
								try {
									bundle.start(Bundle.START_TRANSIENT);
								} catch (BundleException exception) {
									StatusManager
											.getManager()
											.handle(
													new Status(
															IStatus.ERROR,
															WorkflowApplicationPlugin.ID,
															"Failed to load feature", exception));//$NON-NLS-1$
								}
							}
						}
					}
				}
			}

			int wCount = getWorkbench().getWorkbenchWindowCount();
			for (int i = 0; i < welcomeFeatures.size(); i++) {
				AboutInfo newInfo = (AboutInfo) welcomeFeatures.get(i);
				String id = newInfo.getWelcomePerspectiveId();
				// Other editors were already opened in postWindowRestore(..)
				if (id == null || i >= wCount) {
					openWelcomeEditor(window, new WelcomeEditorInput(newInfo),
							id);
				}
			}
		}
	}

	/**
	 * Open a welcome editor for the given input
	 */
	private void openWelcomeEditor(IWorkbenchWindow window,
			WelcomeEditorInput input, String perspectiveId) {
		if (getWorkbench().getWorkbenchWindowCount() == 0) {
			// Something is wrong, there should be at least
			// one workbench window open by now.
			return;
		}

		IWorkbenchWindow win = window;
		if (perspectiveId != null) {
			try {
				win = getWorkbench().openWorkbenchWindow(perspectiveId,
						wbAdvisor.getDefaultPageInput());
				if (win == null) {
					win = window;
				}
			} catch (WorkbenchException e) {
				IDEWorkbenchPlugin
						.log(
								"Error opening window with welcome perspective.", e.getStatus()); //$NON-NLS-1$
				return;
			}
		}

		if (win == null) {
			win = getWorkbench().getWorkbenchWindows()[0];
		}

		IWorkbenchPage page = win.getActivePage();
		String id = perspectiveId;
		if (id == null) {
			id = getWorkbench().getPerspectiveRegistry()
					.getDefaultPerspective();
		}

		if (page == null) {
			try {
				page = win.openPage(id, wbAdvisor.getDefaultPageInput());
			} catch (WorkbenchException e) {
				ErrorDialog.openError(win.getShell(),
						IDEWorkbenchMessages.Problems_Opening_Page, e
								.getMessage(), e.getStatus());
			}
		}
		if (page == null) {
			return;
		}

		if (page.getPerspective() == null) {
			try {
				page = getWorkbench().showPerspective(id, win);
			} catch (WorkbenchException e) {
				ErrorDialog
						.openError(
								win.getShell(),
								IDEWorkbenchMessages.Workbench_openEditorErrorDialogTitle,
								IDEWorkbenchMessages.Workbench_openEditorErrorDialogMessage,
								e.getStatus());
				return;
			}
		}

		page.setEditorAreaVisible(true);

		// see if we already have an editor
		IEditorPart editor = page.findEditor(input);
		if (editor != null) {
			page.activate(editor);
			return;
		}

		try {
			page.openEditor(input, WELCOME_EDITOR_ID);
		} catch (PartInitException e) {
			ErrorDialog
					.openError(
							win.getShell(),
							IDEWorkbenchMessages.Workbench_openEditorErrorDialogTitle,
							IDEWorkbenchMessages.Workbench_openEditorErrorDialogMessage,
							e.getStatus());
		}
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#createEmptyWindowContents(org.eclipse.ui.application.IWorkbenchWindowConfigurer,
	 *      org.eclipse.swt.widgets.Composite)
	 */
	public Control doCreateEmptyWindowContents(Composite parent) {
		final IWorkbenchWindow window = getWindowConfigurer().getWindow();
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		Display display = composite.getDisplay();
		Color bgCol = display
				.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND);
		composite.setBackground(bgCol);
		Label label = new Label(composite, SWT.WRAP);
		label.setForeground(display
				.getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		label.setBackground(bgCol);
		label.setFont(JFaceResources.getFontRegistry().getBold(
				JFaceResources.DEFAULT_FONT));
		String msg = IDEWorkbenchMessages.IDEWorkbenchAdvisor_noPerspective;
		label.setText(msg);
		ToolBarManager toolBarManager = new ToolBarManager();
		// TODO: should obtain the open perspective action from ActionFactory
		openPerspectiveAction = ActionFactory.OPEN_PERSPECTIVE_DIALOG
				.create(window);
		toolBarManager.add(openPerspectiveAction);
		ToolBar toolBar = toolBarManager.createControl(composite);
		toolBar.setBackground(bgCol);
		return composite;
	}

	@Override
	public void doPostWindowOpen() {
		super.doPostWindowOpen();
		
		IActionBarConfigurer configurer = getWindowConfigurer()
				.getActionBarConfigurer();

		IContributionItem[] coolItems = configurer.getCoolBarManager()
				.getItems();
		for (int i = 0; i < coolItems.length; i++) {
			if (coolItems[i] instanceof ToolBarContributionItem) {
				ToolBarContributionItem toolbarItem = (ToolBarContributionItem) coolItems[i];
				String id = toolbarItem.getId();
				if (toolbarItem.getId().equals(
						"org.eclipse.ui.WorkingSetActionSet")  //$NON-NLS-1$
//						toolbarItem.getId().equals("org.eclipse.ui.edit.text.actionSet.annotationNavigation") || //$NON-NLS-1$
//						toolbarItem.getId().equals("org.eclipse.ui.edit.text.actionSet.navigation") || //$NON-NLS-1$
//						toolbarItem.getId().equals("org.eclipse.search.searchActionSet") || 
//						toolbarItem.getId().equals("gov.sandia.dart.simba.plugins.core.modeleditor") || 
//						toolbarItem.getId().equals("org.eclipse.ui.workbench.file") || 
//						toolbarItem.getId().equals("org.eclipse.debug.ui.launchActionSet")
						) 
				{
					toolbarItem.getToolBarManager().removeAll();
					configurer.getCoolBarManager().remove(toolbarItem);
				}
				else if(toolbarItem.getId().equals("org.eclipse.ui.workbench.file"))
				{
					IContributionItem[] items = toolbarItem.getToolBarManager().getItems();
					for(int j = 0; j < items.length; j++)
					{
					  if ("new.group".equals(items[j].getId())
					      || "newWizardDropDown".equals(items[j].getId())) 
//						if(items[j].getId().equals("new.group") || items[j].getId().equals("newWizardDropDown"))
						{
							toolbarItem.getToolBarManager().remove(items[j]);
						}
					}
				}
				else if(toolbarItem.getId().equals("org.eclipse.debug.ui.launchActionSet"))
				{
//					IContributionItem[] items = toolbarItem.getToolBarManager().getItems();
//					for(int j = 0; j < items.length; j++)
//					{
//						if(items[j].getId().equals("org.eclipse.debug.internal.ui.actions.DebugDropDownAction"))
//						{
//							toolbarItem.getToolBarManager().remove(items[j]);
//						}
//					}
				}
			}
		}
		// deletes unwanted Menuitems
		IContributionItem[] menuItems = configurer.getMenuManager().getItems();
		for (int i = 0; i < menuItems.length; i++) {
			IContributionItem menuItem = menuItems[i];
			if (menuItem.getId().equals("org.eclipse.ui.run") && menuItem instanceof IMenuManager) 
			{
//				configurer.getMenuManager().remove(menuItem);
				IContributionItem[] items = ((IMenuManager) menuItem).getItems();
				for (IContributionItem item : items) {
					String id = item.getId();
					if (item.getId() != null
							&& (item.getId().equals("showIn")
									|| item.getId().equals("goInto")
									|| item.getId().equals("goTo"))) {
						((IMenuManager) menuItem).remove(item);
					}
				}
			} else if (menuItem.getId().equals(IWorkbenchActionConstants.M_FILE)) {
				// Do nothing with File right now
			} 
			else 
			if (menuItem.getId().equals(
					IWorkbenchActionConstants.M_NAVIGATE)) {
				IContributionItem[] items = ((IMenuManager) menuItem).getItems();
				for (IContributionItem item : items) {
					String id = item.getId();
					if (item.getId() != null
							&& (item.getId().equals("showIn")
									|| item.getId().equals("goInto")
									|| item.getId().equals("goTo"))) {
						((IMenuManager) menuItem).remove(item);
					}
				}
			}
			else if (menuItem.getId().equals(IWorkbenchActionConstants.M_WINDOW)) {
				IContributionItem[] items = ((IMenuManager) menuItem).getItems();
				for (IContributionItem item : items) {
					if (item.getId() != null
							&& item.getId().equals("selectWorkingSets")) {
						((IMenuManager) menuItem).remove(item);
					}
					else if(item.getId() != null && item.getId().equals("shortcuts"))
					{
						((IMenuManager) menuItem).remove(item);
					}
//					else if(item.getId() != null && (item.getId().equals("closeAllPerspectives")
//							|| item.getId().equals("closePerspective")
//							|| item.getId().equals("savePerspective")
//							|| item.getId().equals("editActionSets")
//							|| item.getId().equals("openPerspective")))
//					{
//						((IMenuManager) menuItem).remove(item);
//					}
					else if(item.getId() != null && item instanceof ActionContributionItem)
					{
						if(item.getId().equals("resetPerspective"))
						{
							((ActionContributionItem)item).getAction().setText("Reset Window Layout");
						}
					}
				}
			} 
//			else if (menuItem.getId().equals(IWorkbenchActionConstants.M_HELP)) {
//				IContributionItem[] items = ((IMenuManager) menuItem)
//						.getItems();
//				for (IContributionItem item : items) {
//					if (item.getId() != null
//							&& (item.getId().equals("org.eclipse.update.ui.updateMenu"))) {
//						((IMenuManager) menuItem).remove(item);
//					}
//				}
//			} 
		}
		configurer.getCoolBarManager().update(true);
		configurer.getMenuManager().update(true);

		// deletes unwanted Preferences
		PreferenceManager pm = getWindowConfigurer().getWindow().getWorkbench()
				.getPreferenceManager();
		IPreferenceNode[] prefNodes = pm.getRootSubNodes();
		for (IPreferenceNode node : prefNodes) {
			// if
			// ("org.eclipse.update.internal.ui.preferences.MainPreferencePage"
			// .equals(node.getId())) {
			// pm.remove(node);
			// } else if ("org.eclipse.ui.preferencePages.Workbench".equals(node
			// .getId())) {
			// pm.remove(node);
			// }
		}
		
		
		//A bug in Eclipse 4 does not respect the PERSPECTIVE_BAR_EXTRAS property.
		//So we will manually populate the perspective bar for a new workspace.
	}

	private void initPerspectiveShortcut(String perspectiveId, String initializedPreference)
	{
		IPreferenceStore apcWorkbenchStore = WorkflowApplicationPlugin.getDefault().getPreferenceStore();
		boolean perspectiveShortcutInitialized = apcWorkbenchStore.getBoolean(initializedPreference);
		if(perspectiveShortcutInitialized)
		{
			return;
		}
		
		apcWorkbenchStore.setValue(initializedPreference, true);

		
		IWorkbenchWindow iwindow = getWindowConfigurer().getWindow();
		
		if(iwindow instanceof WorkbenchWindow)
		{
			WorkbenchWindow window = (WorkbenchWindow) iwindow;
			
			EModelService modelService = (EModelService) PlatformUI.getWorkbench().getService(EModelService.class);
			MWindow model = window.getModel();
			List<MPerspectiveStack> theStack = modelService.findElements(model, null,
							MPerspectiveStack.class, null);
			MPerspectiveStack perspectiveStack=null;
			if (theStack.size() > 0) {
				perspectiveStack = theStack.get(0);
			}

			// Check if perspective already exists
			for(MPerspective perspective : perspectiveStack.getChildren()){
				if(perspective.getElementId().equals(perspectiveId)){
					return;
				}
			}
			
			// Construct the perspective
			IPerspectiveDescriptor perspectiveDescriptor = PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(perspectiveId);
			MPerspective perspective = MAdvancedFactory.INSTANCE.createPerspective();				
			perspective.setLabel(perspectiveDescriptor.getLabel());
			perspective.setElementId(perspectiveDescriptor.getId());
			EPartService partService = (EPartService) window.getService(EPartService.class);				
			IPerspectiveFactory factory = ((PerspectiveDescriptor) perspectiveDescriptor).createFactory();
			
			ModeledPageLayout modelLayout = new ModeledPageLayout(model, modelService,
							partService, perspective, perspectiveDescriptor, (WorkbenchPage) window.getActivePage(), true);
					factory.createInitialLayout(modelLayout);

			// Add perspective to list
			if(perspectiveStack!=null)
			   perspectiveStack.getChildren().add(perspective);
		}
	}

	
}
