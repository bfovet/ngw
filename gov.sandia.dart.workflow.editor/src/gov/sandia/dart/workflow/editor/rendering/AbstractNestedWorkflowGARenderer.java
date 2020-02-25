/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.rendering;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.preferences.IWorkflowEditorPreferences;

public class AbstractNestedWorkflowGARenderer extends GenericWFNodeGARenderer {
	static int renderCount = 0;
	public AbstractNestedWorkflowGARenderer() {
		super();
	}
	private static Set<IFile> rendersInProgress = Collections.newSetFromMap(new ConcurrentHashMap<IFile, Boolean>());
	private static Map<IFile, ImageDescriptor> imageCache = Collections.synchronizedMap(new PassiveExpiringMap<>(10000));
	protected void renderWorkflow(WFNode node, IFile file, Graphics g) {

		if (!WorkflowEditorPlugin.getDefault().getPreferenceStore().getBoolean(IWorkflowEditorPreferences.RENDER_NESTED)) {
			return;

		} else if (!rendersInProgress.add(file)) {
			return;			
		}
		try {
			ImageDescriptor desc = imageCache.get(file);
			if (desc == null ) {
				URI diagramFileUri = GraphitiUiInternal.getEmfService().getFileURI(file);

				// the file's first base node has to be a diagram
				URI diagramUri = GraphitiUiInternal.getEmfService().mapDiagramFileUriToDiagramUri(diagramFileUri);

				// Get the default resource set to hold the new resource
				ResourceSet resourceSet = new ResourceSetImpl();
				TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(resourceSet);
				try {
					if (editingDomain == null) {
						// Not yet existing, create one
						editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
					}

					Resource resource = editingDomain.getResourceSet().getResource(diagramFileUri, false);
					if (resource == null) {
						resource = editingDomain.getResourceSet().getResource(diagramFileUri, true);
					}

					EObject eObject = editingDomain.getResourceSet().getEObject(diagramUri, false);
					if (!(eObject instanceof Diagram)) {
						return;
					}

					Diagram diagram = (Diagram) eObject;
					byte[] bytes = GraphitiUi.getImageService().convertDiagramToBytes(diagram, SWT.IMAGE_PNG);
					ImageData data = new ImageData(new ByteArrayInputStream(bytes));
					int whitePixel = data.palette.getPixel(new RGB(255,255,255));
					data.transparentPixel = whitePixel;

					desc = ImageDescriptor.createFromImageDataProvider(new ImageDataProvider() {
						@Override
						public ImageData getImageData(int zoom) {
							return data;
						}
					}); 
										
					imageCache.put(file, desc);
				} finally {
					// Dispose the editing domain to eliminate memory leak
					if (editingDomain != null)
						editingDomain.dispose();
				}
			}
			Image srcImage = desc.createImage();
			try {
				g.drawImage(srcImage, getImageBounds(srcImage), getRenderBounds(node, g));
			} finally {
				srcImage.dispose();
			}

		} finally {		
			rendersInProgress.remove(file);
		}
	}

	Rectangle getImageBounds(Image srcImage) {
		return new Rectangle(srcImage.getBounds());
	}

	protected Rectangle getRenderBounds(WFNode node, Graphics g) {
		IPreferenceStore store = WorkflowEditorPlugin.getDefault().getPreferenceStore();
		if (!store.getBoolean(IWorkflowEditorPreferences.RENDER_NESTED_INDENTED) || !store.getBoolean(IWorkflowEditorPreferences.PORT_LABELS))
			return getInnerBounds();

		Font f = WorkflowEditorPlugin.getDefault().getDiagramFont();
		g.setFont(f);
		int left = leftIndent(node, g);
		int right = rightIndent(node, g);
		if (left > right)
			left = right;
		if (right < left)
			right = left;
		
		Rectangle r = getInnerBounds().getCopy();
		r.x = r.x + left + 10;
		r.width = Math.max(0, r.width - right - left - 20);
		r.y = r.y + 20;
		r.height = r.height - 40;
		return r;
	}
	
	private int rightIndent(WFNode node, Graphics g) {
		TextLayout tl = new TextLayout(Display.getCurrent());
		tl.setFont(g.getFont());
		int max = 0;
		for (OutputPort port: node.getOutputPorts()) {
			tl.setText(port.getName());
			max = Math.max(tl.getLineBounds(0).width, max);			
		}
		return max;
	}

	private int leftIndent(WFNode node, Graphics g) {
		TextLayout tl = new TextLayout(Display.getCurrent());
		tl.setFont(g.getFont());
		int max = 0;
		for (InputPort port: node.getInputPorts()) {
			tl.setText(port.getName());
			max = Math.max(tl.getLineBounds(0).width, max);			
		}
		return max;
	}

	protected IContainer getDiagramFolder() {
		URI uri = fp.getDiagramTypeProvider().getDiagram().eResource().getURI();
		String pathString = uri.toPlatformString(true);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pathString));
		return file.getParent();
	}

}
