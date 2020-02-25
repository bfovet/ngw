/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.aprepro.pref;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.service.datalocation.Location;

import gov.sandia.dart.aprepro.ApreproPlugin;
import gov.sandia.dart.common.core.env.OS;

public class ApreproInitializer extends AbstractPreferenceInitializer {
	
	@Override
	public void initializeDefaultPreferences()
	{
		IPreferenceStore store = ApreproPlugin.getDefault().getPreferenceStore();

		if(store == null)
		{
			return;
		}
		
		String envApreproPath = System.getenv("APREPRO_PATH");
		String defaultAprepro = StringUtils.isNotBlank(envApreproPath) ? envApreproPath : getDefaultApreproExecutablePath();

		store.setDefault(ApreproConstants.COMMENT_PARAMS_ID, "#");
//		store.setDefault(ApreproConstants.IS_USING_USER_APREPRO, false);
		store.setDefault(ApreproConstants.APREPRO_EXECUTABLE, defaultAprepro);
		
//		store.setDefault(ApreproConstants.APREPRO_USAGE_ID, true);
//		store.setDefault(ApreproConstants.DEPREPRO_USAGE_ID, false);

	}
	
	private String getDefaultApreproExecutablePath()
	{
		try {
			Location installLoc = Platform.getInstallLocation();
			URL installLocURL = installLoc.getURL();
			String installLocPath = installLocURL.getPath();
			IPath installLocIPath = new Path(installLocPath);
			String apreproExecutable = "aprepro";
			if(OS.isWindows())
			{
				apreproExecutable += ".exe";
			}
			IPath apreproPath = installLocIPath.append(new Path("tools/"+apreproExecutable));
			return apreproPath.toOSString();
		} catch (Throwable t) {
			t.printStackTrace();
			return "";
		}
	}

}
