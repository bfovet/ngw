/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.chart.xyplot.expression;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ScatterFunction extends Function implements Iterable<double[]> {
	
	private Map<Double, Double> points = new HashMap<>();
	private double[] boundsX;
	private double[] boundsY;
	
	ScatterFunction(String name, double[] x, double[] y) {
		super(name);
		initMap(x, y);
	}

	private void initMap(double[] xa, double[] ya) {
		double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = Double.MIN_VALUE, maxY=Double.MIN_VALUE;
		for (int i=0; i<xa.length; ++i) {
			final double x = xa[i];
			final double y = ya[i];			
			points.put(x, y);
			if (x < minX)
				minX = x;
			if (x > maxX)
				maxX = x;
			if (y < minY)
				minY = y;
			if (y > maxY)
				maxY = y;		
		}
		
		boundsX = new double[]{minX, maxX};
		boundsY = new double[]{minX, maxX};
	}

	@Override
	public double getValue(double x) {
		Double v = points.get(x);
		if (v != null)
			return v;
		else
			return 0;
	}

	@Override
	public double[] getPreferredXBounds() {
		return boundsX;
	}

	@Override
	public double[] getPreferredYBounds() {
		return boundsY;
	}
	
	@Override
	public Iterator<double[]> iterator() {
		return new Iterator<double[]> () {
			Iterator<Double> it = points.keySet().iterator();
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public double[] next() {
				double d = it.next();
				return new double[] {d, getValue(d)};
			}					
		};
	}
	
	@Override
	public int getSize() {
		return points.size();
	}
}
