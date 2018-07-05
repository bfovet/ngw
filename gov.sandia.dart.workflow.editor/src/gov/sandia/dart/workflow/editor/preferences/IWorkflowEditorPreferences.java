/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.preferences;

public interface IWorkflowEditorPreferences {
	String PREFIX = "gov.sandia.dart.workflow.editor.";
	String FONT = PREFIX + "font";
	String NOTES_FONT = PREFIX + "notes_font";
	String PALETTE_FILE_DIR = PREFIX + "palette_file";
	String MANHATTAN_CONNECTIONS = PREFIX + "manhattan_connection";
	String NODE_TYPE_HEADERS = PREFIX + "node_type_headers";
	String PORT_LABELS = PREFIX + "port_labels";
	String EDITOR_FONT = PREFIX + "editor_font";
}
