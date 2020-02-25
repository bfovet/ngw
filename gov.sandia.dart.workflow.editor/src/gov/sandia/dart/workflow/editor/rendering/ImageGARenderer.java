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

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRenderer;
import org.eclipse.graphiti.platform.ga.IRendererContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;

import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

public class ImageGARenderer extends AbstractGARenderer implements IGraphicsAlgorithmRenderer {
	public static final String ID = "wfimage";
	
	public ImageGARenderer(IRendererContext rc, IFeatureProvider fp) {
		setRc(rc);
		setFp(fp);
	}

	@Override
	protected void fillShape(Graphics g) {
		Rectangle r = getInnerBounds();

		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();
		gov.sandia.dart.workflow.domain.Image image = (gov.sandia.dart.workflow.domain.Image) fp.getBusinessObjectForPictogramElement(pe);

		ImageDescriptor imageDescriptor = null;
		File imageFile = new File(image.getText());
		if (!imageFile.isAbsolute()) {
			imageFile = new File(getWorkflowFile(image).getParent().getLocation().toFile(), image.getText());
		}
		if (imageFile.exists() && imageFile.isFile()) {
			try {
				final File fImageFile = imageFile;
				imageDescriptor = ImageDescriptor.createFromImageDataProvider(new ImageDataProvider() {
					private ImageData data = new ImageData(fImageFile.getAbsolutePath());
					@Override
					public ImageData getImageData(int zoom) {
						return data;
					}
				}); 
			} catch (Exception e) {
				WorkflowEditorPlugin.getDefault().logError("Error trying to get image from " + imageFile.getAbsolutePath(), e);
			}
		}
		if (imageDescriptor == null) {
			imageDescriptor = WorkflowEditorPlugin.getImageDescriptor("/icons/fail.png");
		}
		if (imageDescriptor != null) {			
			// TODO We really should be caching these.
			Image srcImage = imageDescriptor.createImage();
			try {
				if (image.isZoomToFit())
					g.drawImage(srcImage, getBounds(srcImage), r);
				else
					g.drawImage(srcImage, r.x, r.y);
			} finally {
				srcImage.dispose();
			}
		}
	}

	private Rectangle getBounds(Image srcImage) {
		return new Rectangle(srcImage.getBounds());
	}
	
	@Override
	protected void outlineShape(Graphics g) {	
		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();
		gov.sandia.dart.workflow.domain.Image image = (gov.sandia.dart.workflow.domain.Image) fp.getBusinessObjectForPictogramElement(pe);
		if (image.isDrawBorder()) {
			Rectangle r = getInnerBounds();
			int[] poly = { r.x, r.y, r.x + r.width, r.y, r.x + r.width, r.y + r.height, r.x, r.y + r.height, r.x, r.y, r.x, r.y, r.x, r.y, r.x, r.y };
			g.setForegroundColor(ColorConstants.lightGray);
			g.setLineStyle(Graphics.LINE_SOLID);
			g.setLineWidth(1);
			g.drawPolygon(poly);
		}
	}
	
	private IFile getWorkflowFile(gov.sandia.dart.workflow.domain.Image image) {
		URI uri = image.eResource().getURI();
		String pathString = uri.toPlatformString(true);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pathString));
		return file;
	}

}
