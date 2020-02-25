/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.tb.IDecorator;

public class DecoratorManager  {
	private static Map<String, Map<EObject, IDecorator>> decorators = new HashMap<>();

	public static synchronized  Map<EObject, IDecorator> getDecoratorMap(Resource eResource) {
		String tag = eResource.toString();
		Map<EObject, IDecorator> map = decorators.get(tag);
		if (map == null) {
			decorators.put(tag, map = new HashMap<>());
		}
		return map;
	}
	
}
