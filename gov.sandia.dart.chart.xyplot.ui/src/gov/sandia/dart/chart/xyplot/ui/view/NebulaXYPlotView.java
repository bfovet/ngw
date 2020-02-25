/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.chart.xyplot.ui.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.nebula.visualization.xygraph.dataprovider.AbstractDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.Sample;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.TraceType;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.strikewire.snl.apc.osgi.util.EventKeys;
import com.strikewire.snl.apc.osgi.util.OSGIUtils;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;

import gov.sandia.dart.chart.xyplot.expression.Function;
import gov.sandia.dart.chart.xyplot.expression.HistogramFunction;
import gov.sandia.dart.chart.xyplot.expression.ScatterFunction;
import gov.sandia.dart.chart.xyplot.ui.Activator;
import gov.sandia.dart.chart.xyplot.ui.PreferenceInitializer;

public class NebulaXYPlotView extends ViewPart implements ISelectionListener, ISelectionChangedListener {

	public static final String VIEW_ID = "gov.sandia.dart.chart.xyplot.ui.view.XYPlotView";
	private static final int NPOINTS = 250;
	private XYGraphFigure graphFigure;
	private List<Trace> traces = new ArrayList<>();
	private boolean isLinked;

	final private Action linkAction = new Action("Toolbar", IAction.AS_CHECK_BOX) {
		@Override
		public void run() {
			isLinked = !isLinked;
			Activator.getDefault().getPreferenceStore().setValue(PreferenceInitializer.LINKING_ENABLED, isLinked);
		}
	};

	public NebulaXYPlotView() {
		isLinked = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceInitializer.LINKING_ENABLED);
		initializeActions();
	}

	
	private void initializeActions() {
		linkAction.setText("Link with editor view");
		ImageDescriptor linkImageDesc = ImageDescriptor.createFromURL(Platform
				.getBundle("gov.sandia.dart.chart.xyplot.ui").getEntry(
						"/icons/synced.gif"));
		linkAction.setImageDescriptor(linkImageDesc);
		linkAction.setChecked(isLinked);
	}
	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Canvas container = new Canvas(parent, SWT.NONE);
		final LightweightSystem lws = new LightweightSystem(container);
		graphFigure = new XYGraphFigure();
		lws.setContents(graphFigure);
		
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
		MultiControlSelectionProvider selectionProvider = new MultiControlSelectionProvider();
		getSite().setSelectionProvider(selectionProvider);
		
		contributeToActionBars();

	}

	private void contributeToActionBars() {
		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		manager.add(linkAction);
	}

	@Override
	public void setFocus() {
		if (graphFigure != null)
			graphFigure.xyGraph.requestFocus();
	}

	public void resetPlots() {
		for (Iterator<Trace> it = traces.listIterator(); it.hasNext();) {
			Trace t = it.next();
			graphFigure.xyGraph.removeTrace(t);
			it.remove();
		}
	}

	public Collection<Trace> addPlot(Plottable plottable) {
		return addPlot(plottable, false);
	}

	public Collection<Trace> addPlot(Plottable plottable, boolean overwrite) {
		if (plottable == null) {
			return null;
		}
		if (overwrite)
			resetPlots();
		XYGraph xyGraph = graphFigure.xyGraph;
		OSGIUtils.postEvent(EventKeys.METRICS, Activator.class,
				"plugin", Activator.PLUGIN_ID,
				"capability", "plot_function");

		Collection<Trace> theTraces = addPlotToGraph(plottable, xyGraph);
		traces.addAll(theTraces);
		xyGraph.performAutoScale();
		return theTraces;
	}


	static Collection<Trace> addPlotToGraph(Plottable plottable, XYGraph xyGraph) {
		List<Trace> myTraces = new ArrayList<>();
		for (Function function : plottable.functions) {
			AbstractDataProvider provider = getProvider(function);		
			Trace trace = new Trace(function.getName(), xyGraph.primaryXAxis, xyGraph.primaryYAxis, provider);
			xyGraph.primaryYAxis.setTitle(plottable.ordinate);
			xyGraph.primaryXAxis.setTitle(plottable.abscissa);
			setTraceParameters(trace, function);
			xyGraph.addTrace(trace);
			myTraces.add(trace);
		}
		return myTraces;
	}


	private static void setTraceParameters(Trace trace, Function function) {
		if (function instanceof ScatterFunction) {
			trace.setTraceType(TraceType.POINT);
			trace.setPointStyle(PointStyle.FILLED_DIAMOND);
			trace.setPointSize(8);
		} else if (function instanceof HistogramFunction) {
			trace.setTraceType(TraceType.BAR);
			// TODO Should be based on number of bins
			trace.setLineWidth(10);
		}
		RGB[] colors = XYGraph.DEFAULT_TRACES_COLOR;
		int code = Math.abs(trace.getName().hashCode()  + 1) % colors.length;
		trace.setTraceColor(XYGraphMediaFactory.getInstance().getColor(colors[code]));
	}


	@SuppressWarnings("unchecked")
	private static AbstractDataProvider getProvider(Function function) {
		CircularBufferDataProvider provider = new CircularBufferDataProvider(false);
		int size = function.getSize();
		if (size != Function.UNDEFINED) {
			provider.setBufferSize(size+1);
		} else {
			provider.setBufferSize(NPOINTS+1);
		}
		if (function instanceof Iterable) {
			for (double[] sample: (Iterable<double[]>) function) {
				provider.addSample(new Sample(sample[0], sample[1])) ;				
			}
			return provider;
		} else {
			double[] x_bounds = function.getPreferredXBounds();
			double step = (x_bounds[1] - x_bounds[0]) / NPOINTS;
			for (double d = x_bounds[0]; d < x_bounds[1]; d += step) {
				provider.addSample(new Sample(d, function.getValue(d)));				
			}
			return provider;
		}
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		setSelection(event.getSelection());
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		if(part == this)
		{
			return;
		}

		if(isLinked) {
			setSelection(selection);
		}
	}
		
	public void setSelection(ISelection selection)
	{
		if(!(selection instanceof IStructuredSelection) || !isLinked)
		{
			return;
		}

		IStructuredSelection iss = (IStructuredSelection) selection;
		Object[] entities = iss.toArray();
		if(entities==null || entities.length<1)
		{
			return;
		}

		List<Plottable> plottables = new ArrayList<>();
		for(Object entity : entities)
		{
			Plottable p = getPlottable(entity);
			if(p != null)
			{
				plottables.add(p);
			}
		}

		if(plottables.size() < 1)
		{
			getSite().getSelectionProvider().setSelection(selection);
			return;
		}

		resetPlots();
		for(Plottable p : plottables)
		{
			addPlot(p);
		}
	}

	private static final Class<?>[] plottableAdapters = new Class[] {
		Plottable.class,
		MaterialPlottable.class,
	};
	
	private Plottable getPlottable(Object entity)
	{
		IAdapterManager mgr = Platform.getAdapterManager();
		for(Class<?> cls : plottableAdapters)
		{
			Object adapted = mgr.getAdapter(entity, cls);
			if(adapted instanceof Plottable)
			{
				return (Plottable) adapted;
			}
		}
		return null;
	}
}
