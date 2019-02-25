/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.rendering;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRenderer;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRendererFactory;
import org.eclipse.graphiti.platform.ga.IRendererContext;

import com.strikewire.snl.apc.util.ExtensionPointUtils;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

public class GARendererFactory implements IGraphicsAlgorithmRendererFactory {

	private IFeatureProvider fp;

	static Map<String, Class<? extends AbstractGARenderer>> renderers = new ConcurrentHashMap<>();
	
	public GARendererFactory(IFeatureProvider fp) {
		this.fp = fp;
	}
	
	@Override
	public IGraphicsAlgorithmRenderer createGraphicsAlgorithmRenderer(IRendererContext rc) {		
		String id = rc.getPlatformGraphicsAlgorithm().getId();
		if (GenericWFNodeGARenderer.ID.equals(id)) {
			initialize();
			PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();
			Object bo = fp.getBusinessObjectForPictogramElement(pe);
			if (bo instanceof WFNode) {
				Class<? extends AbstractGARenderer> clazz = renderers.get(((WFNode)bo).getType());
				AbstractGARenderer r = null;
				if (clazz != null) {
					try {
						r = clazz.newInstance();
					} catch (Exception e) {
						WorkflowEditorPlugin.getDefault().logError("Error creating node renderer", e);
					}
				} 
				if (r == null)
					r = new GenericWFNodeGARenderer();
				r.setRc(rc);
				r.setFp(fp);
				return r;
			}
		} else if (PortGARenderer.ID.equals(id)) {
			return new PortGARenderer(rc, fp);
		} else if (NoteGARenderer.ID.equals(id)) {
			return new NoteGARenderer(rc, fp);
		} else if (ImageGARenderer.ID.equals(id)) {
			return new ImageGARenderer(rc, fp);
		} else if (ResponseGARenderer.ID.equals(id)) {
			return new ResponseGARenderer(rc, fp);
		} 
		return null;
  }

	private static void initialize() {
		if (renderers.isEmpty()) {
			List<IConfigurationElement> ces =
					ExtensionPointUtils.getConfigurationElements(WorkflowEditorPlugin.PLUGIN_ID, "nodeRenderer", "customRenderer");
			for (IConfigurationElement ce: ces) {
				try {
					// TODO Do this using a rendererfactory instead?
					String nodeType = ce.getAttribute("nodeType");
					AbstractGARenderer renderer = (AbstractGARenderer) ce.createExecutableExtension("class");
					renderers.put(nodeType, renderer.getClass());
					
				} catch (Exception e) {
					WorkflowEditorPlugin.getDefault().logError("Error initializing node renderers", e);
				}
			}
			
			

		}
	}
}
