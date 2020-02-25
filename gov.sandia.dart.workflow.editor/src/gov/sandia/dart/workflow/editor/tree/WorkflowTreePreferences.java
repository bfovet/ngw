/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.tree;

public class WorkflowTreePreferences {


	public enum Mode{
			FLAT,
			HIERARCHICAL
	};
	
	private Mode mode_ = Mode.FLAT;
	
	private boolean showUnconnectedInputs_ = false;
	
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
}
