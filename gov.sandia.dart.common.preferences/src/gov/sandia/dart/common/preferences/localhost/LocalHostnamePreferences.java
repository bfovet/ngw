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
 * Created by mjgibso on Jan 4, 2017 at 4:46:03 PM
 */
package gov.sandia.dart.common.preferences.localhost;

import gov.sandia.dart.common.core.localhostname.AbsLocalhostStrategyProvider;
import gov.sandia.dart.common.core.localhostname.IHostnameStrategy;
import gov.sandia.dart.common.core.localhostname.LocalhostStrategyFactory;
import gov.sandia.dart.common.preferences.CommonPreferencesPlugin;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author mjgibso
 *
 */
public class LocalHostnamePreferences implements ILocalHostnamePreferences
{
	public static final String PREF_KEY = "hostname.lookup";

	private final IPreferenceStore _store;
	
	private LocalHostnamePreferences()
	{
		this._store = CommonPreferencesPlugin.getDefault().getPreferenceStore();
	}
	
	/* (non-Javadoc)
	 * @see gov.sandia.dart.preferences.ILocalHostnamePreferences#getSelected()
	 */
	@Override
	public IHostnameStrategy getSelected()
	{
		return getSelected(false);
	}
	
	public IHostnameStrategy getDefaultSelected()
	{
		return getSelected(true);
	}
	
	private IHostnameStrategy getSelected(boolean getDefault)
	{
		String selection = getDefault ? _store.getDefaultString(PREF_KEY) : _store.getString(PREF_KEY);
		
		AbsLocalhostStrategyProvider provider = LocalhostStrategyFactory.getProvider();
		
		IHostnameStrategy retValue = provider.getDefaultStrategy();
		
		if(StringUtils.isNotBlank(selection))
		{
			Optional<IHostnameStrategy> strat = provider.getStrategies().stream().filter(s -> StringUtils.equals(s.getKey(), selection)).findAny();
			if(strat.isPresent())
			{
				retValue = strat.get();
			}
		}
		
		return retValue;
	}
	
	@Override
	public String getData(IHostnameStrategy strategy)
	{
		return _store.getString(getDataKey(strategy));
	}
	
	public String getDefaultData(IHostnameStrategy strategy)
	{
		return _store.getDefaultString(getDataKey(strategy));
	}
	
	public void setSelection(IHostnameStrategy strategy)
	{
		_store.setValue(PREF_KEY, strategy.getKey());
	}
	
	public void setData(IHostnameStrategy strategy, String data)
	{
		_store.setValue(getDataKey(strategy), data);
	}
	
	public static String getDataKey(IHostnameStrategy strategy)
	{
		return PREF_KEY + '.' + strategy.getKey();
	}
	
	public static LocalHostnamePreferences getInstance()
	{
		return LHPHolder.instance;
	}
	
	private static class LHPHolder
	{
		public static LocalHostnamePreferences instance = new LocalHostnamePreferences();
	}
}
