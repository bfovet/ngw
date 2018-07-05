/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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
import java.util.List;
import java.util.Stack;
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
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.views.palette.PaletteView;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.examples.common.FileService;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramComposite;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.editor.IDiagramContainerUI;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.strikewire.snl.apc.GUIs.CompositeUtils;
import com.strikewire.snl.apc.GUIs.browsers.CommonFileSelectionDialog;
import com.strikewire.snl.apc.selection.DefaultSelectionProviderWithFocusListener;
import com.strikewire.snl.apc.selection.ISelectionProviderWithFocusListener;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;
import com.strikewire.snl.apc.temp.TempFileManager;
import com.strikewire.snl.apc.util.ResourceUtils;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.util.PropertyUtils;
import gov.sandia.dart.workflow.util.WorkflowUtils;

public class WorkflowDiagramEditor extends DiagramEditor implements IPropertyChangeListener  {

	public static final String ID = "gov.sandia.dart.workflow.editor.WorkflowDiagramEditor";
	private Label iconLabel;
	private Label messageLabel;
	private ComboViewer runLocationCombo;
	private AtomicReference<IFile> _baseFile = new AtomicReference<>();
	private Composite _pathComposite;
	private Composite _mainComposite;
	private StackLayout _mainStack;
	private Composite _rootComposite;
	private DefaultEditDomain _rootEditDomain;
	
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
	public void createPartControl(Composite parent)
	{
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

		Composite statusLine = new Composite(contents, SWT.NONE);
		statusLine.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		statusLine.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		GridLayout statusLayout = new GridLayout(3, false);
		statusLayout.verticalSpacing = 0;
		statusLine.setLayout(statusLayout);		

		iconLabel = new Label(statusLine, SWT.NONE);
		iconLabel.setImage(getImage("icons/white_square.png"));		
		iconLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		iconLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		messageLabel = new Label(statusLine, SWT.NONE);
		messageLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		CCombo combo = new CCombo(statusLine, SWT.NONE);
		runLocationCombo = new ComboViewer(combo);
		combo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));			
		runLocationCombo.setContentProvider(new ArrayContentProvider());
		runLocationCombo.setLabelProvider(new DatedPathLabelProvider());
		
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
		
		_rootComposite = new Composite(_mainComposite, SWT.NONE);
		_rootComposite.setLayout(new FillLayout());
		
		_mainStack.topControl = _rootComposite;
		
		super.createPartControl(_rootComposite);
		
		IFile file = getWorkflowFile();
		if (file != null) {
			updateRunLocationLabelFromMarker(file);	
		}
		
	}
			
	private Image getImage(String path) {
		ImageRegistry registry = WorkflowEditorPlugin.getDefault().getImageRegistry();
		Image image = registry.get(path);
		if (null == image) {
			ImageDescriptor desc = WorkflowEditorPlugin.getImageDescriptor(path); 
			registry.put(path, desc);
			image = registry.get(path);
		}
		return image;
	}
	
	public void clearStatus() {
		iconLabel.setImage(null);
		messageLabel.setText("");
	}
	
	public void setStatus(String lastNode, boolean success) {
		String icon = "icons/blue_ball.png";
		String message = "Unknown";
		if (success) {
			icon = "icons/green_ball_check.png";
			message = "Workflow completed successfully";
		} else if (!success) {
			icon = "icons/green_ball.png";
			if (lastNode == null)
				message = "Workflow executing";
			else
				message = "Workflow executing node " + lastNode;		
		}

		iconLabel.setImage(getImage(icon));
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
	}

	private IFile getWorkflowFile() {
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

	void openPaletteView() {
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
		WorkflowEditorPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
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
			return getWorkflowFile().getParent().getLocation().toFile();
		}
		File folder = new File(runLocationCombo.getCCombo().getText());
		if (folder.isAbsolute()) {
			return folder;
		} else {
			return new File(getWorkflowFile().getParent().getLocation().toFile(), folder.getPath());
		}
	}
	
	private void updateRunLocationLabelFromMarker(IFile file) {
		try {
			
			List<DatedPath> content = WorkflowUtils.getRunLocationMarkers(file);
			
			runLocationCombo.setInput(content);
			if (content.size() > 0) {
				runLocationCombo.getCCombo().select(0);
			}
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
		private MyDiagramComposite _diagram;
		private ISelectionProviderWithFocusListener _wrappedProvider;
		
		private NestedChild(WFNode parentNode, String property) throws CoreException
		{
			this._parentNode = parentNode;
			this._property = property;
			
			// TODO unique temp dir/file names...
			IFolder tmpDir = TempFileManager.getUniqueTempDir();
			IFile tmpFile = tmpDir.getFile("tmp.wf.file");
			
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
		}
		
		private void makeButton()
		{
			if(!_pathComposite.isVisible())
			{
				showPathComposite(true);
				addTopFileButton();
			}
			
			Label arrow = new Label(_pathComposite, SWT.NONE);
			arrow.setText(">");
			_lnk = new Link(_pathComposite, SWT.NONE);
			_lnk.setText("<a>"+_parentNode.getName()+"</a>");
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
			private volatile int _retry = 0;
			
			private final Job _saveJob = new Job("Saving to parent") {
				
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						saveToParent();
					} catch(Throwable t) {
						if(_retry > 10)
						{
							String msg = "Unable to save internal nested workflow after 10 tries.";
							return WorkflowEditorPlugin.getDefault().newErrorStatus(msg, t);
						} else {
							String msg = "Error attempting to save internal nested workflow.  Attemp: "+(_retry+1)+".  Will try again...";
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
					_retry++;
				} else {
					_retry = 0;
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
	
}
