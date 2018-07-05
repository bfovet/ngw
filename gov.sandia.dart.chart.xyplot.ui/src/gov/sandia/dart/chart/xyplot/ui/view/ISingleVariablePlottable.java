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

public interface ISingleVariablePlottable {

	public void setVariable(String variable);
	public String getVariable();
	
	static class VariableNotFoundException extends RuntimeException{

		public VariableNotFoundException(String string) {
			super("Could not find variable: " + string);
		}
		
	}
	
}
