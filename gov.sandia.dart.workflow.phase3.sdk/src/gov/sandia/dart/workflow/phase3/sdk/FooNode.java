package gov.sandia.dart.workflow.phase3.sdk;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class FooNode extends SAWCustomNode {

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow,
			RuntimeData runtime) {
		String input = getStringFromPortOrProperty(runtime, properties, "input") + ": foo";
		File dataFile = new File(getComponentWorkDir(runtime, properties), "foo.txt");
		try {
			FileUtils.write(dataFile, input, Charset.defaultCharset());
			return Collections.singletonMap("foo", dataFile.getAbsolutePath());
		} catch (IOException e) {
			throw new SAWWorkflowException(getName() + ": Error writing result file", e);
		}
	}
}
