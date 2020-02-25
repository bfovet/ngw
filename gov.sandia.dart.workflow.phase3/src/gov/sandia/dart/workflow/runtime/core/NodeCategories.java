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

/**
 * The intention here is just to provide a central reference point for these category
 * nodes, not (necessarily) to limit the possible names to only this list.
 * 
 * @author mrglick
 *
 */
public class NodeCategories {
	public static final String
		CONTROL = "Control",
		DATA_FORMATS = "Data Formats",
		DATA_SOURCES = "Data Sources",
		EXTERNAL_PROCESSES = "Applications",
		FILES = "File Operations",
		UI = "User Interface",
		MESHING = "Meshing",
		REMOTE = "Remote/Network Ops",
		SCALAR_OPS   = "Scalar Operations",
		SCRIPTING = "Scripting",
		SEQ_DATA = "Sequential Data",
		TEXT_DATA = "Text Data",
		WORKFLOW = "Workflow Organization";
}
