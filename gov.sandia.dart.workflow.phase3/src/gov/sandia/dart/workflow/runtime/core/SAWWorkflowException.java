/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.core;

@SuppressWarnings("serial")
public class SAWWorkflowException extends RuntimeException {

	public SAWWorkflowException(String message, Throwable cause) {
		super(message, cause);
	}

	public SAWWorkflowException(String message) {
		super(message);
	}
}
