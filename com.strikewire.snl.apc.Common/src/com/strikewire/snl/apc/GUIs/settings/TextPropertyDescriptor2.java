/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/**
 * 
 */
package com.strikewire.snl.apc.GUIs.settings;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author mjgibso
 *
 */
public class TextPropertyDescriptor2 extends TextPropertyDescriptor
{
	private final int controlStyle_;
	
	public TextPropertyDescriptor2(Object id, String displayName, int controlStyle)
	{
		super(id, displayName);
		
		this.controlStyle_ = controlStyle;
	}
	
	@Override
	public CellEditor createPropertyEditor(Composite parent)
	{
        CellEditor editor = new TextCellEditor(parent, controlStyle_);
        if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
        return editor;
	}
	
	public static TextPropertyDescriptor2 fromTextPropertyDescriptor(TextPropertyDescriptor descriptor, int controlStyle)
	{
		TextPropertyDescriptor2 descriptor2 = new TextPropertyDescriptor2(descriptor.getId(), descriptor.getDisplayName(), controlStyle);
		
//		descriptor2.setAlwaysIncompatible(descriptor.getAlwaysIncompatible());
		descriptor2.setCategory(descriptor.getCategory());
		descriptor2.setDescription(descriptor.getDescription());
		descriptor2.setFilterFlags(descriptor.getFilterFlags());
		descriptor2.setHelpContextIds(descriptor.getHelpContextIds());
		descriptor2.setLabelProvider(descriptor.getLabelProvider());
//		descriptor2.setValidator(descriptor.getValidator());
		
		return descriptor2;
	}
}
