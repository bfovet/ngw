/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.dakota;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.Datum;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;

public class DakotaResultsFile {
	public static void write(String filename, String sampleId, Collection<String> responseNames,  Map<String, Object> results) throws IOException {	
		try (PrintWriter writer = new PrintWriter(filename)){
			if (results == null) {
				// This is the Dakota-defined  protocol for handling sample evaluation errors. If the
				// results file starts with "fail", then the sample fails and the rest of the file is ignored.
				writer.println("FAIL");
				throw new SAWWorkflowException(String.format("No responses computed in evaluation %s so cannot return to caller", sampleId));
			} else {
				for (String name : responseNames) {
					if (results.get(name) == null) {
						writer.println("FAIL");
						throw new SAWWorkflowException(String.format("Response named '%s' not found; evaluation %s cannot return to caller", name, sampleId));
					}
				}
				for (String name : responseNames) {
					writer.println(String.format("%s  %s", format(results.get(name)), name));
				}
			}
		} 
	}

	public static void writeFailure(String filename, Exception ex) throws IOException {	
		try (PrintWriter writer = new PrintWriter(filename)){
			writer.println("FAIL");
			writer.println();
			ex.printStackTrace(writer);
		} 
	}
	
	private static Object format(Object object) {
		Datum datum = new Datum("text", object, object.getClass());
		String result = (String) datum.getAs(String.class);
		return result.trim();
	}

}
