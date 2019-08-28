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
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.graphiti.ui.platform.AbstractImageProvider;
 
public class WorkflowImageProvider extends AbstractImageProvider {
 
    public static final String PREFIX =  "ngw.";
 
    public static final String IMG_GEAR = PREFIX + "gear";
    public static final String IMG_LOOP = PREFIX + "loop";
    public static final String IMG_PALETTE = PREFIX + "palette";
    public static final String IMG_WORKFLOW = PREFIX + "nestedWorkflow";
    public static final String IMG_INTERNAL_WORKFLOW = PREFIX + "nestedInternalWorkflow";
    public static final String IMG_REMOTE_WORKFLOW = PREFIX + "remoteNestedWorkflow";

    public static final String IMG_DUPLICATE = PREFIX + "duplicate";
	public static final String IMG_PORTS = PREFIX + "ports";
	public static final String IMG_PLUG = PREFIX + "plug";
	public static final String IMG_PACKAGE = PREFIX + "pkg";
	public static final String IMG_GLOBE = PREFIX + "globe";

	public static final String IMG_PORT = PREFIX + "port";

	private static WorkflowImageProvider INSTANCE = null;
	
	public WorkflowImageProvider() {
		if(INSTANCE == null) {
			INSTANCE = this;
		}
	}
 
    @Override
    protected void addAvailableImages() {
        addImageFilePath(IMG_GEAR, "icons/gear.gif");
        addImageFilePath(IMG_LOOP, "icons/loop.png");
        addImageFilePath(IMG_PALETTE, "icons/palette.png");
        addImageFilePath(IMG_WORKFLOW, "icons/shapes.gif");
        addImageFilePath(IMG_INTERNAL_WORKFLOW, "icons/shapes.gif");
        addImageFilePath(IMG_REMOTE_WORKFLOW, "icons/shapes.gif");

        addImageFilePath(IMG_DUPLICATE, "icons/duplicate.png");
        addImageFilePath(IMG_PORTS, "icons/ports.gif");
        addImageFilePath(IMG_PORT, "icons/port.gif");
        
        addImageFilePath(IMG_PLUG, "icons/plug.png");
        addImageFilePath(IMG_PACKAGE, "icons/package.png");
        addImageFilePath(IMG_GLOBE, "icons/globe.gif");

        addImageFilePath(PREFIX + "add", "icons/add.png");
        addImageFilePath(PREFIX + "fail", "icons/fail.png");
        addImageFilePath(PREFIX + "subtract", "icons/subtract.png");
        addImageFilePath(PREFIX + "multiply", "icons/multiply.png");
        addImageFilePath(PREFIX + "divide", "icons/divide.png");        
        addImageFilePath(PREFIX + "compare", "icons/compare.png");        
        addImageFilePath(PREFIX + "constant", "icons/constant.png");        
        addImageFilePath(PREFIX + "print", "icons/print.gif");        
        addImageFilePath(PREFIX + "file", "icons/file.png");
        addImageFilePath(PREFIX + "folder", "icons/folder.png");        
        addImageFilePath(PREFIX + "externalProcess", "icons/externalProcess.png");        
        addImageFilePath(PREFIX + "cubit", "icons/cubit.gif");
        addImageFilePath(PREFIX + "bashScript", "icons/shell.png");
        addImageFilePath(PREFIX + "pythonScript", "icons/python.png");
        addImageFilePath(PREFIX + "tclScript", "icons/tclScript.png");
        addImageFilePath(PREFIX + "openFile", "icons/file.png");
        addImageFilePath(PREFIX + "prompt", "icons/ask.png");       
        addImageFilePath(PREFIX + "ask_yes_no", "icons/ask.png");
        addImageFilePath(PREFIX + "sierra", "icons/sierra.gif");

        addImageFilePath(PREFIX + "parameterFile", "icons/properties.png");
        addImageFilePath(PREFIX + "or", "icons/or.png");


   	 	IConfigurationElement[] extensions = Platform.getExtensionRegistry().getConfigurationElementsFor("gov.sandia.dart.workflow.editor.nodeIcon" );                      
   	 	for (final IConfigurationElement extension : extensions) {   
   	 		String contributorBundleName = extension.getContributor().getName();
   	 		String nodeType = extension.getAttribute("nodeType" );
   	 		String iconPath = extension.getAttribute("iconPath" );
   	 		String templateIconPath = "platform:/plugin/" + contributorBundleName +"/"+ iconPath;
   	 		addImageFilePath( PREFIX + nodeType, templateIconPath );    
   	 	}
    }

	public static WorkflowImageProvider get() {
		if(INSTANCE == null) {
			new WorkflowImageProvider();
		}
		return INSTANCE;
	}	
}
