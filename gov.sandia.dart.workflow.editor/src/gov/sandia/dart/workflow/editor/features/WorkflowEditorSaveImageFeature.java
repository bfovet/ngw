package gov.sandia.dart.workflow.editor.features;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.ui.features.DefaultSaveImageFeature;
import org.eclipse.graphiti.ui.saveasimage.ISaveAsImageConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class WorkflowEditorSaveImageFeature extends DefaultSaveImageFeature {

	public WorkflowEditorSaveImageFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected String getFilename(GraphicalViewer viewer, ISaveAsImageConfiguration saveAsImageConfiguration) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		String fileExtensions[] = new String[] { "*." + saveAsImageConfiguration.getFormattedFileExtension() }; //$NON-NLS-1$
		fileDialog.setFilterExtensions(fileExtensions);
		File workflowFile = getWorkflowFile();				
		fileDialog.setFileName(FilenameUtils.getBaseName(workflowFile.getName()) + "." + saveAsImageConfiguration.getFormattedFileExtension());
		fileDialog.setFilterPath(workflowFile.getParent());
		String filename = fileDialog.open();
		return addFileExtension(saveAsImageConfiguration.getFormattedFileExtension(), filename);	
	}

	private File getWorkflowFile() {
		URI uri = getDiagram().eResource().getURI();
		String pathString = uri.toPlatformString(true);
		IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pathString));			
		return ifile.getLocation().toFile();
	}

}
