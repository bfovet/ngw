/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.editor;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

public class EmbeddedImageProvider extends AbstractImageProvider {
	 
    public static final String PREFIX =  "gov.sandia.dart.workflow.phase3.embedded.";
    public static final String RUN =  PREFIX + "run";


	private static EmbeddedImageProvider INSTANCE = null;
	
	public EmbeddedImageProvider() {
		INSTANCE = this;
	}
 
    @Override
    protected void addAvailableImages() {
        addImageFilePath(RUN, "icons/i16play.png");
    }

	public static EmbeddedImageProvider get() {
		return INSTANCE;
	}
}
