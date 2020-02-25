/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.packaging;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.domain.Property;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.configuration.NodeType;
import gov.sandia.dart.workflow.editor.configuration.WorkflowTypesManager;
import gov.sandia.dart.workflow.editor.features.AbstractCreateNodeFeature;
import gov.sandia.dart.workflow.util.PropertyUtils;


public class PackageComponentWizard extends Wizard {

    public static final Attributes.Name WORKFLOW_FILES = new Attributes.Name("Workflow-Files");
	public static final Attributes.Name WORKFLOW_COMPONENT = new Attributes.Name("Workflow-Component");
	public static final Attributes.Name WORKFLOW_PACKAGE_VERSION = new Attributes.Name("Workflow-Package-Version");
	protected FileSelectionPage one;
    protected JarLocationPage two;
	private IPath suggested;
	private List<WFNode> nodes;
	private String[] candidateFiles;

    public PackageComponentWizard(List<WFNode> nodes, IPath suggested) {
        super();
        setNeedsProgressMonitor(true);
        this.suggested = suggested;
        this.nodes = nodes;
        
        candidateFiles = getCandidateFiles(nodes);
    }

    private void validateNode(List<String> files) {    
    		for (String path: files) {
    			if (new File(path).isAbsolute()) {
    				// Right now we're only allowing relative paths for files; not sure what else we could do.
    				throw new IllegalArgumentException(String.format("File path %s is absolute and cannot be packaged.", path));    				
    			} else if (path.indexOf("..") > -1) {
    				throw new IllegalArgumentException(String.format("File path %s contains navigation characters '..' and cannot be packaged", path));    				
    			} else if (!new File(suggested.removeLastSegments(1).toPortableString(), path).exists()) {
    				throw new IllegalArgumentException(String.format("File path %s does not exist and cannot be packaged", path));    				    				
    			}
    		}
    	
	}

	@Override
    public String getWindowTitle() {
        return "Package Workflow Component";
    }

	@Override
	public void addPages() {
		if (candidateFiles != null && candidateFiles.length > 0) {
			one = new FileSelectionPage(candidateFiles);
			addPage(one);
		}
		two = new JarLocationPage(suggested);
		addPage(two);
	}

	@Override
	public boolean performFinish() {    		
		try {
			if (one != null && one.getSelectedFiles().size() > 0) {
				validateNode(one.getSelectedFiles());
			}				

			IPath path = two.getPath();
			List<String> propFilePaths = getPropFilePaths();
			FileOutputStream fos = new FileOutputStream(path.toFile());
			Manifest manifest = new Manifest();
			Attributes attr = manifest.getMainAttributes();
			attr.put(Attributes.Name.MANIFEST_VERSION, "1.0");
			attr.put(WORKFLOW_PACKAGE_VERSION, "1");		
			attr.put(WORKFLOW_COMPONENT, StringUtils.join(propFilePaths, ","));
			if (one != null && one.getSelectedFiles().size() > 0) {
				attr.put(WORKFLOW_FILES, StringUtils.join(one.getSelectedFiles(), ","));
			}
			File context = suggested.removeLastSegments(1).toFile();

			try (JarOutputStream jos = new JarOutputStream(fos, manifest)) {
				for (WFNode node: nodes) {
					WFNode newNode = AbstractCreateNodeFeature.duplicateNode(node);  		

					if (one != null && one.getSelectedFiles().size() > 0) {
						List<String> files = one.getSelectedFiles();
						for (Property p: node.getProperties()) {
							if (isHomeFile(p) && !files.contains(p.getValue())) {
								PropertyUtils.setProperty(newNode, p.getName(), "");
							}
						}
					}


					NodeType nodeType =  new NodeType(newNode);
					String renderedNode = WorkflowTypesManager.renderNodeType(nodeType);
					add(getPropFilePath(newNode), renderedNode, jos);
				}

				if (one != null && one.getSelectedFiles().size() > 0) {
					List<String> files = one.getSelectedFiles();
					for (Object o: files) {
						String rFile = o.toString();
						File aFile = new File(context, rFile);
						add(aFile, rFile, jos);
					}
				}
			}

		} catch (IOException e) {
			ErrorDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage(), WorkflowEditorPlugin.getDefault().newErrorStatus(e));
		}

		return true;
	}

	protected boolean isHomeFile(Property p) {
		return "local_file".equals(p.getType()) || "home_file".equals(p.getType());
	}

    public List<String> getPropFilePaths() {
    	List<String> paths = new ArrayList<>();
    	for (WFNode node: nodes) {
    		String path = getPropFilePath(node);
    		paths.add(path);
    		}		
    		return paths;
    	}

    	public String getPropFilePath(WFNode node) {
    		String name = node.getName().replaceAll("[^a-zA-Z0-9\\-]", "_");
    		String path = "META-INF/" + name + ".properties";
    		return path;
    	}

    	private void add(File source, String jarPath, JarOutputStream target) throws IOException {
    		BufferedInputStream in = null;
    		try {			
    			JarEntry entry = new JarEntry(jarPath);
    			entry.setTime(source.lastModified());
    			target.putNextEntry(entry);
    			in = new BufferedInputStream(new FileInputStream(source));

    			byte[] buffer = new byte[1024];
    			while (true) {
    				int count = in.read(buffer);
    				if (count == -1)
    					break;
    				target.write(buffer, 0, count);
    			}
    			target.closeEntry();
    		} finally {
    			if (in != null)
    				in.close();
    		}
    	}

    	private void add(String path, Object contents, JarOutputStream target) throws IOException {
    		JarEntry entry = new JarEntry(path);
    		entry.setTime(System.currentTimeMillis());
		target.putNextEntry(entry);
		String text = contents.toString();
		target.write(text.getBytes(), 0, text.length());			
		target.closeEntry();
	}
	
	private String[] getCandidateFiles(List<WFNode> nodes2) {
		List<String> files = new ArrayList<>();
		for (WFNode node: nodes2) {
			for (Property p: node.getProperties()) {
				if (isHomeFile(p) && !StringUtils.isEmpty(p.getValue())) {
					files.add(p.getValue());
				}
			}
		}
		return files.toArray(new String[files.size()]);
	}


}
