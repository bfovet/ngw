/**
 * 
 */
package gov.sandia.dart.workflow.runtime.components;

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
	
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo("conductor", "conductor")); }
//	@Override public List<String> getDefaultPropertyTypes() { return Arrays.asList("conductor"); }
	
	
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
