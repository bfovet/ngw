/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded;

import java.util.Arrays;
import java.util.List;

import gov.sandia.dart.workflow.editor.configuration.Input;
import gov.sandia.dart.workflow.editor.configuration.NodeType;
import gov.sandia.dart.workflow.editor.configuration.Output;
import gov.sandia.dart.workflow.editor.configuration.Prop;
import gov.sandia.dart.workflow.editor.extensions.IWorkflowEditorNodeTypeContributor;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;

public class EmbeddedNodeTypeContributor implements IWorkflowEditorNodeTypeContributor {

	public EmbeddedNodeTypeContributor() {
	}

	@Override
	public List<NodeType> getNodeTypes() {
		NodeType display = new NodeType("display", NodeCategories.UI);
		display.setOutputs(new Output("f", "default", ""));
		display.setInputs(new Input("x", "default"));
		display.setProperties(new Prop("formatString", "default", ""));

		NodeType browser = new NodeType("browser", NodeCategories.UI);
		browser.setOutputs(new Output("f", "default", ""));
		browser.setInputs(new Input("x", "default"));
		browser.setProperties(new Prop("instantiate browser", "boolean", "false"),
				new Prop("browser ID", "default", "Workflow Browser Component"), new Prop("URL", "default", ""),
				new Prop("wait for event", "default", ""), new Prop("expression", "default", ""));

		NodeType prompt = new NodeType("prompt", Arrays.asList(NodeCategories.CONTROL, NodeCategories.UI));
		prompt.setOutputs(new Output("f", "default", ""));
		prompt.setInputs(new Input("x", "default"));
		prompt.setProperties(new Prop("question", "default", ""));

		NodeType fileChooser = new NodeType("fileChooser", NodeCategories.UI);
		fileChooser.setOutputs(new Output("f", "default", ""), new Output(FileChooserNode.CANCELED, "default", ""));
		fileChooser.setInputs(new Input("x", "default"));
		fileChooser.setProperties(new Prop(FileChooserNode.INITIAL_PATH, "default", ""),
				new Prop(FileChooserNode.TITLE, "default", ""),
				new Prop(FileChooserNode.FILE_FILTERS, "default", ""));

		NodeType askYesNo = new NodeType("ask_yes_no", Arrays.asList(NodeCategories.CONTROL, NodeCategories.UI));
		askYesNo.setOutputs(new Output("yes", "default", ""), new Output("no", "default", ""));
		askYesNo.setInputs(new Input("x", "default"));
		askYesNo.setProperties(new Prop("question", "default", "Yes or no?"));

		NodeType openFile = new NodeType("openFile", Arrays.asList(NodeCategories.FILES, NodeCategories.UI));
		openFile.setOutputs(new Output("f", "default", ""));
		openFile.setInputs(new Input("filename", "default"));
		openFile.setProperties(new Prop("filename", "default", ""));

		NodeType xyplot = new NodeType("xyplot", Arrays.asList(NodeCategories.UI));
		xyplot.setOutputs(new Output("f", "default", ""));
		xyplot.setInputs(new Input("x", "default"), new Input("y", "default"));
		xyplot.setProperties(new Prop("title", "default", "Untitled"));

		return Arrays.asList(display, askYesNo, prompt, fileChooser, openFile, xyplot, browser);
	}
}
