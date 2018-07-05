/**
 * 
 */
package gov.sandia.dart.workflow.editor.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowDiagramEditor;

/**
 * @author mjgibso
 *
 */
public class OpenInternalNestedWorkflowFeature extends AbstractCustomFeature
{

	protected final String nodeType;
	protected final String property;

	public OpenInternalNestedWorkflowFeature(IFeatureProvider fp, String nodeType, String property)
	{
		super(fp);
		
		this.nodeType = nodeType;
		this.property = property;
	}
	
	@Override
	public void execute(ICustomContext context)
	{
		final PictogramElement pe = context.getPictogramElements()[0];
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		if(bo instanceof WFNode)
		{
			WFNode node = (WFNode) bo;	
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorPart editor = page.getActiveEditor();
			if(editor instanceof WorkflowDiagramEditor)
			{
				WorkflowDiagramEditor workflowEditor = (WorkflowDiagramEditor) editor;
				workflowEditor.openNestedInternalWorkflow(node, property);
			}
		}
	}
	
	@Override
	public boolean canExecute(ICustomContext context)
	{
		return true;
	}
	
	@Override
	public boolean canUndo(IContext context) {
		return false;
	}
	
	@Override
	public boolean hasDoneChanges() {
		return false;
	} 
}
