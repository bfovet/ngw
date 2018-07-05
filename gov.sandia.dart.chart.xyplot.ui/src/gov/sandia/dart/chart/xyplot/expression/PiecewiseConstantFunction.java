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


public class PiecewiseConstantFunction extends Function {
	
	private Range[] ranges;
	private double maxY,minY;

	PiecewiseConstantFunction(String name,double scaleX,double scaleY,double offsetX,double offsetY,double[] x, double[] y) {
		super(name,scaleX, scaleY, offsetX, offsetY);

		// We can avoid a whole lot of problems just by ensuring there is always plottable data
		if (x.length == 0) {
			x = new double[] {0, 0};
			y = new double[] {1, 0};
		} else if (x.length == 1) {
			x = new double[] {x[0], x[0]+1};
			y = new double[] {y[0], y[0]};			
		}

		ranges = new Range[x.length-1];
		for (int i = 0; i < x.length - 1; i++) {
			ranges[i] = new Range(x[i],x[i+1],y[i],y[i+1]);
		}
		
		for (int i = 0; i < y.length; i++) {
			double _y_ = scaleY * y[i] + offsetY;
			if(_y_ < minY) {
				minY = _y_;
			} else if (_y_ > maxY) {
				maxY = _y_;
			}
		}
	}
	
	@Override
	public double[] getPreferredXBounds() {
		// Placeholder
		return new double[] {ranges[0].lowerX, ranges[ranges.length-1].upperX};
	}

	@Override
	public double[] getPreferredYBounds() {
		// Placeholder
		return new double[] {minY,maxY};
	}
		
	@Override
	public double getValue(double x) {
		x = (x*scaleX) + offsetX;
		return (scaleY * getValue(x,0,ranges.length)) + offsetY;
	}
	
	public double getValue(double x, int first, int last) {
		
		int mid = (first + last) / 2;
		Range range = ranges[mid];
		if (range.contains(x)) {
			return range.lowerY;
		} else {
			if (x < range.lowerX) {
				if (mid == 0) {
					return range.lowerY;
				} else {
					return getValue(x,first,mid);
				}
			} else if (x >= range.upperX) {
				if (mid == ranges.length-1) {
					return range.upperY;
				} else {
					return getValue(x,mid,last);
				}
			}
		}
		return Double.NaN;
	}
	
	private class Range {
		
		double lowerX;
		double upperX;
		
		double lowerY;
		double upperY;
		
		public Range(double lowerX, double upperX, double lowerY, double upperY) {
			this.lowerX = lowerX;
			this.upperX = upperX;
			this.lowerY = lowerY;
			this.upperY = upperY;
		}
		
		public boolean contains(double x) {
			return lowerX <= x &&
					x < upperX;
		}
	}
}
