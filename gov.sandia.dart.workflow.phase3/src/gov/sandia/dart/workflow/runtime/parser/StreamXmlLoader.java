/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.parser;

import java.io.InputStream;

import javax.xml.bind.JAXBException;

import com.googlecode.sarasvati.load.SarasvatiLoadException;
import com.googlecode.sarasvati.xml.XmlLoader;
import com.googlecode.sarasvati.xml.XmlProcessDefinition;

class StreamXmlLoader extends XmlLoader {
	public XmlProcessDefinition translate (InputStream stream) throws SarasvatiLoadException {
		XmlProcessDefinition def = null;
		try {
			def = (XmlProcessDefinition) getUnmarshaller().unmarshal( stream );
		} catch(JAXBException e) {
			throw new SarasvatiLoadException("Error while unmarshmalling workflow", e);
		}

		return def;
	}
}
