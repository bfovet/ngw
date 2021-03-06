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
 * Created by mjgibso on Jan 4, 2017 at 4:47:07 PM
 */
package gov.sandia.dart.common.preferences.localhost;

import gov.sandia.dart.common.core.localhostname.IHostnameStrategy;

/**
 * @author mjgibso
 *
 */
public interface ILocalHostnamePreferences
{
	IHostnameStrategy getSelected();
	
	String getData(IHostnameStrategy strategy);
}
