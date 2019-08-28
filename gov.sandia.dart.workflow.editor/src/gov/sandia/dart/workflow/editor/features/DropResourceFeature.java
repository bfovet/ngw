/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.features;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.Image;
import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.Property;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.configuration.NodeType;
import gov.sandia.dart.workflow.editor.configuration.WorkflowTypesManager;
import gov.sandia.dart.workflow.editor.packaging.PackageComponentWizard;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class DropResourceFeature extends AbstractAddFeature implements IAddFeature {

	private static final String FILE = "file";
	private static final String FOLDER = "folder";

	private static final String NESTED_WORKFLOW = "nestedWorkflow";
	private static final String PYTHON_SCRIPT = "pythonScript";

	private static final String FILE_NAME = "fileName";
	private static final String FOLDER_NAME = "folderName";

	private IResource resource;
	private ContainerShape targetContainer;

	public DropResourceFeature(IResource resource, ContainerShape targetContainer, IFeatureProvider workflowFeatureProvider) {
		super(workflowFeatureProvider);
		this.resource = resource;
		this.targetContainer = targetContainer;
	}

	@Override
	public boolean canAdd(IAddContext context) {
		return canDropFileOnDiagram() || canDropFileOnANode();
	}

	private boolean canDropFileOnANode() {
		if (resource instanceof IFile) {
		  if (targetContainer instanceof PictogramElement) {
			  Object bo = getBusinessObjectForPictogramElement(targetContainer);
			  if (bo instanceof WFNode) {
				  return true;
			  }
		  }
		}
		return false;
	}

	private boolean canDropFileOnDiagram() {
		return (resource instanceof IFile || resource instanceof IFolder && targetContainer instanceof Diagram);
	}

	@Override
	public PictogramElement add(IAddContext context) {
		if (canDropFileOnDiagram())
			return doDropFileOnDiagram(context);
		else if (canDropFileOnANode())
			return doDropFileOnANode(context);
		else
			return null;
	}

	private PictogramElement doDropFileOnANode(IAddContext context) {
		WFNode node = (WFNode) getBusinessObjectForPictogramElement(targetContainer);
		// TODO Use path relative to workflow file?
		if (PropertyUtils.hasProperty(node, FILE_NAME)) {
			PropertyUtils.setProperty(node, FILE_NAME, getFilePath());
			
		} else {		
			String name = getUniqueFilePropertyName(node);
			Property prop = DomainFactory.eINSTANCE.createProperty();
			prop.setName(name);
			prop.setValue(getFilePath());			
			prop.setType("home_file"); 
			node.getProperties().add(prop);
			node.eResource().getContents().add(prop);
		}

		return targetContainer;
	}

	private String getUniqueFilePropertyName(WFNode node) {
		int counter = 1;
		while (PropertyUtils.getProperty(node, "file" + counter) != null)
			counter++;
		return "file" + counter;
	}

	// TODO This doesn't give the right answer for nested internal workflows.
	protected String getFilePath() {
		try {
			java.nio.file.Path iwfPath = getWorkflowFile().getLocation().toFile().getParentFile().toPath();		
			java.nio.file.Path resourcePath = resource.getLocation().toFile().toPath();
			java.nio.file.Path relativeResourcePath = iwfPath.relativize(resourcePath);
			
			return relativeResourcePath.toString().replaceAll("\\\\", "/");
		} catch (Exception e) {
			return resource.getName();
		}
	}

	protected PictogramElement doDropFileOnDiagram(IAddContext context) {
		if (resource instanceof IFile) {
			JarInputStream jis = isComponentJar();
			if (jis != null) {			
				return doAddComponentFromJar(context, jis);
			} 
			String extension = FilenameUtils.getExtension(resource.getName()).toLowerCase();
			
			if (resource instanceof IFile && extension.equals("iwf")){
				return doAddNestedWorkflowComponent(context);
			} else if (resource instanceof IFile && extension.equals("py")){
				return doAddPythonComponent(context);
			} else if (resource instanceof IFile && isImageFile(extension)){
			 		return doAddImageComponent(context);
			} else {
				return doAddFileComponent(context);
			}
		} else {
			return doAddFolderComponent(context);
		}
	}

	private PictogramElement doAddImageComponent(IAddContext context) {
		CreateContext createContext = new CreateContext();
		createContext.setTargetContainer(context.getTargetContainer());
		createContext.setLocation(context.getX(), context.getY());
		createContext.setSize(180, 180);

		String filePath = getFilePath();
		InputStream contents = null;

		try {
			contents = ((IFile) resource).getContents();
			org.eclipse.swt.graphics.Image sample = 
				new org.eclipse.swt.graphics.Image(Display.getCurrent(), contents);
			Rectangle bounds = sample.getBounds();
			createContext.setSize(bounds.width, bounds.height);
			sample.dispose();			
		} catch (Exception ex) {
			// Apparently not an image? Just don't worry about it, won't render and user can fix later
		}  finally {
			if (contents != null)
				try { contents.close(); } catch (IOException ioe) {}
		}
		
		CreateImageFeature cf = new CreateImageFeature(getFeatureProvider());
		
		Image image  = (Image) cf.create(createContext)[0];	

		image.setText(filePath);
		image.setDrawBorder(false);
		image.setZoomToFit(true);
		return targetContainer;
	}
	
	private static Set<String> IMAGES = new HashSet<>(Arrays.asList("png", "jpg", "jpeg", "bmp", "gif"));
	private boolean isImageFile(String extension) {
		return IMAGES.contains(extension);
	}

	private PictogramElement doAddPythonComponent(IAddContext context) {
		CreateContext createContext = new CreateContext();
		createContext.setTargetContainer(context.getTargetContainer());
		createContext.setLocation(context.getX(), context.getY());
		createContext.setSize(170, 70);
		CreateWFNodeFeature cf =
				new CreateWFNodeFeature(getFeatureProvider(),
						WorkflowTypesManager.get().getNodeType(FILE));
		String filePath = getFilePath();
		File file = new File(filePath);
		String name = file.getName().replaceAll("\\s+", "_").replaceAll("\\.", "_");
		WFNode fileNode  = (WFNode) cf.create(createContext)[0];	
		fileNode.setLabel(file.getName());	
		fileNode.setName(name);
		PropertyUtils.setProperty(fileNode, FILE_NAME, filePath);			

		PictogramElement pe = getFeatureProvider().getPictogramElementForBusinessObject(fileNode);
		int width = pe.getGraphicsAlgorithm().getWidth();
		createContext = new CreateContext();
		createContext.setTargetContainer(context.getTargetContainer());
		createContext.setLocation(context.getX() + width + 50, context.getY());
		createContext.setSize(context.getWidth(), context.getHeight());
		cf = new CreateWFNodeFeature(getFeatureProvider(),
						WorkflowTypesManager.get().getNodeType(PYTHON_SCRIPT));
		WFNode scriptNode  = (WFNode) cf.create(createContext)[0];	
		
		
		CreateConnectionContext ccc = new CreateConnectionContext();
		ccc.setSourceAnchor(getOutputPort(fileNode, "fileReference"));
		ccc.setTargetAnchor(getInputPort(scriptNode, "script"));
		ICreateConnectionFeature ccf = new CreateArcFeature(getFeatureProvider());
		ccf.create(ccc);
		
		return targetContainer;
	}

	private Anchor getInputPort(WFNode scriptNode, String string) {
		for (InputPort port: scriptNode.getInputPorts())
			if (port.getName().equals(string))
				return (Anchor) getFeatureProvider().getPictogramElementForBusinessObject(port);
		return null;
	}

	private Anchor getOutputPort(WFNode fileNode, String string) {
		for (OutputPort port: fileNode.getOutputPorts())
			if (port.getName().equals(string))
				return (Anchor) getFeatureProvider().getPictogramElementForBusinessObject(port);
		return null;
	}

	// TODO Error checking/handling
	private PictogramElement doAddComponentFromJar(IAddContext context, JarInputStream jis) {
		try {
			CreateContext createContext = new CreateContext();
			createContext.setTargetContainer(context.getTargetContainer());
			createContext.setLocation(context.getX(), context.getY());
			createContext.setSize(context.getWidth(), context.getHeight());
						
			Manifest manifest = jis.getManifest();
			Set<String> nodeTypePath = new HashSet<>();
			nodeTypePath.addAll(Arrays.asList(String.valueOf(manifest.getMainAttributes().get(PackageComponentWizard.WORKFLOW_COMPONENT)).split(",")));
			Set<String> filePaths = getFiles(manifest);

			IFile wfFile = getWorkflowFile();
			File saveLocation = wfFile.getLocation().removeLastSegments(1).toFile();
			
			JarEntry entry;
			while ((entry = jis.getNextJarEntry()) != null) {
				String path = entry.getName();
				
				if (filePaths.contains(path)) {
					try (FileOutputStream fos = new FileOutputStream(new File(saveLocation, path))) {
						IOUtils.copy(jis, fos);
					}
				} else if (nodeTypePath.contains(path)) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();		               
					IOUtils.copy(jis, baos);
					List<NodeType> nt = WorkflowTypesManager.parseNodeTypes(new ByteArrayInputStream(baos.toByteArray()));
					CreateWFNodeFeature cf =
							new CreateWFNodeFeature(getFeatureProvider(), nt.get(0));
					cf.create(createContext);	

				}
				new Job("Refresh") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							wfFile.getParent().refreshLocal(1, monitor);
						} catch (CoreException e) {
							// Whatever
						}
						return Status.OK_STATUS;
					}					
				}.schedule();
			}
			
		} catch (Exception e) {
			// We're doing a drop -- bad stuff happens if we throw
			WorkflowEditorPlugin.getDefault().logError(e);
		} finally {
			IOUtils.closeQuietly(jis);			
		}
		return targetContainer;
	}

	private Set<String> getFiles(Manifest manifest) {
		Set<String> paths = new HashSet<>();
		Object value = manifest.getMainAttributes().get(PackageComponentWizard.WORKFLOW_FILES);
		if (value != null) {
			String[] strings = ((String) value).split(",");
			paths.addAll(Arrays.asList(strings));
		}
		return paths;
	}

	private JarInputStream isComponentJar() {
		if (! (resource instanceof IFile) || !"jar".equalsIgnoreCase(resource.getFileExtension()))
			return null;
		try  {
			JarInputStream jis = new JarInputStream(((IFile) resource).getContents());
			Manifest manifest = jis.getManifest();
			if (manifest != null && manifest.getMainAttributes().containsKey(PackageComponentWizard.WORKFLOW_PACKAGE_VERSION))
				return jis;
			else
				jis.close();
		} catch (IOException | CoreException e) {
			WorkflowEditorPlugin.getDefault().logError(e);
		}
		
		return null;
	}

	private PictogramElement doAddFileComponent(IAddContext context) {
		CreateContext createContext = new CreateContext();
		createContext.setTargetContainer(context.getTargetContainer());
		createContext.setLocation(context.getX(), context.getY());
		createContext.setSize(170, 70);
		CreateWFNodeFeature cf =
				new CreateWFNodeFeature(getFeatureProvider(),
						WorkflowTypesManager.get().getNodeType(FILE));
		String filePath = getFilePath();
		File file = new File(filePath);
		String name = file.getName().replaceAll("\\s+", "_").replaceAll("\\.", "_");
		WFNode node  = (WFNode) cf.create(createContext)[0];	
		node.setLabel(file.getName());	
		node.setName(name);
		PropertyUtils.setProperty(node, FILE_NAME, filePath);			
		return getFeatureProvider().getPictogramElementForBusinessObject(node);
	}
	
	private PictogramElement doAddFolderComponent(IAddContext context) {
		CreateContext createContext = new CreateContext();
		createContext.setTargetContainer(context.getTargetContainer());
		createContext.setLocation(context.getX(), context.getY());
		createContext.setSize(170, 70);
		CreateWFNodeFeature cf =
				new CreateWFNodeFeature(getFeatureProvider(),
						WorkflowTypesManager.get().getNodeType(FOLDER));
		String filePath = getFilePath();
		File file = new File(filePath);
		String name = file.getName().replaceAll("\\s+", "_").replaceAll("\\.", "_");
		WFNode node  = (WFNode) cf.create(createContext)[0];	
		node.setLabel(file.getName());	
		node.setName(name);
		PropertyUtils.setProperty(node, FOLDER_NAME, filePath);			
		return getFeatureProvider().getPictogramElementForBusinessObject(node);
	}

	
	private PictogramElement doAddNestedWorkflowComponent(IAddContext context) {
		CreateContext createContext = new CreateContext();
		createContext.setTargetContainer(context.getTargetContainer());
		createContext.setLocation(context.getX(), context.getY());
		createContext.setSize(context.getWidth(), context.getHeight());
		CreateWFNodeFeature cf =
				new CreateWFNodeFeature(getFeatureProvider(),
						WorkflowTypesManager.get().getNodeType(NESTED_WORKFLOW));
		WFNode node  = (WFNode) cf.create(createContext)[0];	
		node.setLabel("Run " + resource.getName());
		PropertyUtils.setProperty(node, FILE_NAME, getFilePath());		
		
		List<PictogramElement> pes = Graphiti.getLinkService().getPictogramElements(getDiagram(), node);
		if (pes.size() > 0) {
			PictogramElement pe = pes.get(0);
			CustomContext cc = new CustomContext();
			cc.setPictogramElements(new PictogramElement[] {pe});	
			cc.setInnerPictogramElement(pe);
			new DefinePortsFromNestedWorkflowFeature(getFeatureProvider(), "nestedWorkflow", "fileName").execute(cc);
			return pe;
		}
		
		return getFeatureProvider().getPictogramElementForBusinessObject(node);
	}

	
	private IFile getWorkflowFile() {
		URI uri = getDiagram().eResource().getURI();
		String pathString = uri.toPlatformString(true);
		IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pathString));
		return ifile;
	}
	
	@Override
	public boolean hasDoneChanges() {
		return true;
	}
}
