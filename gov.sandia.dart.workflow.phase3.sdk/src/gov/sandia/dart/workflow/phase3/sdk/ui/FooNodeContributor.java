package gov.sandia.dart.workflow.phase3.sdk.ui;

import java.util.Arrays;
import java.util.List;

import gov.sandia.dart.workflow.editor.configuration.Input;
import gov.sandia.dart.workflow.editor.configuration.NodeType;
import gov.sandia.dart.workflow.editor.configuration.Output;
import gov.sandia.dart.workflow.editor.configuration.Prop;
import gov.sandia.dart.workflow.editor.extensions.IWorkflowEditorNodeTypeContributor;

public class FooNodeContributor implements IWorkflowEditorNodeTypeContributor {

	@Override
	public List<NodeType> getNodeTypes() {
		return Arrays.asList(new NodeType("foo").
				setCategories("Miscellaneous").
				setProperties(new Prop("input", "default")).
				setOutputs(new Output("foo", "default", null)).
				setInputs(new Input("input", "default")));
	}

}
