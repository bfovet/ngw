/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.util;

/**
 * An exception for GetOpt errors.
 * <P>
 * (C) 2000 Sandia National Laboratories
 * @version $Id: GetOptException.java,v 1.3 2001/10/19 17:15:21 ejfried Exp $
 */

public class GetOptException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 625398392390707546L;

	public GetOptException(String gripe) {
        super(gripe);
    }

	public GetOptException() {
	}
}
