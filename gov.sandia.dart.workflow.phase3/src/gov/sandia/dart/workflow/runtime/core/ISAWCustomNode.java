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

import java.util.List;
import java.util.Map;

public interface ISAWCustomNode {
	// TODO Make doExecute() public so can be included here
	
	/**
	 * @return The default list of properties built into this node. The runtime
	 *         property list derived from the diagram may be larger or smaller.
	 */
	List<PropertyInfo> getDefaultProperties();

	/**
	 * @return The default list of input ports built into this node. The runtime
	 *         port list derived from the diagram may be larger or smaller.
	 */
	List<InputPortInfo> getDefaultInputs();

	/**
	 * @return The default list of output ports and their types built into this node. The runtime
	 *         port list derived from the diagram may be larger or smaller.
	 */
	List<OutputPortInfo> getDefaultOutputs();

	/**
	 * @return A category in which this node could be displayed in a palette.
	 */

	String getCategory();

	/**
	 * @return A list of categories in which this node could be displayed in a palette.
	 */

	List<String> getCategories();
	
	/**
	 * Override this method to tell the workflow engine if, based on the current saved state, the node needs to be evaluated
	 * or can simply report previous data.
	 * @param workflow TODO
	 */
	boolean canReuseExistingState(WorkflowDefinition workflow, RuntimeData runtime, Map<String, String> properties);
	
	/**
	 * Report previous state as if the node had just been executed
	 * @return 
	 */
	Map<String, Object> getPreviousResults(WorkflowDefinition workflow, RuntimeData runtime, Map<String, String> properties);	
	
}
