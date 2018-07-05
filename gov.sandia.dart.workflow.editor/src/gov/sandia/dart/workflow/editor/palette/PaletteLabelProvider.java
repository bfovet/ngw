/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.palette;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class PaletteLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		return ((PaletteEntryEditPart) element).getImage();
	}

	@Override
	public String getText(Object element) {
		return ((PaletteEntryEditPart) element).getText();
	}
}
