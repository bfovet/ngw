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
package com.strikewire.snl.apc.GUIs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.PropertyDescriptor;


/**
 * @author mjgibso
 *
 */
public class CheckListDialogPropertyDescriptor<E> extends PropertyDescriptor
{
	private class MyDialogCellEditor extends DialogCellEditor
	{
		private MyDialogCellEditor(Composite parent)
		{
			super(parent);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected Object openDialogBox(Control cellEditorWindow) {
			CheckListDialog<E> cld = new CheckListDialog<E>(
					cellEditorWindow.getShell(), // parent shell
					choices_, // choices
					superGetLabelProvider(), // label provider
					shellTitle_, // shell title
					dialogTitle_, // dialog title
					message_ // dialog message
					);
			cld.setSingleSelection(singleSelection_);
			Object value = doGetValue();
			Collection<E> elements = getElementsFromObject(value);
			if(elements != null)
			{
				cld.setInitialSelection((E[]) elements.toArray());
			}
			if(cld.open() != Window.OK)
			{
				return null;
			}
			
			List<E> result = cld.getResult();
			if(singleSelection_)
			{
				if(result!=null && result.size()>0)
				{
					return result.get(0);
				} else {
					return null;
				}
			} else {
				return result;
			}
		}
		
		@Override
		protected void updateContents(Object value)
		{
			String text = getLabelProvider().getText(value);
			super.updateContents(text);
		}
	}
	
	private final LabelProvider cellLabelProvider_ = new LabelProvider() {
		public String getText(Object element) {
			if(singleSelection_)
			{
				return superGetLabelProvider().getText(element);
			} else {
				Collection<E> elements = getElementsFromObject(element);
				if(elements != null)
				{
					return getDisplayValue(elements);
				}
			}
			
			String text = super.getText(element);
			return text;
		};
	};
	
	private final List<E> choices_;
	private final boolean singleSelection_;
	private final String shellTitle_;
	private final String dialogTitle_;
	private final String message_;
	
	public CheckListDialogPropertyDescriptor(Object id, String displayName, List<E> choices, boolean singleSelection, String shellTitle, String dialogTitle, String message)
	{
		super(id, displayName);
		
		this.choices_ = choices;
		this.singleSelection_ = singleSelection;
		this.shellTitle_ = shellTitle;
		this.dialogTitle_ = dialogTitle;
		this.message_ = message;
	}
	
	@Override
	public CellEditor createPropertyEditor(Composite parent)
	{
		return new MyDialogCellEditor(parent);
	}
	
	protected String getDisplayValue(Collection<E> values)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if(values!=null && values.size()>0)
		{
			boolean first = true;
			for(E value : values)
			{
				if(first)
				{
					first = false;
				} else {
					sb.append(", ");
				}
				
				sb.append(superGetLabelProvider().getText(value));
			}
		}
		
		sb.append("]");
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	private Collection<E> getElementsFromObject(Object obj)
	{
		try {
			List<E> elements = new ArrayList<E>();
			if(obj instanceof Collection)
			{
				for(Object e : (Collection<?>) obj)
				{
					elements.add((E) e);
				}
			} else if(obj instanceof Object[]) {
				for(Object e : (Object[]) obj)
				{
					elements.add((E) e);
				}
			} else {
				elements.add((E) obj);
			}
			return elements;
		} catch(ClassCastException cce) {
			// noop
		}
		
		return null;
	}
	
	private ILabelProvider superGetLabelProvider()
	{
		return super.getLabelProvider();
	}
	
	@Override
	public ILabelProvider getLabelProvider()
	{
		return cellLabelProvider_;
	}
}
