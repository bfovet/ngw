package gov.sandia.dart.workflow.editor;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.custom.CCombo;

import gov.sandia.dart.workflow.util.WorkflowUtils;

public class SubdirSelectionCombo extends ComboViewer {

	private DatedPathLabelProvider mLabelProvider;
	
	private IPath mRootPath;
	
	public SubdirSelectionCombo(CCombo combo) {
		super(combo);
		
		setContentProvider(new ArrayContentProvider());
		mLabelProvider = new DatedPathLabelProvider();
		setLabelProvider(mLabelProvider);
	}
	
	public void setText(String text) {
		IPath newPath = new Path(text);
		
		if(mRootPath != null && mRootPath.isPrefixOf(newPath)) {
			newPath = newPath.makeRelativeTo(mRootPath);
		}
		
		getCCombo().setText(newPath.toString());

	}
	
	public void setInput(IFile input) {
		if(input instanceof IFile) {
			IFile workflowFile = (IFile) input;
			IPath rootPath = new Path(workflowFile.getParent().getLocation().toFile().getAbsolutePath());
			List<DatedPath> datedPaths = null;
			try {
				datedPaths = WorkflowUtils.getRunLocationMarkers(workflowFile);
			} catch (CoreException e) {
				// Fall through
			}
			
			if(datedPaths == null || datedPaths.size() == 0) {
				File parent = workflowFile.getParent().getLocation().toFile(); 
				File dflt = new File(parent, FilenameUtils.getBaseName(workflowFile.getName()));
				datedPaths = Collections.singletonList(new DatedPath(dflt.getAbsolutePath(), System.currentTimeMillis()));
			}

			mRootPath = rootPath;
			mLabelProvider.setRootPath(rootPath);
			super.setInput(datedPaths);
			getCCombo().select(0);
		}		
	}
	
	public IPath getPath() {
		if(getStructuredSelection().size() > 0 ) {
			Object selection = getStructuredSelection().getFirstElement();
			
			if(selection instanceof DatedPath) {
				return new Path(((DatedPath)selection).path);
			}
		}
		
		IPath selectedPath = new Path(getCCombo().getText());
		
		if(!selectedPath.isAbsolute() && mRootPath != null) {
			selectedPath = mRootPath.append(selectedPath);
		}
		
		return selectedPath;
	}

	public int getItemCount() {
		return getCCombo().getItemCount();
	}
}
