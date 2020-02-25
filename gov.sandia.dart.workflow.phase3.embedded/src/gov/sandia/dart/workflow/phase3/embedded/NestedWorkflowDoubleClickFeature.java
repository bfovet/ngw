package gov.sandia.dart.workflow.phase3.embedded;

import java.io.File;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowDiagramEditor;
import gov.sandia.dart.workflow.editor.features.ICustomFeatureProvider;
import gov.sandia.dart.workflow.editor.features.OpenReferencedFileFeature;
import gov.sandia.dart.workflow.editor.settings.NOWPSettingsEditorUtils;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class NestedWorkflowDoubleClickFeature extends OpenReferencedFileFeature {

	private static final String PRIVATE_WORK_DIR = "use private work directory";
	private static final String OLD_PRIVATE_WORK_DIR = "privateWorkDir";
	
	public NestedWorkflowDoubleClickFeature(IFeatureProvider fp, String nodeType, String property) {
		super(fp, nodeType, property);
	}
	
	@Override
	protected IEditorPart openEditor(WFNode node) throws PartInitException {
		IEditorPart part = super.openEditor(node);
		if (part instanceof WorkflowDiagramEditor) {
			WorkflowDiagramEditor editor = NOWPSettingsEditorUtils.getDiagramEditor(node);
			if (editor != null) {
				File location = editor.getRunLocation();
				if (location.exists()) {
					if (PropertyUtils.isTrue(node, PRIVATE_WORK_DIR) || PropertyUtils.isTrue(node, OLD_PRIVATE_WORK_DIR)) {
						File subdir = new File(location, node.getName());
						if (subdir.isDirectory()) {
							((WorkflowDiagramEditor) part).setRunLocation(subdir);		
						}
					}
				}
			}
		}
		return part;		
	}

	public static class Provider implements ICustomFeatureProvider {

		@Override
		public ICustomFeature createFeature(IFeatureProvider fp, String nodeType, String property) {
			return new NestedWorkflowDoubleClickFeature(fp, nodeType, property);
		}
		
	}
	
}
