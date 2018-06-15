/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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

import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;

public class DakotaResultsFile {
	public static void write(String filename, Collection<String> responseNames,  Map<String, Object> results) throws IOException {	
		try (PrintWriter writer = new PrintWriter(filename)){
			if (results == null) {
				// This is the Dakota-defined  protocol for handling sample evaluation errors. If the
				// results file starts with "fail", then the sample fails and the rest of the file is ignored.
				writer.println("FAIL");
				throw new SAWWorkflowException("No responses computed so cannot return to Dakota");
			} else {
				for (String name : responseNames) {
					if (results.get(name) == null) {
						writer.println("FAIL");
						throw new SAWWorkflowException(String.format("Response named '%s' not found; workflow cannot return to Dakota", name));
					}
				}
				for (String name : responseNames) {
					writer.println(String.format("%s  %s", results.get(name).toString(), name));
				}
			}
		} 
	}

}
