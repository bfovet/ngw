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


public abstract class Function {

	public static final int UNDEFINED = -1;

	public static int SAMPLE_SIZE = 1000;
	
	protected Double scaleX;
	protected Double scaleY;
	protected Double offsetX;
	protected Double offsetY;
	protected String name;
    
    public Function(String name,double scaleX, double scaleY, double offsetX, double offsetY) {
    	this.setName(name);
    	this.scaleX = scaleX;
    	this.scaleY = scaleY;
    	this.offsetX = offsetX;
    	this.offsetY = offsetY;
    }
    
    public Function(String name) {
    	this(name,1.0,1.0,0.0,0.0);
    }    
    
	public abstract double getValue(double x);
        
    public abstract double[] getPreferredXBounds();
    
    public abstract double[] getPreferredYBounds();
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * For a function containing a known number of discrete data points, returns the number of points. Otherwise returns {@link #UNDEFINED}.
	 * @return the number of points in this function, or {@link #UNDEFINED} if undefined
	 */
	public int getSize() {
		return UNDEFINED;
	}
}
