/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.nested;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;

/**
 * A WorkflowConductor is a basically an iterator over a set of workflows to run.
 * @author ejfried
 *
 */
public interface WorkflowConductor extends Iterable<Map<String, String>>, Iterator<Map<String, String>> {

	@Override
	boolean hasNext();

	@Override
	Map<String, String> next();
	
	@Override
	Iterator<Map<String, String>> iterator();

	void setProperties(Map<String, String> properties);
	
	List<PropertyInfo> getDefaultProperties();

}
