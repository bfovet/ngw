/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/**
 * 
 */
package com.strikewire.snl.apc.GUIs.settings;

import gov.sandia.dart.common.preferences.CommonPreferencesPlugin;
import gov.sandia.dart.common.preferences.settings.ISettingsViewPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.GUIs.CompositeUtils;
import com.strikewire.snl.apc.osgi.util.EventUtils;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;

/**
 * @author mjgibso
 *
 */
public class SettingsView extends AbstractMessageView implements ISelectionListener, IPropertyChangeListener
{
	public static final boolean DEFAULT_BROADCAST = false;
	
	private Composite parent_;
	
	private ISettingsEditor<Object> currentEditor_;
	
	private PropertySettingsEditor propertyEditor_ = new PropertySettingsEditor();
	
	private final MyMultiControlSelectionProvider selectionProvider_ = new MyMultiControlSelectionProvider();
	
	private IContextMenuRegistrar contextMenuRegistrar_ = new MyContextMenuRegistrar();

	private Composite messageArea;
	
	public static final String VIEW_ID = "com.strikewire.snl.apc.common.view.settings";

	private FormToolkit toolkit;

	private ScrolledForm form;
	
	private class PerspectiveChangeListener implements EventHandler
	{
		public static final String TOPIC = "org/eclipse/e4/ui/model/ui/ElementContainer/selectedElement/SET";
		
		@Override
		public void handleEvent(Event event) {
			Object newVal = event.getProperty("NewValue");
			Object oldVal = event.getProperty("OldValue");
			if(newVal instanceof MPerspective && oldVal instanceof MPerspective)
			{
				checkBlankEditor();
			}
		}
	}
	
	private final PerspectiveChangeListener _perspectiveChangeListener = new PerspectiveChangeListener();
	
	public SettingsView()
	{		
		IPreferenceStore store = CommonPreferencesPlugin.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		parent_ = parent;
		parent_.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		parent_.setLayout(new FillLayout());
		setListening(true);
		getSite().setSelectionProvider(selectionProvider_);
		EventUtils.registerForEvent(PerspectiveChangeListener.TOPIC, _perspectiveChangeListener);
		checkBlankEditor();
	}
	
	public void setListening(boolean listening)
	{
		if(listening) {
			getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
		} else {
			getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
		boolean focused = false;
		if(currentEditor_ != null)
		{
			focused = currentEditor_.setFocus();
		}
		
		if(!focused)
		{
			this.parent_.setFocus();
		}
	}
	
	private void checkBlankEditor()
	{
		// if we're showing a real editor
		if(currentEditor_!=null && !(currentEditor_ instanceof IEmptySelectionSettingsEditor))
		{
			// leave it there
			return;
		}
		
		// so we're showing nothing, or an empty settings editor
		
		// see what we should be showing
		ISettingsEditor<Object> emptyEditor = null;
		try {
			emptyEditor = SettingsExtensionManager.getInstance().getEditorForCurrentPerspective();
		} catch (CoreException e) {
			CommonPlugin.getDefault().log(e.getStatus());
		}
		
		ISelection sel = getSite().getSelectionProvider().getSelection();
		IStructuredSelection iss = sel instanceof IStructuredSelection ? (IStructuredSelection) sel : new StructuredSelection();
		setEditor(emptyEditor, true, iss);
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		// we don't care about ourselves...
		if(part == this)
		{
			return;
		}
		
		if(!(selection instanceof IStructuredSelection))
		{
			// TODO clear the currently showing editor?
			return;
		}
		
		final IStructuredSelection iss = (IStructuredSelection) selection;
		
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
      public void run() {
				try {
					setSelection(iss);
				} catch (Throwable t) {
					CommonPlugin.getDefault().logError("Error opening node in Settings View", t);
				}
			}
		});
	}
	
	private void setSelection(IStructuredSelection selection)
	{
		if(parent_.isDisposed())
		{
			return;
		}
		
		// see if we have a specific editor to use for the given selection
		
		// TODO If the selection adapts to different editors, do we want to choose one of them somehow?
		// Get the common editor for the selection.
		 ISettingsEditor<Object> editor = getCommonEditor(selection);
		
		// TODO clear the currently showing editor if we didn't get one?  Could be confusing to leave it.
		boolean clear = false;
		
		// if we don't have a specific editor to set
		if(editor==null && selection.isEmpty())
		{
			// get what the current empty selection editor would be for the current perspective
			try {
				editor = SettingsExtensionManager.getInstance().getEditorForCurrentPerspective();
			} catch (CoreException e) {
				CommonPlugin.getDefault().logError(e);
			}
			clear = true;
		}
		
		setEditor(editor, clear, selection);
	}
	
	private void setEditor(ISettingsEditor<Object> editor, boolean clearIfNull, IStructuredSelection selection)
	{
		if(parent_.isDisposed())
		{
			return;
		}
		
		if(editor == null)
		{
			if(clearIfNull)
			{
				clear();
			}
			
			return;
		}
		
		// we have a real editor.
		// if it's not the same editor as we already have
		if(!editor.equals(currentEditor_) || !editor.isReusable())
		{
			// we have a new editor
			clear();
			
			currentEditor_ = editor;
			FormColors colors = new FormColors(parent_.getDisplay());
			colors.createColor( IFormColors.BORDER, 0, 0, 0); 
			colors.setBackground(parent_.getDisplay().getSystemColor(SWT.COLOR_WHITE)); 

			toolkit =  new FormToolkit(colors);
			IManagedForm mform = new ManagedForm(toolkit, toolkit.createScrolledForm(parent_));
			toolkit = mform.getToolkit(); 
			form = mform.getForm();
			IPreferenceStore store = CommonPreferencesPlugin.getDefault().getPreferenceStore();
			if (store.getBoolean(ISettingsViewPreferences.DRAW_BORDERS)) {
				toolkit.paintBordersFor(form.getBody());
			}

			toolkit.decorateFormHeading(form.getForm());			
			messageArea = createMessageArea(form.getForm().getHead());
			form.setHeadClient(messageArea);
			setMessage("", IMessageProvider.NONE);

			if(currentEditor_ instanceof IMultiSettingsEditor)
			{
				((IMultiSettingsEditor<Object>) currentEditor_).createPartControl(mform, this, selectionProvider_, contextMenuRegistrar_);
			} else {
				currentEditor_.createPartControl(mform, this, selectionProvider_, contextMenuRegistrar_);
			}
		} else {
			setMessage("", IMessageProvider.NONE);
		}
		
		// now set the selection on the editor
		if(currentEditor_ instanceof IMultiSettingsEditor)
		{
			((IMultiSettingsEditor<Object>) currentEditor_).setNodes(Arrays.asList(selection.toArray()));
		} else {
			currentEditor_.setNode(selection.getFirstElement());
		}
		parent_.layout(true, true);
		
		Object first = selection.getFirstElement();
		if(first != null)
		{
			Object adapter = Platform.getAdapterManager().getAdapter(first, "org.eclipse.core.commands.operations.IUndoContext");
			if(adapter instanceof IUndoContext)
			{
				setUndoContext((IUndoContext) adapter);
			}
		}
	}
	
	private void setUndoContext(IUndoContext undoCtx) {
		UndoRedoActionGroup grp = new UndoRedoActionGroup(this.getSite(), undoCtx, false);
		grp.fillActionBars(getViewSite().getActionBars());
	}

	public static void openEditor(IStructuredSelection selection) throws PartInitException
	{
		openEditor(selection, DEFAULT_BROADCAST);
	}
	
	public static void openEditor(IStructuredSelection selection, boolean broadcast) throws PartInitException
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if(window != null)
		{
			IWorkbenchPage page = window.getActivePage();
			if(page != null)
			{
				openEditor(page, selection, broadcast);
			}
		}
	}
	
	public static void openEditor(IWorkbenchPage page, IStructuredSelection selection) throws PartInitException
	{
		openEditor(page, selection, DEFAULT_BROADCAST);
	}
	
	public static void openEditor(IWorkbenchPage page, IStructuredSelection selection, boolean broadcast) throws PartInitException
	{
		IViewPart vp = page.findView(VIEW_ID);
		if(vp == null)
		{
			vp = page.showView(VIEW_ID, null, IWorkbenchPage.VIEW_CREATE);
		}
		if(vp instanceof SettingsView)
		{
			SettingsView settingsView = (SettingsView) vp;
			settingsView.setSelection(selection);
			page.activate(settingsView);
			
			if(broadcast)
			{
				settingsView.selectionProvider_.forceFire(new SettingsSelection(selection));
			}
		} else {
			// error?
		}
	}
	
	@Override
	public void dispose() {
		IPreferenceStore store = CommonPreferencesPlugin.getDefault().getPreferenceStore();
		store.removePropertyChangeListener(this);
	}
	
	private void clear()
	{
		try {
			if(currentEditor_ != null)

			{
				currentEditor_.dispose();
				this.currentEditor_ = null;
			}

			CompositeUtils.removeChildrenFromComposite(parent_, false);

			selectionProvider_.clear();
		} catch (Throwable t) {
			// Carry on.
			CommonPlugin.getDefault().logError("Error clearing settings view; ignoring", t);
		}
	}
	
	private ISettingsEditor<Object> getCommonEditor(IStructuredSelection nodes)
	{
		Map<ISettingsEditor<Object>, Collection<Object>> editors = getSettingsEditors(nodes);
		if(editors.size() == 1)
		{
			// this could of course return null, if none of the nodes adapt to an editor
			return editors.keySet().iterator().next();
		}
		
		// TODO If the selection adapts to different editors, do we want to choose one of them somehow?
		
		return null;
	}
	
	private Map<ISettingsEditor<Object>, Collection<Object>> getSettingsEditors(IStructuredSelection nodes)
	{
		Map<ISettingsEditor<Object>, Collection<Object>> editors = new HashMap<ISettingsEditor<Object>, Collection<Object>>();
		for(Object node : nodes.toArray())
		{
			ISettingsEditor<Object> editor = getEditor(node);
			// if we have a multi-selection, but the editor cannot handle multi-selection, don't include it
			if(nodes.size()>1 && !(editor instanceof IMultiSettingsEditor))
			{
				continue;
			}
			
			Collection<Object> nodesForEditor = editors.get(editor);
			if(nodesForEditor == null)
			{
				nodesForEditor = new ArrayList<Object>();
				// hashmap supports null keys
				editors.put(editor, nodesForEditor);
			}
			nodesForEditor.add(node);
		}
		return editors;
	}
	
	@SuppressWarnings("unchecked")
	private ISettingsEditor<Object> getEditor(Object node)
	{
		Object adapter = getAdapter(node, ISettingsEditor.class);
		if(adapter instanceof ISettingsEditor)
		{
			return (ISettingsEditor<Object>) adapter;
		}
		
		adapter = getAdapter(node, IPropertySheetPage.class);
		if(adapter instanceof IPropertySheetPage)
		{
			return new PropertyPageSettingsEditor();
		}
		
		adapter = getAdapter(node, IPropertySource.class);
		if(adapter instanceof IPropertySource)
		{
			return propertyEditor_;
		}
		
		return null;
	}
	
	private Object getAdapter(Object node, Class<?> adapterType)
	{
		Object adapter = Platform.getAdapterManager().loadAdapter(node, adapterType.getName());
		if(adapterType.isInstance(adapter))
		{
			return adapter;
		}
		
		if(node instanceof IAdaptable)
		{
			adapter = ((IAdaptable) node).getAdapter(adapterType);
		}
		
		if(adapterType.isInstance(adapter))
		{
			return adapter;
		}
		
		return null;
	}
	
	private class MyMultiControlSelectionProvider extends MultiControlSelectionProvider
	{
		@Override
		public synchronized ISelection getSelection()
		{
			if(activeProvider_ != null)
			{
				return super.getSelection();
			}
			
			ISettingsEditor<?> currentEditor = currentEditor_;
			if(currentEditor != null)
			{
				ISelection sel = null;
				if(currentEditor instanceof IMultiSettingsEditor<?>)
				{
					List<?> nodes = ((IMultiSettingsEditor<?>) currentEditor).getNodes();
					if(nodes != null)
					{
						sel = new StructuredSelection(nodes);
					}
				} else {
					Object node = currentEditor.getNode();
					if(node != null)
					{
						sel = new StructuredSelection(node);
					}
				}
				
				if(sel != null)
				{
					return sel;
				}
			}
			
			return new StructuredSelection();
		}
		
		public void forceFire(ISelection selection)
		{
			fireSelectionEvent(new SelectionChangedEvent(this, selection));
		}
	}
	
	private class MyContextMenuRegistrar implements IContextMenuRegistrar
	{
		@Override
		public void registrarContextMenu(String ID, MenuManager menuMgr, ISelectionProvider selectionProvider)
		{
			IViewSite site = getViewSite();
			if(site == null)
			{
				return;
			}
			
			site.registerContextMenu(VIEW_ID+'.'+ID, menuMgr, selectionProvider);
		}
	}

	@Override
	protected String getDefaultMessage() {
		return "";
	}
	
	@Override
	protected void layoutForNewMessage() {
		boolean showMessageArea = StringUtils.isNotBlank(message) && messageImage!=null;
		messageArea.setLayoutData(null);
		form.setHeadClient(showMessageArea ? messageArea : null);
		form.getForm().layout(new Control[] {messageLabel, messageImageLabel});
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (ISettingsViewPreferences.DRAW_BORDERS.equals(event.getProperty()))
			clear();						
	}
}
