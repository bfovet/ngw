/**
 * 
 */
package gov.sandia.dart.workflow.runtime.components;

import java.util.Arrays;
import java.util.List;

/**
 * @author mjgibso
 *
 */
public class NestedInternalWorkflowNode extends NestedWorkflowNode
{
	public static final String FILE_CONTENTS = "fileContents";
	
	@Override public List<String> getDefaultProperties() { return Arrays.asList("conductor"); }
	@Override public List<String> getDefaultPropertyTypes() { return Arrays.asList("conductor"); }	

}
