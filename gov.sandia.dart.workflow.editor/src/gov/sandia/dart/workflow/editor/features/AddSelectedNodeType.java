package gov.sandia.dart.workflow.editor.features;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class AddSelectedNodeType extends AbstractCustomFeature {

	public AddSelectedNodeType(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void execute(ICustomContext context) {
		Object obj = CreateArcFeature.createUserSelectedNode(false, getFeatureProvider(), getDiagram(), context.getX(), context.getY());		
		Display.getDefault().asyncExec(() -> focusEditor(getDiagram()));
	}

	private void focusEditor(EObject bo) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId("dartWorkflow");		
		DiagramEditorInput input = new DiagramEditorInput(bo.eResource().getURI(), providerId);
		IEditorPart part = page.findEditor(input);
		if (part != null) {
			page.activate(part);
			part.setFocus();
		}
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}
	
	@Override
	public String getName() {
		return "Add Object";
	}
}
