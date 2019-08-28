package gov.sandia.dart.workflow.editor.tree;

public class WorkflowTreePreferences {


	public enum Mode{
			FLAT,
			HIERARCHICAL
	};
	
	private Mode mode_ = Mode.FLAT;
	
	private boolean showUnconnectedInputs_ = false;
	
	private boolean showTreeInDiagramEditor_ = false;
	
	
	public Mode getMode() {
		return mode_;
	}
	
	public void setMode(Mode mode) {
		mode_ = mode;
	}
	
	public boolean getShowUnconnectedInputs() {
		return showUnconnectedInputs_;
	}
	
	public void setShowUnconnectedPorts(boolean showUnconnectedInputs) {
		showUnconnectedInputs_ = showUnconnectedInputs;
	}

	public boolean getShowTreeInDiagramEditor() {
		return showTreeInDiagramEditor_;
	}

	public void setShowTreeInDiagramEditor(boolean showTreeInDiagramEditor_) {
		this.showTreeInDiagramEditor_ = showTreeInDiagramEditor_;
	}

}
