/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components;

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import java.io.File;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

//
// Experimental "unified file node"
//
// Requires a fileName property or input port
//
// Writes any data found on the "dataIn" port to the specified file (data is appended
// if the "append" property is true).
//
// Sends file data to "dataOut" port, if present.
//
// Sends file name to "fileReference" port, if present.
//
public class FileNode extends SAWCustomNode {
	public static String DATA_IN_PORT = "dataIn",
						DATA_OUT_PORT = "dataOut",
						FILE_OUT_PORT = "fileReference",
						FILE_NAME = "fileName";
	
	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		File targetFile = getFileFromPortOrProperty(runtime, properties, FILE_NAME, false, false);
		Map<String, Object> results = new HashMap<>();

		boolean appendFlag = getAppendFlag(properties);
		boolean sendDataOut = isConnectedOutput(DATA_OUT_PORT, workflow);
		boolean sendFile = isConnectedOutput(FILE_OUT_PORT, workflow);
				
		byte[] inBytes = (byte[]) runtime.getInput(getName(), DATA_IN_PORT, byte[].class);
		
		if (inBytes != null)
		{
			try { FileUtils.writeByteArrayToFile(targetFile, inBytes, appendFlag); }
			catch (Throwable t) { throw new SAWWorkflowException("Problem writing to file", t); }
			runtime.log().info("File node \"{0}\" wrote {1} bytes (MD5: {2}) to {3}", getName(), inBytes.length, computeChecksum(inBytes), targetFile.getPath());
		}
		
		if (sendDataOut)
		{
			if (inBytes == null || appendFlag)
			{
				try { inBytes = FileUtils.readFileToByteArray(targetFile); }
				catch (Throwable t) { throw new SAWWorkflowException("Problem reading from file", t); }
				runtime.log().info("File node \"{0}\" read {1} bytes (MD5: {2}) from {3}", getName(), inBytes.length, computeChecksum(inBytes), targetFile.getPath());
			}
			results.put(DATA_OUT_PORT, inBytes);
		}
		
		if (sendFile) // TODO: what type-dependent behavior should be here?
			results.put(FILE_OUT_PORT, targetFile.getAbsolutePath());
		
		return results;
	}
	
	// shamefully ripped off from com.strikeware.snl.apc.util.ComputeDigest
	// (to minimize lib dependencies)
	private String computeChecksum(byte[] bytes) {
        // Create our MessageDigest object with the specified algorithm.
        MessageDigest md;
        try
        {
            md = MessageDigest.getInstance( "md5" );
        }
        catch ( java.security.NoSuchAlgorithmException exc )
        {
            return "Error: cannot get MessageDigest for md5";
        }

        // Now compute the digest, turn them into hex digits, and return it.
        byte[] digestBytes = md.digest(bytes);
        StringBuffer sb = new StringBuffer( 2 * digestBytes.length );
        for ( int i = 0; i < digestBytes.length; i++ )
        {
            sb.append( Character.forDigit( ( ( digestBytes[i] >> 4 ) & 0x0F ), 16 ) );
            sb.append( Character.forDigit( ( digestBytes[i] & 0x0F ), 16 ) );
        }
        return sb.toString();

	}

	public boolean getAppendFlag(Map<String, String> properties) {
		String flagValue = properties.get("append");
		if (flagValue != null && flagValue.equals("true"))
			return true;
		else
			return false;
	}

	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo(FILE_NAME), new InputPortInfo(DATA_IN_PORT)); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo(FILE_OUT_PORT, "text"), new OutputPortInfo(DATA_OUT_PORT, "default")); }
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo(FILE_NAME, "home_file"), new PropertyInfo("append", "boolean")); }

	@Override public String getCategory() { return NodeCategories.INPUT_OUTPUT; }
}
