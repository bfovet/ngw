/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded;

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.chart.xyplot.expression.FunctionBuilder;
import gov.sandia.dart.chart.xyplot.ui.view.NebulaXYPlotView;
import gov.sandia.dart.chart.xyplot.ui.view.Plottable;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class XYPlotNode extends SAWCustomNode {

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties,
			WorkflowDefinition workflow, RuntimeData runtime) {
		
		final String title = properties.getOrDefault("title", "Untitled");		
		final double[] x = (double[]) runtime.getInput(this.getName(), "x", double[].class);
		final double[] y = (double[]) runtime.getInput(this.getName(), "y", double[].class);
		
		if (x == null || y == null) {
			throw new SAWWorkflowException(String.format("%s: both inputs required", getName()));
		}
		
		// Creating a object to preserve link between x and y
				class Items {
				    private double x_val;
				    private double y_val;
				  
				    public Items(double d, double e) {			
				    	this.x_val = d;
				        this.y_val = e;
					}
					
				    public double getX() {
				        return this.x_val;
				    }

				    public double getY() {
				        return this.y_val;
				    }
				}
						
				class SortbyX implements Comparator<Items>{
				    public int compare(Items a, Items b){
				    	if( a.x_val > b.x_val)
				    		return 1;
				    	if( a.x_val < b.x_val)
				    		return -1;
				    	return 0;
				    }
				}
						
				ArrayList<Items> sortItems = new ArrayList<Items>();
				for (int i = 0; i < x.length; i++) {		
					sortItems.add(new Items(x[i],y[i]));
				}
				Collections.sort(sortItems, new SortbyX());		
				
				for (int i = 0; i < x.length; i++) {
					x[i] = sortItems.get(i).getX();
					y[i] = sortItems.get(i).getY();
				}
			
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					IWorkbenchPage page = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					NebulaXYPlotView view = (NebulaXYPlotView) page
							.showView(NebulaXYPlotView.VIEW_ID, null, IWorkbenchPage.VIEW_VISIBLE);
					view.addPlot(new Plottable(FunctionBuilder
							.buildPiecewiseLinear(title, 1.0, 1.0, 0.0, 0.0, x,
									y)));
				} catch (PartInitException e) {
					EmbeddedWorkflowPlugin.getDefault().logError(String.format("%s: couldn't create plot", getName()), e);			
				}
			}				
		});

		return Collections.singletonMap("f", true);
	}
	
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo("title")); }
	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo("x"), new InputPortInfo("y")); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo("f")); }
	
	
	@Override
	public String getCategory() {
		return "Engineering";
	}

}
