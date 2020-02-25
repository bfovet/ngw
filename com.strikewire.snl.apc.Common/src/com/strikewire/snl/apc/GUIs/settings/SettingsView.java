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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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
import com.strikewire.snl.apc.GUIs.ChoicesButtonDialog;
import com.strikewire.snl.apc.GUIs.CompositeUtils;
import com.strikewire.snl.apc.GUIs.settings.IApplyableSettingsEditor.ApplyStatus;
import com.strikewire.snl.apc.osgi.util.EventUtils;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;

import gov.sandia.dart.common.preferences.CommonPreferencesPlugin;
import gov.sandia.dart.common.preferences.settings.ISettingsViewPreferences;

/**
 * @author mjgibso
 *
 */
public class SettingsView extends AbstractMessageView implements ISelectionListener, IPropertyChangeListener, IApplyableSettingsEditorListener
{
	public static final boolean DEFAULT_BROADCAST = false;
	
	private Composite _parent;
	
	private ISettingsEditor<Object> _currentEditor;
	
	private PropertySettingsEditor _propertyEditor = new PropertySettingsEditor();
	
	private final MyMultiControlSelectionProvider _selectionProvider = new MyMultiControlSelectionProvider();
	
	private IManagedForm _mform;
	
	private IContextMenuRegistrar _contextMenuRegistrar = new MyContextMenuRegistrar();

	private Composite _messageArea;
	
	public static final String VIEW_ID = "com.strikewire.snl.apc.common.view.settings";

	private FormToolkit _toolkit;

	private ScrolledForm _form;
	
	private Button _applyB;
	private Button _cancelB;
	
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
		_parent = parent;
		_parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		_parent.setLayout(layout);
		setListening(true);
		getSite().setSelectionProvider(_selectionProvider);
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
		if(_currentEditor != null)
		{
			focused = _currentEditor.setFocus();
		}
		
		if(!focused)
		{
			this._parent.setFocus();
		}
	}
	
	private void checkBlankEditor()
	{
		// if we're showing a real editor
		if(_currentEditor!=null && !(_currentEditor instanceof IEmptySelectionSettingsEditor))
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
		if(_parent.isDisposed())
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
	
	private static final String CHOICE_APPLY = "Apply";
	private static final String CHOICE_DISCARD = "Discard";
	private static final String CHOICE_CANCEL = "Cancel";
	
	/**
	 * Method checks the current editor for possible changes before allowing a change to be
	 * made in the currently displayed editor.  Returns true if the currently displayed editor
	 * should be allowed to be changed, false if it should stay in place as is.
	 * @return
	 */
	private boolean checkForChanges()
	{
		boolean allowChange = false;
		
		// before we do anything, see if the current editor has unsaved changes
		Optional<IApplyableSettingsEditor<?>> applyable = getApplyable();
		if(applyable.isPresent())
		{
			IApplyableSettingsEditor<?> editor = applyable.get();
			if(editor.isDirty())
			{
				ChoicesButtonDialog cbd = new ChoicesButtonDialog(getSite().getShell(), "Save Changes?",
						"There are unsaved changes in the Settings View.  Would you like to apply the changes?",
						new String[] {CHOICE_APPLY, CHOICE_DISCARD, CHOICE_CANCEL}, CHOICE_APPLY);
				if(cbd.open() != Window.CANCEL)
				{
					String choice = cbd.getValue();
					if(StringUtils.isNotBlank(choice))
					{
						switch (choice)
						{
							case CHOICE_APPLY:
								IStatus status = handleApply(editor);
								// if the apply didn't go smoothly, don't allow the editor to be changed
								if(status.isOK())
								{
									allowChange = true;
								}
								break;
							case CHOICE_DISCARD:
								// just let it continue, let this settings editor be lost
								allowChange = true;
								break;
							case CHOICE_CANCEL:
								// don't allow changing the editor...
								break;
							default:
								// shouldn't happen, but don't allow editor to be changed in this case...
								break;
						}
					}
				}
			} else { // editor is not dirty
				allowChange = true;
			}
		} else { // there is no current applyable settings editor
			allowChange = true;
		}
		
		return allowChange;
	}
	
	private void setEditor(ISettingsEditor<Object> editor, boolean clearIfNull, IStructuredSelection selection)
	{
		if(_parent.isDisposed())
		{
			return;
		}
		
		if(editor == null)
		{
			if(clearIfNull)
			{
				if(checkForChanges())
				{
					clear();
				}
			}
			
			return;
		}
		
		// we have a real editor.
		// if it's not the same editor as we already have
		boolean newEditor = false;
		if(!editor.equals(_currentEditor) || !editor.isReusable())
		{
			if(!checkForChanges())
			{
				return;
			}
			
			newEditor = true;
			
			// we have a new editor
			clear();
			
			_currentEditor = editor;
			FormColors colors = new FormColors(_parent.getDisplay());
			colors.createColor(IFormColors.BORDER, 0, 0, 0); 
			colors.setBackground(_parent.getDisplay().getSystemColor(SWT.COLOR_WHITE)); 

			_toolkit = new FormToolkit(colors);
			_mform = new ManagedForm(_toolkit, _toolkit.createScrolledForm(_parent));
			_toolkit = _mform.getToolkit();
			_form = _mform.getForm();
			GridDataFactory.fillDefaults().grab(true, true).applyTo(_form);
			IPreferenceStore store = CommonPreferencesPlugin.getDefault().getPreferenceStore();
			if(store.getBoolean(ISettingsViewPreferences.DRAW_BORDERS))
			{
				_toolkit.paintBordersFor(_form.getBody());
			}

			_toolkit.decorateFormHeading(_form.getForm());			
			_messageArea = createMessageArea(_form.getForm().getHead());
			_form.setHeadClient(_messageArea);
			setMessage("", IMessageProvider.NONE);

			if(_currentEditor instanceof IMultiSettingsEditor)
			{
				((IMultiSettingsEditor<Object>) _currentEditor).createPartControl(_mform, this, _selectionProvider, _contextMenuRegistrar);
			} else {
				_currentEditor.createPartControl(_mform, this, _selectionProvider, _contextMenuRegistrar);
			}
			
			getApplyable().ifPresent(a -> {
				createBottomButtonPanel();
				a.setDirtyStateListener(this);
			});
 		} else {
			setMessage("", IMessageProvider.NONE);
		}
		
		if(!newEditor)
		{
			if(!checkForChanges())
			{
				return;
			}
		}
		
		// now set the selection on the editor
		if(_currentEditor instanceof IMultiSettingsEditor)
		{
			((IMultiSettingsEditor<Object>) _currentEditor).setNodes(Arrays.asList(selection.toArray()));
		} else {
			_currentEditor.setNode(selection.getFirstElement());
		}
		_parent.layout(true, true);
		
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
	
	private void createBottomButtonPanel()
	{
		Label sepLine = _toolkit.createSeparator(_parent, SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().applyTo(sepLine);
		
		Composite buttonBar = _toolkit.createComposite(_parent);
		GridDataFactory.fillDefaults().applyTo(buttonBar);
		
		int numCols = 2;
		GridLayout layout = new GridLayout(numCols, false);
		buttonBar.setLayout(layout);
		
		int defaultButtonWidth = 100;
		_applyB = _toolkit.createButton(buttonBar, "Apply", SWT.PUSH);
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.RIGHT, SWT.CENTER).hint(defaultButtonWidth, SWT.DEFAULT).applyTo(_applyB);
		_cancelB = _toolkit.createButton(buttonBar, "Revert", SWT.PUSH);
		GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).hint(defaultButtonWidth, SWT.DEFAULT).applyTo(_cancelB);
		
		_applyB.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				getApplyable().ifPresent(SettingsView.this::handleApply);
			}
		});
		
		_cancelB.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				getApplyable().ifPresent(SettingsView.this::handleCancel);
			}
		});
		
		dirtyStateChanged();
	}
	
	private IStatus handleApply(IApplyableSettingsEditor<?> editor)
	{
		IStatus status = Status.CANCEL_STATUS;
		switch (editor.getApplyStatus()) {
			case CONFLICTING_IRRECONCILABLE:
				MessageDialog.openError(getSite().getShell(), "Out Of Sync",
						"The underlying data source for this editor has changed or is no longer in "
						+ "sync with the data displayed in the editor.  You must revert your changes to "
						+ "re-load the editor.");
				break;
			case CONFLICTING_REPLACABLE:
				boolean overwrite = MessageDialog.openConfirm(getSite().getShell(), "Confirm Overwrite",
						"The underlying data source for this editor has changed or is no longer in "
						+ "sync with the data displayed in the editor.  If you continue, you will "
						+ "overwrite any other changes in the underlying data source made outside of this editor.");
				if(overwrite)
				{
					 status = editor.performApply();
				}
				break;
			case CONFLICTING_MERGEABLE:
				boolean merge = MessageDialog.openConfirm(getSite().getShell(), "Confirm Merge",
						"The underlying data source for this editor has changed or is no longer in "
						+ "sync with the data displayed in the editor.  If you continue, an attempt "
						+ "will be made to merge your changes with the underlying data source.");
				if(merge)
				{
					status = editor.performApply();
				}
				break;
			case OK:
				status = editor.performApply();
				break;
		}
		
		return status;
	}
	
	private void handleCancel(IApplyableSettingsEditor<?> editor)
	{
		boolean revert = MessageDialog.openConfirm(getSite().getShell(), "Revert Changes?",
				"Any changes made in the settings editor will be lost.");
		if(revert)
		{
			editor.performCancel();
		}
	}
	
	private Optional<IApplyableSettingsEditor<?>> getApplyable()
	{
		Optional<ISettingsEditor<?>> seOp = Optional.ofNullable(_currentEditor);
		Optional<IApplyableSettingsEditor<?>> apOp = seOp.filter(IApplyableSettingsEditor.class::isInstance).map(IApplyableSettingsEditor.class::cast);
//		if(!apOp.isPresent())
//		{
//			CommonPlugin.getDefault().logError("Editor is not applyable.", null);
//		}
		return apOp;
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
				settingsView._selectionProvider.forceFire(new SettingsSelection(selection));
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
			if(_currentEditor != null)

			{
				_currentEditor.dispose();
				this._currentEditor = null;
			}

			CompositeUtils.removeChildrenFromComposite(_parent, false);

			_selectionProvider.clear();
			
			_mform = null;
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
			return _propertyEditor;
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
			
			ISettingsEditor<?> currentEditor = _currentEditor;
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
		_messageArea.setLayoutData(null);
		_form.setHeadClient(showMessageArea ? _messageArea : null);
		_form.getForm().layout(new Control[] {messageLabel, messageImageLabel});
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (ISettingsViewPreferences.DRAW_BORDERS.equals(event.getProperty()))
			clear();						
	}

	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.settings.IDirtyStateListener#dirtyStateChanged()
	 */
	@Override
	public void dirtyStateChanged()
	{
		getApplyable().ifPresent(this::updateState);
	}
	
	private void updateState(IApplyableSettingsEditor<?> editor)
	{
		updateViewName(editor);
		updateButtonState(editor);
	}
	
	private void updateViewName(IApplyableSettingsEditor<?> editor)
	{
		boolean dirty = editor.isDirty();
		//System.out.println(getTitle());
		String name = getPartName();
		boolean marked = name.startsWith("*");
		if(dirty != marked)
		{
			String newName = dirty ? "*"+name : name.substring(1);
			setPartName(newName);
		}
	}
	
	private void updateButtonState(IApplyableSettingsEditor<?> editor)
	{
		boolean dirty = editor.isDirty();
//		ApplyStatus status = editor.getApplyStatus();
		
		// TODO until we have status displayed indicating irreconcilable changes, don't disable the button due to them,
		// let the user attempt to apply, and then inform them why they can't.
//		this._applyB.setEnabled(dirty && status!=ApplyStatus.CONFLICTING_IRRECONCILABLE);
		this._applyB.setEnabled(dirty);
		this._cancelB.setEnabled(dirty);
	}

	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.settings.IApplyableSettingsEditorListener#applyStatusChanged(com.strikewire.snl.apc.GUIs.settings.IApplyableSettingsEditor.ApplyStatus)
	 */
	@Override
	public void applyStatusChanged(ApplyStatus newStatus)
	{
		getApplyable().ifPresent(this::updateState);
	}
}
