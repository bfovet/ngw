/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.library;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.configuration.NodeType;
import gov.sandia.dart.workflow.editor.configuration.WorkflowTypesManager;
import gov.sandia.dart.workflow.editor.preferences.IWorkflowEditorPreferences;

/**
 * Library of user-defined node types
 * @author ejfried
 */
public class UserCustomNodeLibrary {
	private static List<NodeType> nodes = null;
	private final static Set<INodeLibraryChangeListener> listeners = ConcurrentHashMap.newKeySet();	
	
	public static synchronized void addNode(WFNode node) {
		checkInitialized();		
		NodeType type = new NodeType(node);
		nodes.add(type);
		saveDatabase();
		notifyListeners();
	}
	
	private static void saveDatabase() {
		// TODO IFiles, not Files
		File dir = new File(WorkflowEditorPlugin.getDefault().getPreferenceStore().getString(IWorkflowEditorPreferences.PALETTE_FILE_DIR));
		File file = new File(dir, "userComponents.xml");
		try (FileWriter writer = new FileWriter(file)) {
			WorkflowTypesManager.writeNodeTypes(writer, nodes);
		} catch (IOException e) {
			WorkflowEditorPlugin.getDefault().logError("Couldn't save user component file", e);
		}
	}
	
	private static synchronized void checkInitialized() {
		if (nodes == null) {
			nodes = new ArrayList<>();
		
			File dir = new File(WorkflowEditorPlugin.getDefault().getPreferenceStore().getString(IWorkflowEditorPreferences.PALETTE_FILE_DIR));
			File file = new File(dir, "userComponents.xml");
			if (file.exists()) {
				try {
					nodes = WorkflowTypesManager.parseNodeTypes(file);
				} catch (IOException | ParserConfigurationException | SAXException e) {
					WorkflowEditorPlugin.getDefault().logError("Error loading user nodes", e);
				}
			}
		}
		
	}

	public static synchronized List<NodeType> getNodes() {
		checkInitialized();
		return Collections.unmodifiableList(nodes);
	}
	
	public static synchronized void removeNodeType(String typeToRemove) {
		for (ListIterator<NodeType> it = nodes.listIterator(); it.hasNext(); ) {
			NodeType type = it.next();
			if (String.valueOf(System.identityHashCode(type)).equals(typeToRemove)) {
				it.remove();
				saveDatabase();
				notifyListeners();
				return;
			}
		}		
	}

	private static void notifyListeners() {
		listeners.forEach(o -> o.nodeLibraryChanged());
	}
	
	public static void addListener(INodeLibraryChangeListener listener) {
		listeners.add(listener);
	}
	
	public static void removeListener(INodeLibraryChangeListener listener) {
		listeners.remove(listener);
	}
}
