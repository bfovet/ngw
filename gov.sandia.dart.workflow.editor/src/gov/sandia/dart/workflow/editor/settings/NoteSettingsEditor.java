/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.settings;

import java.util.Objects;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.strikewire.snl.apc.GUIs.settings.AbstractSettingsEditor;
import com.strikewire.snl.apc.GUIs.settings.IContextMenuRegistrar;
import com.strikewire.snl.apc.GUIs.settings.IMessageView;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;

import gov.sandia.dart.common.preferences.CommonPreferencesPlugin;
import gov.sandia.dart.common.preferences.settings.ISettingsViewPreferences;
import gov.sandia.dart.workflow.domain.Note;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.rendering.NoteGARenderer;

public class NoteSettingsEditor extends AbstractSettingsEditor<Note> {

	private Note note;
	private Image image;
	private Text text;
	private Combo color;	
	/**
	 * Font and color stuff commented out for now.
	 */
	
	@Override
	public void createPartControl(IManagedForm mform, IMessageView messageView, MultiControlSelectionProvider selectionProvider, IContextMenuRegistrar ctxMenuReg)
	{
		super.createPartControl(mform, messageView, selectionProvider, ctxMenuReg);
		ImageDescriptor desc = WorkflowEditorPlugin.getImageDescriptor("/icons/shapes.gif");
		image = desc!=null ? desc.createImage() : null;		
		form.setImage(image);
		
		form.getBody().setLayout(new GridLayout(1, false));		
		Section textSection = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		textSection.setText("Text");
		textSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		text = toolkit.createText(textSection, "", SWT.MULTI | SWT.WRAP);
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				try {
					if (note == null)
						return;
					String value = text.getText();
					if (Objects.equals(value, note.getText()))
						return;

					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(note);
	        				domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() {
							note.setText(value);
						}
					});

				} catch (Exception e2) {
					WorkflowEditorPlugin.getDefault().logError("Can't find property in object", e2);
				}
			}
		});
		textSection.setClient(text);		
		
		color = createComboControl(form.getBody(), NoteGARenderer.COLORS);

	}

	protected Combo createComboControl(Composite composite, String[] items) {
		String propertyName = "Color";		
		Composite row = toolkit.createComposite(composite);
		IPreferenceStore store = CommonPreferencesPlugin.getDefault().getPreferenceStore();
		if (store.getBoolean(ISettingsViewPreferences.DRAW_BORDERS)) {
			toolkit.paintBordersFor(row);
		}

		row.setLayout(new GridLayout(2, false));
		row.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		toolkit.createLabel(row, propertyName);

		Combo combo = new Combo(row, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		combo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				try {
					if (note == null)
					{
						return;
					}
					String value = combo.getText();
					String current = note.getColor(); 
					if (Objects.equals(value, current))
						return;

					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(note);
	        			domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() { 
							note.setColor(value);
						}
					});

				} catch (Exception e2) {
					WorkflowEditorPlugin.getDefault().logError("Can't process note color ", e2);
				}
			}


		});
		 
		combo.setItems(items);			
		return combo;
	}	

	
	
	
	@Override
	public void setNode(Note node) {
		form.setText("Workflow Annotation");
		this.note = node;		
		text.setText(node.getText());
		color.setText(node.getColor() == null ? "yellow" : node.getColor());

	}

	@Override
	public Note getNode() {
		return note;
	}

	@Override
	public void dispose() {
		note = null;
		if (image != null)
			image.dispose();
	}
}
