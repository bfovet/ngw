/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;

import gov.sandia.dart.workflow.editor.configuration.NodeType;
import gov.sandia.dart.workflow.editor.configuration.WorkflowTypesManager;
import gov.sandia.dart.workflow.editor.extensions.IWorkflowEditorPaletteContributor;
import gov.sandia.dart.workflow.editor.features.CreateImageFeature;
import gov.sandia.dart.workflow.editor.features.CreateNoteFeature;
import gov.sandia.dart.workflow.editor.features.CreateResponseFeature;
import gov.sandia.dart.workflow.editor.features.CreateWFNodeFeature;
import gov.sandia.dart.workflow.editor.library.UserCustomNodeLibrary;

public class PaletteBuilder {

	public static final String USER_DEFINED = "User Defined";

	public IPaletteCompartmentEntry[] createPaletteEntries(IFeatureProvider featureProvider, URI diagramFile) {
		
		Map<String, PaletteCompartmentEntry> compartmentsByName = new HashMap<>();
		List<IPaletteCompartmentEntry> compartments = new ArrayList<>();
		PaletteCompartmentEntry compartmentEntry = getCompartment("Workflow Organization", compartments, compartmentsByName);

		ICreateFeature noteFeature = new CreateNoteFeature(featureProvider);   
		ObjectCreationToolEntry noteToolEntry = new ObjectCreationToolEntry(
				"Note", "A descriptive note",
				null,
				null,
				noteFeature);
		compartmentEntry.addToolEntry(noteToolEntry);		
		
		ICreateFeature imageFeature = new CreateImageFeature(featureProvider);   
		ObjectCreationToolEntry imageToolEntry = new ObjectCreationToolEntry(
				"Image", "An image on the workflow canvas",
				null,
				null,
				imageFeature);
		compartmentEntry.addToolEntry(imageToolEntry);		
		
		ICreateFeature responseFeature = new CreateResponseFeature(featureProvider);   
		ObjectCreationToolEntry responseToolEntry = new ObjectCreationToolEntry(
				"Response", "A global response for the workflow",
				null,
				null,
				responseFeature);
		compartmentEntry.addToolEntry(responseToolEntry);
		
		WorkflowTypesManager manager = WorkflowTypesManager.get();
		Map<String, NodeType> nodeTypes = manager.getNodeTypes();
		
		for (NodeType nodeType: nodeTypes.values()) {
			// These are added back elsewhere
			List<String> categories = nodeType.getCategories();
			if (categories.size() == 1 && "Uncategorized".equals(categories.get(0))) {
				continue;
			}
			for (String category : categories) {
				PaletteCompartmentEntry compartment = getCompartment(category, compartments, compartmentsByName);
				String name = nodeType.getName();
				ICreateFeature functionFeature = new CreateWFNodeFeature(featureProvider, nodeType);   
				ObjectCreationToolEntry functionToolEntry = new ObjectCreationToolEntry(
						name, name,
						functionFeature.getCreateImageId(),
						functionFeature.getCreateLargeImageId(),
						functionFeature);
				compartment.addToolEntry(functionToolEntry);
			}
		}
		
		for (NodeType nodeType: UserCustomNodeLibrary.getNodes()) {
			List<String> categories = nodeType.getCategories();
			for (String category : categories) {
				PaletteCompartmentEntry compartment = getCompartment(category, compartments, compartmentsByName);
				String name = nodeType.getLabel();
				if (StringUtils.isEmpty(name))
					name = nodeType.getName();
				ICreateFeature functionFeature = new CreateWFNodeFeature(featureProvider, nodeType);   	    	
				// Using the description as a backdoor to allow us to find and delete the nodeType later. This is an ugly kludge, but not
				// Graphiti makes this very hard; this tool entry is pulled apart and discarded rather than actually being used on the palette.
				ObjectCreationToolEntry functionToolEntry = new ObjectCreationToolEntry(
						name, String.valueOf(System.identityHashCode(nodeType)),
						functionFeature.getCreateImageId(),
						functionFeature.getCreateLargeImageId(),
						functionFeature);
				compartment.addToolEntry(functionToolEntry);
			}
		}
		
		
		List<IWorkflowEditorPaletteContributor> extensions = getExtensions();	
		for (IWorkflowEditorPaletteContributor extension: extensions) {
			IPaletteCompartmentEntry paletteEntry = extension.getPaletteEntry(featureProvider, diagramFile);			
			if (paletteEntry != null) {
				compartments.add(paletteEntry);
			}
		}
		
		for (IPaletteCompartmentEntry entry : compartments) {
			List<IToolEntry> tools = entry.getToolEntries();
			tools.sort(new Comparator<IToolEntry>() {
				@Override
				public int compare(IToolEntry p1, IToolEntry p2) {
					try {
						return p1.getLabel().compareToIgnoreCase(p2.getLabel());
					} catch (RuntimeException e) {
						return 0;
					}
				}
				
			});
		}
				
		IPaletteCompartmentEntry[] entries = compartments.toArray(new IPaletteCompartmentEntry[compartments.size()]);
		Arrays.sort(entries, new Comparator<IPaletteCompartmentEntry>() {
			@Override
			public int compare(IPaletteCompartmentEntry o1,
					IPaletteCompartmentEntry o2) {
				return o1.getLabel().compareToIgnoreCase(o2.getLabel());
			}
		});
		return entries;
	}

	private PaletteCompartmentEntry getCompartment(String category,
			List<IPaletteCompartmentEntry> compartments, Map<String, PaletteCompartmentEntry> compartmentsByName) {
		PaletteCompartmentEntry entry = compartmentsByName.get(category);
		if (entry == null) {
			entry = new PaletteCompartmentEntry(category, null);
			compartments.add(entry);
			compartmentsByName.put(category, entry);
			entry.setInitiallyOpen("Engineering".equals(category));
		}
		return entry;		
	}
	
	private List <IWorkflowEditorPaletteContributor> getExtensions() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(WorkflowEditorPlugin.PLUGIN_ID, "paletteContributor");
		IConfigurationElement[] elements = extensionPoint.getConfigurationElements();
		 List <IWorkflowEditorPaletteContributor> contributors = new ArrayList<>();
		for (int el = 0; el < elements.length; el++) {
			IConfigurationElement config = elements[el];
			if (config.getName().equals("contributor")) {				
				try {
					IWorkflowEditorPaletteContributor contributor = (IWorkflowEditorPaletteContributor) config.createExecutableExtension("class");
					contributors.add(contributor);
				} catch (Exception e) {
					WorkflowEditorPlugin.getDefault().logError("Can't create paletteContributor", e);
				}
			}
		}
		return contributors;
	}
}
