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

import java.util.Iterator;

import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class HistogramFunction extends Function implements Iterable<double[]> {
	
	private long[] bins;
	private double bounds[];

	HistogramFunction(String name, double[] data, int nbins) {
		super(name);
		
		if(nbins < 1) {
			DescriptiveStatistics stat = new DescriptiveStatistics(data);
			double IQR = stat.getPercentile(75) - stat.getPercentile(25);
			double binWidth = 2 * IQR * Math.pow(data.length, -1/3.0);
			nbins = (int) ((stat.getMax() - stat.getMin())/binWidth);
			nbins = nbins == 0 ? 1 : nbins;	
		}
		
		
		long[] histogram = new long[nbins];
		EmpiricalDistribution distribution = new EmpiricalDistribution(nbins);
		distribution.load(data);
		int k = 0;
		for (SummaryStatistics stats: distribution.getBinStats())
		{
		    histogram[k++] = stats.getN();
		}
		bins = histogram;
		bounds = distribution.getUpperBounds();
	}
	
	@Override
	public Iterator<double[]> iterator() {
		return new Iterator<double[]> () {
			int i = 0;
			@Override
			public boolean hasNext() {
				return i < bins.length;
			}

			@Override
			public double[] next() {
				double x = bounds[i];
				double y = bins[i++];
				return new double[] {x, y};
			}					
		};
	}

	@Override
	public double getValue(double x) {
		return 0;
	}

	@Override
	public double[] getPreferredXBounds() {
		return null;
	}

	@Override
	public double[] getPreferredYBounds() {
		return null;
	}
	
	@Override
	public int getSize() {
		return bins.length;
	}
}
