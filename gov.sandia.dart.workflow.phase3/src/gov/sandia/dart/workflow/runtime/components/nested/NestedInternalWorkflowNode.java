/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/**
 * 
 */
package gov.sandia.dart.workflow.runtime.components.nested;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;

/**
 * @author mjgibso
 *
 */
public class NestedInternalWorkflowNode extends NestedWorkflowNode
{
	public static final String FILE_CONTENTS = "fileContents";
	
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(
			new PropertyInfo(WORKDIR_NAME_TEMPLATE, "text"),
			new PropertyInfo(PRIVATE_WORK_DIR, "boolean", "true"),
			new PropertyInfo(FILE_CONTENTS, "multitext", "", true));
	}
	
	
	@Override
	protected File getSubWorkflowFile(Map<String, String> properties, RuntimeData runtime) throws IOException
	{
		File tmpFile = File.createTempFile(getName(), ".iwf.tmp");
		tmpFile.deleteOnExit();
		String fileContents = properties.get(FILE_CONTENTS);
		try(FileWriter fw = new FileWriter(tmpFile))
		{
			fw.write(fileContents);
		}
		
		return tmpFile;
	}
	
	@Override
	protected File getSubWorkflowHomeDir(File subWorkflowFile, RuntimeData runtime)
	{
		return runtime.getHomeDir();
	}
}
