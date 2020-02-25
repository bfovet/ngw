/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components;

import java.io.File;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

//
// Unified file node
//
// Requires a fileName property or input port
//
// If data is found on the "dataIn" port,
//   + relative pathnames are resolved relative to the component work directory,
//   + data is written (or appended according to append property) to the specified file.
// Otherwise,
//   + relative pathnames are resolved relative to the workflow home directory.
//
// Sends file data to "dataOut" port, if connected.
//
// Sends file name to "fileReference" port, if connected.
//
public class FileNode extends SAWCustomNode {
	private static final String APPEND = "append";
	private static final String CHECK_EXISTS = "checkExists";
	public static String DATA_IN_PORT = "dataIn",
						DATA_OUT_PORT = "dataOut",
						FILE_OUT_PORT = "fileReference",
						FILE_NAME = "fileName";
	
	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		byte[] inBytes = (byte[]) runtime.getInput(getName(), DATA_IN_PORT, byte[].class);

		String targetFileName = getStringFromPortOrProperty(runtime, properties, FILE_NAME);
		File targetFile = new File(targetFileName);
		if (!targetFile.isAbsolute()) {
			if (inBytes != null) {
				targetFile = new File(getComponentWorkDir(runtime, properties), targetFileName);
			} else {
				targetFile = new File(runtime.getHomeDir(), targetFileName);
			}
		}

		boolean appendFlag = getAppendFlag(properties);
		boolean checkExists = getCheckExistsFlag(properties);
		
		if (checkExists && !targetFile.exists()) {
			throw new SAWWorkflowException(String.format("Node %s: file '%s' does not exist", getName(), targetFile.getAbsolutePath()));
		}

		boolean sendDataOut = isConnectedOutput(DATA_OUT_PORT, workflow);
		boolean sendFile = isConnectedOutput(FILE_OUT_PORT, workflow);
		
		if (inBytes != null)
		{
			try { FileUtils.writeByteArrayToFile(targetFile, inBytes, appendFlag); }
			catch (Throwable t) { throw new SAWWorkflowException("Problem writing to file", t); }
			runtime.log().info("File node \"{0}\" wrote {1} bytes (MD5: {2}) to {3}", getName(), inBytes.length, computeChecksum(inBytes), targetFile.getPath());
		}

		Map<String, Object> results = new HashMap<>();

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
		return "true".equals(properties.get(APPEND));
	}
	
	public boolean getCheckExistsFlag(Map<String, String> properties) {
		return "true".equals(properties.get(CHECK_EXISTS));
	}


	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo(FILE_NAME), new InputPortInfo(DATA_IN_PORT)); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo(FILE_OUT_PORT, "text"), new OutputPortInfo(DATA_OUT_PORT, "default")); }
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo(FILE_NAME, "home_file"), new PropertyInfo(APPEND, "boolean"), new PropertyInfo(CHECK_EXISTS, "boolean")); }

	@Override public String getCategory() { return NodeCategories.FILES; }
}
