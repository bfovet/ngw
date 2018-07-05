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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.IExecutionInfo;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.tb.ContextButtonEntry;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.graphiti.tb.IDecorator;

import com.strikewire.snl.apc.util.ExtensionPointUtils;

import gov.sandia.dart.workflow.domain.NamedObject;
import gov.sandia.dart.workflow.domain.ResponseArc;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.features.BringToFrontFeature;
import gov.sandia.dart.workflow.editor.features.DuplicateNodeFeature;
import gov.sandia.dart.workflow.editor.features.ICustomFeatureProvider;
import gov.sandia.dart.workflow.editor.features.OpenReferencedFileFeature;
import gov.sandia.dart.workflow.editor.features.SendToBackFeature;
import gov.sandia.dart.workflow.editor.monitoring.IWorkflowListener;
import gov.sandia.dart.workflow.editor.monitoring.WorkflowMonitor;

public class WorkflowToolBehaviorProvider extends DefaultToolBehaviorProvider implements IWorkflowListener {


	public WorkflowToolBehaviorProvider(IDiagramTypeProvider dtp) {
		super(dtp);
//		System.err.println("new WTBP");
		WorkflowMonitor.addWorkflowListener(this);
	}

	@Override
	public IContextMenuEntry[] getContextMenu(ICustomContext context) {
		PictogramElement[] pics = context.getPictogramElements();
		
		final CustomContext customContext = new CustomContext();
		customContext.setInnerPictogramElement(pics[0]);
		customContext.setPictogramElements(pics);

		List<ContextButtonEntry> buttons = getContextButtons(pics);
		List<IContextMenuEntry> entries = new ArrayList<>();
		for (ContextButtonEntry button: buttons) {
			IFeature feature = button.getFeature();
			ContextMenuEntry item = new ContextMenuEntry(feature, context);
			entries.add(item);
		}

		if (context.getPictogramElements().length == 1) {
			ContextMenuEntry entry = new ContextMenuEntry(null, customContext);
			entry.add(new ContextMenuEntry(new BringToFrontFeature(getFeatureProvider()), customContext));
			entry.add(new ContextMenuEntry(new SendToBackFeature(getFeatureProvider()), customContext));
			entry.setSubmenu(false);
			entries.add(entry);
		}

		return entries.toArray(new IContextMenuEntry[entries.size()]);
	}


	@Override
	public IPaletteCompartmentEntry[] getPalette() {
//		System.err.println("get Pallette");
		return new PaletteBuilder().createPaletteEntries(getFeatureProvider(), getDiagramFile());
	}

	@Override
	public boolean isShowMarqueeTool() {
		return false;
	}
	
	@Override
	public boolean isShowSelectionTool() {
		return false;
	}
	
	@Override
	public boolean isShowFlyoutPalette() {
		return false;
	}

	@Override
	public Object getToolTip(GraphicsAlgorithm ga) {
		PictogramElement pe = ga.getPictogramElement();
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);

		if (bo instanceof WFNode) {
			String type = ((WFNode) bo).getType();
			if (type != null && !type.isEmpty()) {
				return type;
			}
		} else if (bo instanceof NamedObject) {
			String name = ((NamedObject) bo).getName();
			if (name != null && !name.isEmpty()) {
				return name;
			}
		}
		return super.getToolTip(ga);
	}

	@Override
	public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
		IContextButtonPadData data = super.getContextButtonPad(context);
		PictogramElement pe = context.getPictogramElement();		
		setGenericContextButtons(data, pe, CONTEXT_BUTTON_DELETE);

		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
				
		if (bo instanceof WFNode) {
			List<ContextButtonEntry> buttons = getContextButtons(pe);			
			
			data.getDomainSpecificContextButtons().addAll(buttons);
		}

		return data;
	}

	private List<ContextButtonEntry> getContextButtons(PictogramElement... pe) {
		final CustomContext customContext = new CustomContext();
		customContext.setInnerPictogramElement(pe[0]);
		customContext.setPictogramElements(pe);

		IFeatureProvider featureProvider = getFeatureProvider();

		List<IContextButtonContributor> contributors =
				ExtensionPointUtils.getExtensionInstances(WorkflowEditorPlugin.PLUGIN_ID, "contextButtonContributor", "contributor", "class");
		List<ContextButtonEntry> buttons = new ArrayList<>();
		contributors.forEach(c -> buttons.addAll(c.getContextButtons(featureProvider, customContext)));
		return buttons;
	}


	private URI getDiagramFile() {
		return getDiagramTypeProvider().getDiagram().eResource().getURI();		
	};


	@Override
	public IDecorator[] getDecorators(PictogramElement pe) {
		try {
			IFeatureProvider featureProvider = getFeatureProvider();
			Object bo = featureProvider.getBusinessObjectForPictogramElement(pe);
			Map<EObject, IDecorator> validationMarkers = DecoratorManager.getDecoratorMap(getDiagramTypeProvider().getDiagram().eResource());
			IDecorator validationMarker = validationMarkers.get(bo);
			if (validationMarker != null)
				return new IDecorator[] { validationMarker };
			
			return super.getDecorators(pe);
		} catch (Throwable t) {
			t.printStackTrace();
			return super.getDecorators(pe);
		}
	}
	/**
	 * True/False: decorate as running or error
	 * null: no decoration 
	 */
	public Boolean getExecutionStatus(Object bo) {
		if (bo instanceof WFNode) {
			String name = ((WFNode) bo).getName();

			Object status = isExecuting(name, getDiagramFile().lastSegment());

			if (status != null) {
				if (status instanceof Throwable)
					return Boolean.FALSE;
				else
					return Boolean.TRUE;
			}
		} 
		
		return null;
	}

	@Override
	public void postExecute(IExecutionInfo executionInfo) {
		Map<EObject, IDecorator> decorators = DecoratorManager.getDecoratorMap(getDiagramTypeProvider().getDiagram().eResource());
		new WorkflowValidator().validate(executionInfo, decorators);
		getDiagramTypeProvider().getDiagramBehavior().refresh();
	}

	@Override
	public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
		if (context.getPictogramElements().length == 1) {
			final PictogramElement pe = context.getPictogramElements()[0];
			Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
			if (bo instanceof WFNode) {
				initialize();
				WFNode node = (WFNode) bo;
				DoubleClickAction action = doubleClickActions.get(node.getType());
				if(action != null)
				{
					String property = action.getProperty();
					if(property != null)
					{
						ICustomFeature feature = action.getCustomFeature(getFeatureProvider());
						if(feature.canExecute(context))
						{
							return feature;
						}
					}
				}
			} else if (bo instanceof WFArc || bo instanceof ResponseArc) {
				initialize();
				if (arcDoubleClickFeature != null) {
					try {
						Constructor<? extends ICustomFeature> constructor = arcDoubleClickFeature.getConstructor(IFeatureProvider.class);
						ICustomFeature feature = constructor.newInstance(getFeatureProvider());
						if (feature.canExecute(context)) {
							return feature;
						}
					} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						WorkflowEditorPlugin.getDefault().logError("Can't create arc double-click extension", e);
					}	
				}				
			}
		}
		return super.getDoubleClickFeature(context);
	}

	private static Map<String, DoubleClickAction> doubleClickActions = null;
	private static class DoubleClickAction
	{
		private final IConfigurationElement element;
		private final String nodeType;
		private final String property;
		
		private DoubleClickAction(IConfigurationElement element)
		{
			this.element = element;
			
			this.nodeType = element.getAttribute("nodeType");
			this.property = element.getAttribute("property");
		}
		
		public String getNodeType()
		{
			return nodeType;
		}
		
		public String getProperty()
		{
			return property;
		}
		
		public ICustomFeature getCustomFeature(IFeatureProvider fp)
		{
			try {
				Object obj = element.createExecutableExtension("customFeature");
				if(obj instanceof ICustomFeatureProvider)
				{
					ICustomFeatureProvider provider = (ICustomFeatureProvider) obj;
					return provider.createFeature(fp, nodeType, property);
				}
			} catch (Throwable t) {
				WorkflowEditorPlugin.getDefault().logError("Error generating custom feature for double click action", t);
			}
			
			return new OpenReferencedFileFeature(fp, nodeType, property);
		}
	}
	private static Class<? extends ICustomFeature> arcDoubleClickFeature = null;
	private synchronized static void initialize() {
		if (doubleClickActions == null) {
			doubleClickActions = new ConcurrentHashMap<>();
			List<IConfigurationElement> elements =
					ExtensionPointUtils.getConfigurationElements(WorkflowEditorPlugin.PLUGIN_ID, "mouseAction", "doubleClick");
			for (IConfigurationElement element : elements) {
				DoubleClickAction action = new DoubleClickAction(element);
				doubleClickActions.put(action.getNodeType(), action);
			}
			elements =
					ExtensionPointUtils.getConfigurationElements(WorkflowEditorPlugin.PLUGIN_ID, "mouseAction", "arcDoubleClick");
			for (IConfigurationElement element : elements) {
				String feature = element.getAttribute("feature");				
				try {
					arcDoubleClickFeature = (Class<? extends ICustomFeature>) Platform.getBundle(element.getContributor().getName()).loadClass(feature);
				} catch (ClassNotFoundException e) {
					WorkflowEditorPlugin.getDefault().logError("Can't load arc double-click extension", e);
				}
			}
		}				
	}

	public DuplicateNodeFeature getDuplicateNodeFeature(ICustomContext context) {
		return new DuplicateNodeFeature(getFeatureProvider());
	}
	
	@Override
	public void dispose() {
		WorkflowMonitor.removeWorkflowListener(this);
	}

	private Map<String, Map<String, Object>> executingNodes = Collections.synchronizedMap(new HashMap<>());
	
	@Override
	public void nodeEntered(String name, String workflow) {
		addExecutingNode(name, workflow);
		// TODO Maybe need a DelayRunner here?
		getDiagramTypeProvider().getDiagramBehavior().refresh();
	}

	@Override
	public void nodeExited(String name, String workflow) {
		removeExecutingNode(name, workflow);
		getDiagramTypeProvider().getDiagramBehavior().refresh();
	}
	
	@Override
	public void nodeAborted(String name, String workflow, Throwable t) {
		// TODO Put the error somewhere
		addAbortedNode(name, workflow, t);
		getDiagramTypeProvider().getDiagramBehavior().refresh();
	}
	
	@Override
	public void workflowStopped(String workflow) {
		// executingNodes.remove(workflow);
		getDiagramTypeProvider().getDiagramBehavior().refresh();
	}
	
	@Override
	public void workflowStarted(String workflow) {
		clearExecutingNodes(workflow);
		getDiagramTypeProvider().getDiagramBehavior().refresh();
	}


	private void addExecutingNode(String name, String workflow) {
		synchronized (executingNodes) {
			Map<String, Object> nodes = executingNodes.get(workflow);
			if (nodes == null) {
				executingNodes.put(workflow, nodes = new HashMap<>());
			}
			nodes.put(name, name);
		}
	}
	
	private void addAbortedNode(String name, String workflow, Throwable t) {
		synchronized (executingNodes) {
			Map<String, Object> nodes = executingNodes.get(workflow);
			if (nodes == null) {
				executingNodes.put(workflow, nodes = new HashMap<>());
			}
			nodes.put(name, t);
		}
	}


	private void removeExecutingNode(String name, String workflow) {
		synchronized (executingNodes) {
			Map<String, Object> nodes = executingNodes.get(workflow);
			if (nodes != null)
				nodes.remove(name);
		}
	}
	
	private void clearExecutingNodes(String workflow) {
		synchronized (executingNodes) {
			Map<String, Object> nodes = executingNodes.get(workflow);
			if (nodes != null)
				nodes.clear();
		}
	}


	private Object isExecuting(String name, String workflow) {
		synchronized (executingNodes) {
			Map<String, Object> nodes = executingNodes.get(workflow);
			if (nodes == null || !nodes.containsKey(name))
				return null;
			else
				return nodes.get(name);
		}
	}

}
