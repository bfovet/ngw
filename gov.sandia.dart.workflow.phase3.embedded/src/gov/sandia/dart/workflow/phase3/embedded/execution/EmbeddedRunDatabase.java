/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.execution;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.jobs.Job;

// TODO This needs to be keyed off of workdir, not IWF file.
class EmbeddedRunDatabase {
	static EmbeddedRunDatabase INSTANCE = new EmbeddedRunDatabase();
	private ConcurrentMap<IFile, Job> jobs = new ConcurrentHashMap<>();
	
	void put(IFile file, Job job) {
		jobs.put(file, job);
	}
	
	Job get(IFile file) {
		return jobs.get(file);
	}
	
	Job remove(IFile file) {
		return jobs.remove(file);
	}


}

