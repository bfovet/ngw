/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.osgi.util;

import gov.sandia.dart.metrics.MetricsEventKeys;

public interface EventKeys
{
	String SELECTION_BROADCAST = "gov/sandia/dart/selection/broadcast";
	
	
	String JOURNAL_FILE_CHANGED = "gov/sandia/dart/modelmanifest/part/journal_changed";
	String MODEL_PART_CLOSED = "gov/sandia/dart/modelmanifest/part/closed";
	String MODEL_PART_DELETED = "gov/sandia/dart/modelmanifest/part/delete";
	String MODEL_PART_ADDED = "gov/sandia/dart/modelmanifest/part/add";
	String MODEL_ADDED = "gov/sandia/dart/modelmanifest/model/add";
	String MODEL_REMOVED = "gov/sandia/dart/modelmanifest/model/remove";
	
	String MODEL_CHANGED = "gov/sandia/dart/modelmanifest/model/change";
	
	/**
	 * METRICS - For posting to the metrics server;
	 * gov/sandia/dart/metrics/add 
	 */
	String METRICS = MetricsEventKeys.Add.getKey();
	
	// Mesh change events for Cubit
	String MESH_CHANGED = "gov/sandia/dart/cubit/mesh/change";
	String ASSEMBLY_CHANGED = "gov/sandia/dart/cubit/mesh/assembly/changed";
	String MESH_BLOCK_CREATED = "gov/sandia/dart/cubit/mesh/block/create";
	String MESH_BLOCK_DELETED = "gov/sandia/dart/cubit/mesh/block/deleted";	
}
