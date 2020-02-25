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
 * Created by mjgibso on Jan 17, 2017 at 4:06:20 PM
 */
package gov.sandia.dart.common.preferences.localhost;

import gov.sandia.dart.common.core.localhostname.AbsHostnameInput;
import gov.sandia.dart.common.core.localhostname.AbsLocalhostStrategyProvider;
import gov.sandia.dart.common.core.localhostname.IHostnameStrategy;
import gov.sandia.dart.common.core.localhostname.LocalhostStrategyFactory;
import gov.sandia.dart.common.preferences.CommonPreferencesPlugin;

import java.util.Optional;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author mjgibso
 *
 */
public class LocalhostPreferenceInitializer extends AbstractPreferenceInitializer
{

	/**
	 * 
	 */
	public LocalhostPreferenceInitializer()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IPreferenceStore store = CommonPreferencesPlugin.getDefault().getPreferenceStore();
		
		AbsLocalhostStrategyProvider provider = LocalhostStrategyFactory.getProvider();
		
		IHostnameStrategy strategy = provider.getDefaultStrategy();
		
		store.setDefault(LocalHostnamePreferences.PREF_KEY, strategy.getKey());
		
		provider.getStrategies().forEach(s -> setDefaultInput(store, provider, s));
		
		// Don't default the static manual entry, as then it could bounce around if they select it and don't
		// change it.  So by leaving it blank, they will be forced to set it to something when they select
		// that option (by the validator), and thus it will be set to something static, which is the whole
		// point of that option.
	}
	
	
	private void setDefaultInput(IPreferenceStore prefs, AbsLocalhostStrategyProvider provider, IHostnameStrategy strategy)
	{
		AbsHostnameInput input = provider.getDefaultInput(strategy);
		Optional<String> parameter = input.getParameter();
		parameter.ifPresent(p -> prefs.setDefault(LocalHostnamePreferences.getDataKey(strategy), p));
	}
	
	
}
