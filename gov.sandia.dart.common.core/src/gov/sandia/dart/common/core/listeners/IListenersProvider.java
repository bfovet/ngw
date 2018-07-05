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
 * Created by mjgibso on Jun 28, 2015 at 6:35:01 AM
 */
package gov.sandia.dart.common.core.listeners;

import java.util.Collection;

/**
 * @author mjgibso
 *
 */
public interface IListenersProvider<L>
{
	Collection<L> getListeners();
}
