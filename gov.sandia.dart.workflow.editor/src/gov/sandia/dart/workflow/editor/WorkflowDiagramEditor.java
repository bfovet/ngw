/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.FreeformGraphicalRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.views.palette.PaletteView;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.examples.common.FileService;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramComposite;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.editor.IDiagramContainerUI;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.strikewire.snl.apc.GUIs.CompositeUtils;
import com.strikewire.snl.apc.GUIs.browsers.CommonFileSelectionDialog;
import com.strikewire.snl.apc.selection.DefaultSelectionProviderWithFocusListener;
import com.strikewire.snl.apc.selection.ISelectionProviderWithFocusListener;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;
import com.strikewire.snl.apc.temp.TempFileManager;
import com.strikewire.snl.apc.util.ResourceUtils;

import gov.sandia.dart.workflow.domain.Arc;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.monitoring.WorkflowTracker;
import gov.sandia.dart.workflow.editor.preferences.IWorkflowEditorPreferences;
import gov.sandia.dart.workflow.editor.settings.WFArcSettingsEditor;
import gov.sandia.dart.workflow.util.PropertyUtils;
import gov.sandia.dart.workflow.util.WorkflowUtils;

public class WorkflowDiagramEditor extends DiagramEditor implements IPropertyChangeListener  {

	public static final String ID = "gov.sandia.dart.workflow.editor.WorkflowDiagramEditor";
	private Label messageLabel;
	private SubdirSelectionCombo runLocationCombo;
	private AtomicReference<IFile> _baseFile = new AtomicReference<>();
	private Composite _pathComposite;
	private Composite _mainComposite;
	private StackLayout _mainStack;
	private Composite _rootComposite;
	private DefaultEditDomain _rootEditDomain;
	private KeyboardStateListener listener;

	private Stack<NestedChild> _nestedChildrenStack = new Stack<>();
	
	private MultiControlSelectionProvider _selectionProvider = new MultiControlSelectionProvider();
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		this._rootEditDomain = getEditDomain();
		WorkflowEditorPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}
	
	@Override
	public DiagramBehavior createDiagramBehavior() {
		return new WorkflowDiagramBehavior(this);		
	}
			
	@Override
	public void setFocus() {
		super.setFocus();
		updateHasRunData();
	}
	
	
	
	@Override
	public void createPartControl(Composite parent)
	{
		Display d = parent.getDisplay();
		if (listener == null) {
			listener = new KeyboardStateListener(d);
			d.addFilter(SWT.KeyDown, listener);		
			d.addFilter(SWT.KeyUp, listener);		
		}
		
		GridLayout parentLayout = new GridLayout(1, false);
		parentLayout.marginHeight = 0;
		parentLayout.marginWidth = 0;
		parentLayout.horizontalSpacing = 0;
		parentLayout.verticalSpacing = 0;
		parent.setLayout(parentLayout);
		
		Composite contents = new Composite(parent, SWT.NONE);
		contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		GridLayout contentsLayout = new GridLayout(1, false);
		contentsLayout.marginHeight = 0;
		contentsLayout.marginWidth = 0;
		contentsLayout.horizontalSpacing = 0;
		contentsLayout.verticalSpacing = 0;
		contents.setLayout(contentsLayout);

		Composite toolbarLine = new Composite(contents, SWT.NONE);
		toolbarLine.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		toolbarLine.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		GridLayout toolbarLayout = new GridLayout(3, false);
		toolbarLayout.verticalSpacing = 0;
		toolbarLine.setLayout(toolbarLayout);		
		
		Label runLabel = new Label(toolbarLine, SWT.NONE);
		runLabel.setText("Run in:");
		runLabel.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));

		CCombo combo = new CCombo(toolbarLine, SWT.BORDER);
		runLocationCombo = new SubdirSelectionCombo(combo);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));			
		runLocationCombo.getCCombo().addModifyListener(e -> updateHasRunData());
		
		_pathComposite = new Composite(contents, SWT.NONE);
		_pathComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		_pathComposite.setBackground(getSite().getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		RowLayout pathLayout = new RowLayout();
		pathLayout.marginTop = 0;
		pathLayout.marginBottom = 0;
		pathLayout.marginLeft = 0;
		pathLayout.marginRight = 0;
		_pathComposite.setLayout(pathLayout);
		_pathComposite.setVisible(false);
		
		_mainComposite = new Composite(contents, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(_mainComposite);
		_mainStack = new StackLayout();
		_mainComposite.setLayout(_mainStack);
		
		_rootComposite = buildRootComposite(_mainComposite);
		_mainStack.topControl = _rootComposite;
		
		IFile file = getWorkflowFile();
		if (file != null) {
			updateRunLocationLabelFromMarker(file);	
		}
		messageLabel = new Label(contents, SWT.NONE);
		messageLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}
	
	protected Composite buildRootComposite(Composite parent) {
		return buildGraphComposite(parent);
	}
		
	protected Composite buildGraphComposite(Composite parent) {
		Composite graphComposite = new Composite(parent, SWT.NONE);
		graphComposite.setLayout(new FillLayout());
				
		super.createPartControl(graphComposite);

		return graphComposite;
	}

	/**
	 * This belongs in the embedded plugin, but how?
	 * @return
	 */

	protected Object updateHasRunData() {
		
		IToolBehaviorProvider provider = getDiagramTypeProvider().getCurrentToolBehaviorProvider();
		if (provider instanceof WorkflowToolBehaviorProvider) {
			IFile workflowFile = getWorkflowFile();
			IPath path = runLocationCombo.getPath();
			File workdir = new File(path.toOSString());
			WorkflowTracker.updateRunData(workflowFile, workdir);
		}
		getDiagramTypeProvider().getDiagramBehavior().refresh();
		return null;
	}
	
	public void clearStatus() {
		messageLabel.setText("");
	}
	
	public void setStatus(String message) {
		messageLabel.setText(message);
	}
		
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		openPaletteView();
		
		IFile file = getWorkflowFile();
		if (file != null && file.exists() && runLocationCombo != null) {
			updateRunLocationLabelFromMarker(file);
		}
		// Would like to do this in persistency behavior but don't know how
		Diagram d = getDiagramTypeProvider().getDiagram();
		IFeatureProvider fp = getDiagramTypeProvider().getFeatureProvider();
		TransactionalEditingDomain editingDomain = getEditingDomain();				
		final CommandStack commandStack = editingDomain.getCommandStack();
		final boolean[] changed = new boolean[1];
		commandStack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				for (Connection c: getDiagramTypeProvider().getDiagram().getConnections()) {
					Object bo = getDiagramTypeProvider().getFeatureProvider().getBusinessObjectForPictogramElement(c);
					if (bo instanceof Arc) {
						changed[0] = changed[0] | WFArcSettingsEditor.updateConnectionAppearance(d, fp, (Arc) bo);
					}
				}					
			}
		});
		try {
			if (changed[0]) {
				doSave(null);
			} else {
				((WorkflowDiagramBehavior )getDiagramBehavior()).markClean();
				updateDirtyState();
			}
		} catch (Exception e) {
			// MUST NOT FAIL
		}

	}

	public IFile getWorkflowFile() {
		IDiagramTypeProvider diagramTypeProvider = getDiagramBehavior().getDiagramTypeProvider();
		Diagram diagram = diagramTypeProvider.getDiagram();
		Resource eResource = diagram.eResource();
		URI uri = eResource.getURI();
		String pathString = uri.toPlatformString(true);
		return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pathString));
	}
	
	@Override
	public void refreshTitle() {
		IFile baseFile = getBaseFile();
		setPartName(baseFile.getName());
//		String name = URI.decode(getDiagramTypeProvider().getDiagram().eResource().getURI().lastSegment());	
//		setPartName(name);		
	}

	protected void openPaletteView() {
		try {
			IWorkbenchPartSite site = getSite();
			if (site != null) {
				IWorkbenchWindow workbenchWindow = site.getWorkbenchWindow();
				if (workbenchWindow != null) {
					IWorkbenchPage activePage = workbenchWindow.getActivePage();
					if (activePage != null) {
						activePage.showView(PaletteView.ID, null, IWorkbenchPage.VIEW_VISIBLE);
					}
				}
			}			
		} catch (Exception e) {
			// Whatever
		}
	}
	
	@Override
	protected DiagramEditorInput convertToDiagramEditorInput(IEditorInput input)
			throws PartInitException {
		if (input instanceof FileStoreEditorInput) {			
			final java.net.URI uri = ((FileStoreEditorInput) input).getURI();
			final String path = uri.getPath();
			org.eclipse.emf.common.util.URI emfURI = org.eclipse.emf.common.util.URI.createFileURI(path);
			input = new URIEditorInput(emfURI);
		}
		return super.convertToDiagramEditorInput(input);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void doSaveAs() {
		try {
			Diagram oldDiagram = getDiagramTypeProvider().getDiagram();						
			URI oldURI = oldDiagram.eResource().getURI();
			oldURI = oldDiagram.eResource().getResourceSet().getURIConverter().normalize(oldURI);
			
		    String scheme = oldURI.scheme();		    
		    IFile oldFile = null;		    
		    if ("platform".equals(scheme) && oldURI.segmentCount() > 1 &&
		    		"resource".equals(oldURI.segment(0)))
		    {
			      StringBuffer platformResourcePath = new StringBuffer();
			      for (int j = 1, size = oldURI.segmentCount(); j < size; ++j)
			      {
				        platformResourcePath.append('/');
				        platformResourcePath.append(oldURI.segment(j));
			      }
			      oldFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformResourcePath.toString()));
			      
		    }
		    if (oldFile == null) {
		    	  Display.getDefault().beep();
		    	  return;
		      }
		    CommonFileSelectionDialog dialog = new CommonFileSelectionDialog(getSite().getShell(), oldFile.toString(), "Select file to Save As...", SWT.SAVE);
			dialog.setFilterExtensions(new String[]{"*.iwf"});
			//TODO make dialog browse to file!
			
			
//			SaveAsDialog dialog = new SaveAsDialog(getSite().getShell());	
//			dialog.setOriginalFile(oldFile);
			
			int ret = dialog.open();
			if(ret != Window.OK)
			{
				return;
			}
			
			IFile newFile = dialog.getFirstSelectionAsCommonFile().asIFile();
			IPath newLoc = newFile.getFullPath();
//			IPath newLoc = dialog.getResult();	
//			IFile newFile = ResourcesPlugin.getWorkspace().getRoot().getFile(newLoc);
			
			if(newLoc != null)
			{
				URI newURI = URI.createPlatformResourceURI(newLoc.toString(), true);								
				if(newFile.exists()) {
					if(oldURI.equals(newURI)) {
						doSave(null);
						return;
					} else {
						newFile.delete(true, null);
					}
				}

				ByteArrayOutputStream baos = new ByteArrayOutputStream();	
				
				oldDiagram.eResource().save(baos, null);				
				ResourceUtils.setContents(newFile, baos.toString());

				DiagramEditorInput editorInput = new DiagramEditorInput(newURI, null);				
				WorkflowDiagramEditor workflowEditor = (WorkflowDiagramEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, WorkflowDiagramEditor.ID);
				Diagram newDiagram = workflowEditor.getDiagramTypeProvider().getDiagram();

				
				
				TransactionalEditingDomain editingDomain = workflowEditor.getEditingDomain();				
				final CommandStack commandStack = editingDomain.getCommandStack();
				commandStack.execute(new RecordingCommand(editingDomain) {

					@Override
					protected void doExecute() {
						newDiagram.setName(newFile.getName());
					}
				});
				workflowEditor.doSave(null);

				editingDomain.dispose();
				
				close();							
			}				
		}catch(CoreException | IOException e) {
			
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if (listener != null) {
			listener.display.removeFilter(SWT.KeyDown, listener);
			listener.display.removeFilter(SWT.KeyUp, listener);

			listener = null;
		}
		WorkflowEditorPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		getGraphicalViewer().getRootEditPart().refresh();
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		getGraphicalViewer().getRootEditPart().getViewer().getControl().redraw();
		getGraphicalViewer().getRootEditPart().refresh();
	}
	
	// TODO Change this to a URI after it's working
	public void setRunLocation(File folder) {
		final IFile workflowFile = getWorkflowFile();
		WorkflowUtils.updateRunLocationMarker(workflowFile, folder);
		updateRunLocationLabelFromMarker(workflowFile);
	}

	@Override
	public void hookGraphicalViewer()
	{
		GraphicalViewer gv = getGraphicalViewer();
		getSelectionSynchronizer().addViewer(gv);
		ISelectionProviderWithFocusListener provider = new DefaultSelectionProviderWithFocusListener(gv, gv.getControl());
		addGraphicalViewerToSelectionProvider(provider);
		getSite().setSelectionProvider(_selectionProvider);
	}
	
	private IFile getBaseFile()
	{
		return _baseFile.updateAndGet(f -> {
			return f!=null ? f : getWorkflowFile();
		});
	}
	
	public File getRunLocation() {
		if (StringUtils.isEmpty(runLocationCombo.getCCombo().getText())) {
			File runLocation = getWorkflowFile().getParent().getLocation().toFile();
			String subdirName = getPartName();
			
			int extIndex = subdirName.indexOf('.');
			if(extIndex > 0) {
				subdirName = subdirName.substring(0,  extIndex);
			}
			
			int index = 0;

			File testLocation = new File(runLocation, subdirName);		
			while(testLocation.exists()) {
				index++;
				testLocation = new File(runLocation, subdirName + "(" + index + ")");
			}
			
			return testLocation;
		}
		File folder = new File(runLocationCombo.getPath().toOSString());
		if (folder.isAbsolute()) {
			return folder;
		} else {
			return new File(getWorkflowFile().getParent().getLocation().toFile(), folder.getPath());
		}
	}
	
	private void updateRunLocationLabelFromMarker(IFile file) {
		try {			
			runLocationCombo.setInput(file);
		} catch (Exception e) {
			WorkflowEditorPlugin.getDefault().logError("Problem getting run location marker", e);
		}
	}

	public void openNestedInternalWorkflow(WFNode node, String property)
	{
		try {
			NestedChild child = new NestedChild(node, property);
			child.load();
		} catch (CoreException e) {
			WorkflowEditorPlugin.getDefault().log(e.getStatus());
		}
	}
	
	private void addGraphicalViewerToSelectionProvider(ISelectionProviderWithFocusListener provider)
	{
		_selectionProvider.addSelectionProvider(provider);
	}
	
	private void setMainCanvas(Control control)
	{
		if(_mainStack.topControl != control)
		{
			_mainStack.topControl = control;
			_mainComposite.layout();
		}
	}
	
	private class NestedChild
	{
		private final WFNode _parentNode;
		private final String _property;
		private final DiagramEditorInput _input;
		private Link _lnk;
		private Label _arrow;
		private MyDiagramComposite _diagram;
		private ISelectionProviderWithFocusListener _wrappedProvider;
		
		private NestedChild(WFNode parentNode, String property) throws CoreException
		{
			this._parentNode = parentNode;
			this._property = property;
			
			IFolder tmpDir = TempFileManager.getUniqueTempDir();
			IFile tmpFile = tmpDir.getFile(parentNode.getName()+".iwf.tmp");
			
			if(tmpFile.exists())
			{
				tmpFile.delete(false, new NullProgressMonitor());
			}
			
			String fileContents = getContents();
			
			DiagramEditorInput input;
			if(StringUtils.isBlank(fileContents))
			{
				Diagram diagram = Graphiti.getPeCreateService().createDiagram("dartWorkflow", _parentNode.getName(), true);
				URI uri = URI.createPlatformResourceURI(tmpFile.getFullPath().toString(), true);
				FileService.createEmfFileForDiagram(uri, diagram);
				String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
				input = DiagramEditorInput.createEditorInput(diagram, providerId);
			} else {
				InputStream source = new ByteArrayInputStream(fileContents.getBytes());
				tmpFile.create(source, false, new NullProgressMonitor());
				java.net.URI locationURI = tmpFile.getLocationURI();
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(locationURI);
				FileStoreEditorInput fsinput = new FileStoreEditorInput(fileStore);
				input = convertToDiagramEditorInput(fsinput);
			}
			
			this._input = input;
		}
		
		private String getContents()
		{
			return PropertyUtils.getProperty(_parentNode, _property);
		}
		
		private void load()
		{
			if(_diagram == null)
			{
				makeButton();
				
				_diagram = new MyDiagramComposite(_mainComposite, SWT.NONE);
				_diagram.setInput(_input);
				
				// listen for changes
				IDiagramTypeProvider typeProvider = _diagram.getDiagramTypeProvider();
				Diagram diagram = typeProvider.getDiagram();
				Resource resource = diagram.eResource();
				resource.eAdapters().add(new ModificationListener());

				// hook ourselves up as a selection provider
				GraphicalViewer gv = _diagram.getGraphicalViewer();
				_wrappedProvider = new DefaultSelectionProviderWithFocusListener(gv, gv.getControl());
				addGraphicalViewerToSelectionProvider(_wrappedProvider);
				
				_nestedChildrenStack.push(this);
			} else {
				// we're already loaded, so we just need to pop down to us
				while(!_nestedChildrenStack.empty() && _nestedChildrenStack.peek()!=this)
				{
					_nestedChildrenStack.pop().dispose();
				}
			}
			
			// show this one
			setMainCanvas(_diagram);
			updateEditDomainAndPalette(_diagram.getEditDomain(), _diagram.getDiagramBehavior().myGetPaletteRoot(), false);
		}
		
		private void updateEditDomainAndPalette(DefaultEditDomain editDomain, PaletteRoot paletteRoot, boolean clearRoot)
		{
			DefaultEditDomain origEditDomain = getEditDomain();
			if(origEditDomain == editDomain)
			{
				return;
			}
			
			if(clearRoot)
			{
				editDomain.setPaletteRoot(null);
			}
			setEditDomain(editDomain);
			editDomain.setPaletteViewer(origEditDomain.getPaletteViewer());
			editDomain.setPaletteRoot(paletteRoot);
		}
		
		private void dispose()
		{
			if(_wrappedProvider != null)
			{
				_selectionProvider.removeSelectionProvider(_wrappedProvider);
				_wrappedProvider = null;
			}
			
			if(_diagram != null)
			{
				
				if(!_diagram.isDisposed())
				{
					_diagram.dispose();
				}
				_diagram = null;
			}
			
			if(_lnk != null)
			{
				if(!_lnk.isDisposed())
				{
					_lnk.dispose();
				}
				_lnk = null;
			}
			
			if(_arrow != null)
			{
				if(!_arrow.isDisposed())
				{
					_arrow.dispose();
				}
				_arrow = null;
			}
		}
		
		private void makeButton()
		{
			if(!_pathComposite.isVisible())
			{
				showPathComposite(true);
				addTopFileButton();
			}
			
			_arrow = new Label(_pathComposite, SWT.NONE);
			_arrow.setText(">");
			_lnk = new Link(_pathComposite, SWT.NONE);
			_lnk.setText("<a>"+_parentNode.getName()+"</a>");
			_lnk.setToolTipText(_parentNode.getLabel());
			_lnk.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					load();
				}
			});
			
			_pathComposite.getParent().layout(true, true);
		}
		
		private void addTopFileButton()
		{
			Link lnk = new Link(_pathComposite, SWT.NONE);
			
			IFile baseFile = getBaseFile();
			lnk.setText("<a>"+baseFile.getName()+"</a>");
			lnk.setToolTipText("Root Workflow File");
			lnk.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					while(!_nestedChildrenStack.empty())
					{
						_nestedChildrenStack.pop().dispose();
					}
					CompositeUtils.removeChildrenFromComposite(_pathComposite);
					showPathComposite(false);
					setMainCanvas(_rootComposite);
					updateEditDomainAndPalette(_rootEditDomain, getPaletteRoot(), true);
				}
			});
		}
		
		private void showPathComposite(boolean show)
		{
			if(_pathComposite==null || _pathComposite.isDisposed() || _pathComposite.isVisible()==show)
			{
				return;
			}
			
			_pathComposite.setVisible(show);
			Layout layout = _pathComposite.getLayout();
			if(layout instanceof RowLayout)
			{
				RowLayout rowLayout = (RowLayout) layout;
				rowLayout.marginHeight = show ? 3 : 0;
				rowLayout.marginWidth = show ? 3 : 0;
			}
			
			_pathComposite.getParent().layout(true, true);
		}
		
		private class ModificationListener extends AdapterImpl
		{
			private AtomicInteger _retry = new AtomicInteger();
			
			private final Job _saveJob = new Job("Saving to parent") {
				
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						saveToParent();
					} catch(Throwable t) {
						if(_retry.get() > 10)
						{
							String msg = "Unable to save internal nested workflow after 10 tries.";
							return WorkflowEditorPlugin.getDefault().newErrorStatus(msg, t);
						} else {
							String msg = "Error attempting to save internal nested workflow.  Attempt: "+(_retry.get()+1)+".  Will try again...";
							WorkflowEditorPlugin.getDefault().logWarning(msg, t);
						}
						scheduleSave(true);
					}
					return Status.OK_STATUS;
				}
			};
			
			public ModificationListener()
			{
				_saveJob.setSystem(true);
			}
			
			private void scheduleSave(boolean retry)
			{
				if(retry)
				{
					_retry.incrementAndGet();
				} else {
					_retry.set(0);
				}
				_saveJob.schedule(500);
			}
			
			@Override
			public void notifyChanged(Notification msg)
			{
				if(msg.getFeatureID(null) == Resource.RESOURCE__IS_MODIFIED)
				{
					if(msg.getNewBooleanValue())
					{
						scheduleSave(false);
					}
				}
			}
			
			private String getDiagramAsString() throws IOException
			{
				IDiagramTypeProvider typeProvider = _diagram.getDiagramTypeProvider();
				Diagram diagram = typeProvider.getDiagram();
				Resource resource = diagram.eResource();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				resource.save(outputStream, null);
				return outputStream.toString();
			}
			
			private void saveToParent() throws IOException
			{
				String newValue = getDiagramAsString();
				String oldValue = getContents();
				if(StringUtils.equals(newValue, oldValue))
				{
//					System.out.println("  no change");
					return;
				}
				
				TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(_parentNode);

				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					public void doExecute() {
						PropertyUtils.setProperty(_parentNode, _property, newValue);
					}
				});
				getSite().getShell().getDisplay().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						updateDirtyState();
					}
				});
			}
		}
	}
	
	
	private class MyDiagramComposite extends DiagramComposite
	{
		public MyDiagramComposite(Composite parent, int style)
		{
			super(WorkflowDiagramEditor.this, parent, style);
		}
		
		@Override
		protected DiagramBehavior createDiagramBehavior()
		{
			return new MyWorkflowDiagramBehavior(this);
		}
		
		@Override
		public MyWorkflowDiagramBehavior getDiagramBehavior()
		{
			return (MyWorkflowDiagramBehavior) super.getDiagramBehavior();
		}
		
		@Override
		public void configureGraphicalViewer() {
			super.configureGraphicalViewer();
			if (WorkflowEditorPlugin.getDefault().getPreferenceStore().getBoolean(IWorkflowEditorPreferences.CONNECTIONS_BEHIND)) {
				try {
					GraphicalViewer viewer = getGraphicalViewer();
					pushConnectionsToBack(viewer);
					
				} catch (Exception e) {
					// Oh well, something has changed. Connections will be in front.
					WorkflowEditorPlugin.getDefault().logWarning("Unable to move connection layer behind figures in nested workflow", e);
				}
			}		

		}
	}
	
	private static class MyWorkflowDiagramBehavior extends WorkflowDiagramBehavior
	{

		public MyWorkflowDiagramBehavior(IDiagramContainerUI diagramContainer)
		{
			super(diagramContainer);
		}
		
		@Override
		protected void initActionRegistry(ZoomManager zoomManager)
		{
			// NOOP
		}
		
		/**
		 * Overriding for visibility 
		 */
		protected PaletteRoot myGetPaletteRoot()
		{
			return getPaletteRoot();
		}
		
		@Override
		protected void disposeAfterGefDispose()
		{
			IDiagramContainerUI container = getDiagramContainer();
			if(container != null)
			{
				container.setEditDomain(null);
			}
			super.disposeAfterGefDispose();
		}
	}
	
	
	@Override
	public void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		if (WorkflowEditorPlugin.getDefault().getPreferenceStore().getBoolean(IWorkflowEditorPreferences.CONNECTIONS_BEHIND)) {
			try {
				GraphicalViewer viewer = getGraphicalViewer();
				pushConnectionsToBack(viewer);
				
			} catch (Exception e) {
				// Oh well, something has changed. Connections will be in front.
				WorkflowEditorPlugin.getDefault().logWarning("Unable to move connection layer behind figures", e);
			}
		}		
	}

	/**
	 * By putting the connection layer before the primary in the layer stack, we
	 * can make GEF draw connections underneath the other figures. We could do
	 * this by overriding createPrintableLayers in a custom RootEditPane, and
	 * calling setRootEditPart on the GraphicalViewer, but unfortunately Graphiti
	 * already provides one, in an internal class.
	 */

	private static void pushConnectionsToBack(GraphicalViewer viewer)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		// The main layered pane contains several sub-layered panes; the middle one contains the "printable layers."
		// We're just getting that "sub-stack."
		FreeformGraphicalRootEditPart rootEditPart = (FreeformGraphicalRootEditPart) viewer.getRootEditPart();
		Method m = FreeformGraphicalRootEditPart.class.getDeclaredMethod("getPrintableLayers");
		m.setAccessible(true);
		
		LayeredPane pane = (LayeredPane) m.invoke(rootEditPart);
		Layer primary = pane.getLayer(LayerConstants.PRIMARY_LAYER);		
		Layer connections = pane.getLayer(LayerConstants.CONNECTION_LAYER);		
		
		// The reason we're here; move the connection layer in the stack.
		pane.remove(connections);
		pane.addLayerAfter(connections, LayerConstants.CONNECTION_LAYER, primary);
	}
	
	@Override
	protected void createActions() {
		super.createActions();
		// Print action enablement testing causes issues on GTK. Weirdly, although this ticet says it's fixed, it's not:
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=449384
		IAction printAction = getActionRegistry().getAction(ActionFactory.PRINT.getId());
		getActionRegistry().removeAction(printAction);
	}
}
