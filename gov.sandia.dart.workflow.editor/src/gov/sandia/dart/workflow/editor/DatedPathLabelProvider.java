/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.LabelProvider;

public class DatedPathLabelProvider extends LabelProvider {

	IPath rootPath;
	
	public DatedPathLabelProvider() {
		rootPath = null;
	}
	
	public DatedPathLabelProvider(IPath rootPath) {
		this.rootPath = rootPath;
	}
	
	public void setRootPath(IPath rootPath) {
		this.rootPath = rootPath;
	}
	
	@Override
	public String getText(Object element) {
		IPath path = new Path(((DatedPath) element).path);
		if(rootPath != null && rootPath.isPrefixOf(path)) {
			IPath relPath = path.makeRelativeTo(rootPath);
			if(relPath.isEmpty()) {
				return path.toOSString();
			}
			return relPath.toOSString();
		} else {		
			return path.toOSString();
		}
	}
}
