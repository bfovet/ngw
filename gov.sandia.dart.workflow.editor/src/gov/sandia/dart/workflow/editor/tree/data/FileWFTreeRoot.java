/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.tree.data;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import com.strikewire.snl.apc.resources.CommonResourceUtils;

public class FileWFTreeRoot extends WFTreeRoot {

	URI uri_;
	
	IPath path_; 
	
	private long lastTimestamp_ = -1;
	
	public FileWFTreeRoot(IPath path, Object parent) {
		super(path.lastSegment(), parent);
		setURI(path);		
	}
	
	private void setURI(IPath path) {
		path_ = path;
		String pathName = path.toString();
		uri_ = URI.createFileURI(pathName);
	}
	
	@Override
	protected Resource getRootResource() {
		ResourceSet resourceSet = new ResourceSetImpl();
		URI resourceURI = resourceSet.getURIConverter().normalize(uri_);

		Resource resource = resourceSet.getResource(resourceURI, true);
		return resource;
	}

	@Override
	protected boolean needsToBeRefreshed() {
		// TODO AJR - can we do this from URI easily?
		if(path_ == null) {
			return false;
		}
		
		IFile file = CommonResourceUtils.getFileForPath(path_.toString());
		
		if (file != null) {
			long timestamp = file.getModificationStamp();

			if(timestamp == IFile.NULL_STAMP || timestamp > lastTimestamp_) {
				lastTimestamp_ = timestamp;
				return true;
			}
		}
		return false;
	}

}
