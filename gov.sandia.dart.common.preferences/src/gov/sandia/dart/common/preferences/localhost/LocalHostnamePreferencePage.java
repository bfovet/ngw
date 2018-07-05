/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*
 * Created by mjgibso on Jan 4, 2017 at 12:41:48 PM
 */
package gov.sandia.dart.common.preferences.localhost;

import gov.sandia.dart.common.core.localhostname.AbsHostnameInput;
import gov.sandia.dart.common.core.localhostname.EmptyHostnameInput;
import gov.sandia.dart.common.core.localhostname.HostnameInput;
import gov.sandia.dart.common.core.localhostname.IHostnameStrategy;
import gov.sandia.dart.common.core.localhostname.LocalhostStrategyFactory;
import gov.sandia.dart.common.preferences.CommonPreferencesPlugin;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author mjgibso
 *
 */
public class LocalHostnamePreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
	
	public static LocalHostnamePreferences prefs = LocalHostnamePreferences.getInstance();
	
	private final List<RadioButtonField> _fields = LocalhostStrategyFactory.getProvider().getStrategies().stream()
			.map(RadioButtonField::new).collect(Collectors.toList());
	
	private Button _validateB;
	private Text _validateT;
	
	/**
	 * 
	 */
	public LocalHostnamePreferencePage()
	{
		super();
		setDescription("Resolution method:");
	}
	
	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return CommonPreferencesPlugin.getDefault().getPreferenceStore();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench)
	{
		// no-op
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		
		Composite comp = new Composite(parent, SWT.NONE);
		
		int numColumns = 2;
		GridLayout layout = new GridLayout(numColumns, false);
		comp.setLayout(layout);
		
		// put warning up at the top that changes herein will require app restart to take effect...
		Label warningL = new Label(comp, SWT.NONE);
		warningL.setText("Note: preferences changed on this page won't take effect until the application is restarted.");
		warningL.setForeground(warningL.getDisplay().getSystemColor(SWT.COLOR_RED));
		GridDataFactory.generate(warningL, numColumns, 1);
		
		_fields.forEach(f -> f.create(comp, numColumns));
		
		_validateB = new Button(comp, SWT.PUSH);
		_validateB.setText("Validate");
		_validateB.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				runLookupAndUpdateLabel();
			}
		});
		
		_validateT = new Text(comp, SWT.BORDER);
		_validateT.setEditable(false);
		GridDataFactory.fillDefaults().applyTo(_validateT);
		

		// Load up the data
		_fields.forEach(RadioButtonField::load);
		
		
		// validate everything
		updateEnablements();
		checkState();
		
		return comp;
	}
	
	private void runLookupAndUpdateLabel()
	{
		String result;
		try {
			result = runLookup();
		} catch (Throwable t) {
			result = "Error running lookup: " + t.getMessage();
		}
		_validateT.setText(result);
	}
	
	private String runLookup() throws IOException, IllegalStateException
	{
		Optional<RadioButtonField> selected = _fields.stream().filter(RadioButtonField::isSelected).findAny();
		RadioButtonField field = selected.orElseThrow(() -> new IllegalStateException("No hostname lookup method selected"));
		URI uri = field.resolve();
		return uri.getHost();
	}
	
	@Override
	protected void performDefaults()
	{
		_fields.forEach(RadioButtonField::loadDefault);
		
		super.performDefaults();
	}
	
	@Override
	public boolean performOk()
	{
		_fields.forEach(RadioButtonField::store);
		
		return true;
	}
	
	private void updateEnablements()
	{
		this._fields.forEach(RadioButtonField::updateEnablements);
	}
	
	private void checkState()
	{
		boolean valid = _fields.stream().allMatch(RadioButtonField::checkState);
		
		_validateB.setEnabled(valid);
		
		setErrorMessage(valid ? null : "The selected resolution method requires a value be set");
		
		setValid(valid);
	}
	
	private class RadioButtonField
	{
		private final IHostnameStrategy _strategy;
		private Button _btn;
		private Text _field;
		
		private RadioButtonField(IHostnameStrategy strategy)
		{
			this._strategy = strategy;
		}
		
		public void create(Composite parent, int numColumns)
		{
			_btn = new Button(parent, SWT.RADIO);
			_btn.setText(this._strategy.getDescription());
			_btn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					LocalHostnamePreferencePage.this.updateEnablements();
					LocalHostnamePreferencePage.this.checkState();
				}
			});
			
			boolean hasField = this._strategy.needsInput();
			
			int hSpan = hasField ? Math.min(1, numColumns-1) : numColumns;
			
			GridDataFactory.generate(_btn, hSpan, 1);
			
			if(hasField)
			{
				_field = new Text(parent, SWT.BORDER);
				_field.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).create());
				_field.addModifyListener(new ModifyListener() {
					
					@Override
					public void modifyText(ModifyEvent e) {
						LocalHostnamePreferencePage.this.checkState();
					}
				});
			}
		}
		
		private URI resolve() throws IOException
		{
			AbsHostnameInput input = _strategy.needsInput() ? new HostnameInput(_field.getText()) : new EmptyHostnameInput();
			return _strategy.resolve(input);
		}
		
		public void load()
		{
			load(prefs.getSelected());
		}
		
		public void loadDefault()
		{
			load(prefs.getDefaultSelected());
		}
		
		private void load(IHostnameStrategy storedStrategy)
		{
			_btn.setSelection(StringUtils.equals(_strategy.getKey(), storedStrategy.getKey()));
			setData(prefs.getData(_strategy));
		}
		
		private void setData(String data)
		{
			if(_field != null)
			{
				_field.setText(data!=null ? data : "");
			}
		}
		
		public void store()
		{
			if(_btn.getSelection())
			{
				prefs.setSelection(_strategy);
			}
			
			if(_field != null)
			{
				prefs.setData(_strategy, _field.getText());
			}
		}
		
		public void updateEnablements()
		{
			if(_field != null)
			{
				_field.setEnabled(_btn.getSelection());
			}
		}
		
		public boolean checkState()
		{
			// if we're selected, and we have a field, and it's blank, then we're invalid
			return !(_btn.getSelection() && _field!=null && StringUtils.isBlank(_field.getText()));
		}
		
		public boolean isSelected()
		{
			return _btn.getSelection();
		}
	}
}
