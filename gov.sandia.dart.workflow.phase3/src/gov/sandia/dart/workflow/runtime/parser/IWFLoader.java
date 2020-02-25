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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.googlecode.sarasvati.xml.XmlProcessDefinition;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class IWFLoader {

	public static XmlProcessDefinition translateProcessDefinition(File iwfFile, String name, WorkflowDefinition workflow, RuntimeData data) throws IOException {
	
		/*
		 * Note: temporary code. We're going to read in an IWF file, and write
		 * out a Sarasvati input file, which we then parse; all the information
		 * for the runtime system has been encoded in the Sarasvati file. We
		 * should modify this to use the runtime info out of the IWF directly,
		 * and only write out a vanilla Sarasvati input. This will be one less 
		 * thing to port to a new workflow engine.
		 */		
		
		IWFParser parser = new IWFParser();
		List<?> objects = parser.parse(iwfFile);
		return translateProcessDefinition(objects, name, workflow, data);
	}

	public static XmlProcessDefinition translateProcessDefinition(List<?> objects, String name, WorkflowDefinition workflow,
			RuntimeData data) {
		String sarasvatiData = SarasvatiWriter.emitSarasvatiWorkflow(objects);
	
		// Show Sarasvati input when needed for debugging
		// System.out.println(sarasvatiData);
			
		StreamXmlLoader xmlLoader = new StreamXmlLoader();
		XmlProcessDefinition def = xmlLoader.translate(IOUtils.toInputStream(sarasvatiData, Charset.defaultCharset()));		
		def.setName(name);
		List<Object> customs = def.getCustomProcessData();
		CustomXMLParser.parse(customs, workflow, data);
			
		return def;
	}

}
