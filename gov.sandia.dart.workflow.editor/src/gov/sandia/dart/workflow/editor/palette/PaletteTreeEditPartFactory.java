/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.palette;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteEntry;

public class PaletteTreeEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart parentEditPart, Object model) {
		if (model instanceof PaletteContainer)
			return new PaletteTreeNodeEditPart((PaletteContainer) model);
		if (model instanceof PaletteEntry)
			return new PaletteEntryEditPart((PaletteEntry) model);
		return null;
	}

}
