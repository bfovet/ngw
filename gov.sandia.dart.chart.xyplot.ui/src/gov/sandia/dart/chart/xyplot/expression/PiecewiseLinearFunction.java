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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PiecewiseLinearFunction extends Function {

	private Range[] ranges;
	private double maxY,minY;
	
	PiecewiseLinearFunction(String name,double scaleX, double scaleY,double offsetX, double offsetY,double[] x, double[] y) {
		super(name,scaleX,scaleY,offsetX,offsetY);
		// Sort the ranges so that graph should be continuous.
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
												
		// We can avoid a whole lot of problems just by ensuring there is always plottable data
		if (x.length == 0) {
			x = new double[] {0, 0};
			y = new double[] {1, 0};
		} else if (x.length == 1) {
			x = new double[] {x[0], x[0]+1};
			y = new double[] {y[0], y[0]};			
		}
		
		ranges = new Range[x.length-1];
		
		
		ArrayList<Items> sortItems = new ArrayList<Items>();
		for (int i = 0; i < x.length; i++) {		
			sortItems.add(new Items(x[i],y[i]));
		}
		Collections.sort(sortItems, new SortbyX());			
		for (int i = 0; i < x.length; i++) {
			x[i] = sortItems.get(i).getX();
			y[i] = sortItems.get(i).getY();
		}
			
		for (int i = 0; i < x.length-1; i++) {
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
	public double getValue(double x) {
		x = (x*scaleX) + offsetX;
		return (scaleY * getValue(x,0,ranges.length)) + offsetY;
	}
	
	@Override
	public double[] getPreferredXBounds() {
		// Placeholder
		if (ranges.length < 1 || ranges[0] == null)
			return new double[] {-10, 10};
		double lower = ranges[0].lowerX;
		double upper = ranges[0].upperX;
		if (ranges[ranges.length-1] != null)
			upper = ranges[ranges.length-1].upperX;
		if (upper == lower)
			upper = lower + 1;
		return new double[] {lower, upper};
	}

	@Override
	public double[] getPreferredYBounds() {
		// Placeholder
		return new double[] {minY,maxY};
	}
	
	
	public double getValue(double x,int first,int last) {
		int mid = (first + last) / 2;
		
		Range range = ranges[mid];
		if (range.contains(x)) {
			return range.getValue(x);
		} else if (x < range.lowerX) {
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
		return Double.NaN;
	}
	
	private class Range {
		
		double lowerX;
		double upperX;
		
		double slope;
		double lowerY;
		double upperY;
		
		public Range(double lowerX, double upperX, double lowerY, double upperY) {
			this.lowerX = lowerX;
			this.upperX = upperX;
			this.slope = (upperY - lowerY) / (upperX - lowerX);
			this.lowerY = lowerY;
			this.upperY = upperY;
		}
		
		public boolean contains(double x) {
			return lowerX <= x &&
					x < upperX;
		}
		
		public double getValue(double x) {
			return ((x-lowerX) * slope) + lowerY;
		}
		
	}

}
