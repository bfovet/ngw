package gov.sandia.dart.workflow.runtime.components;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;

public abstract class AbstractNestedWorkflowNode extends SAWCustomNode {

	protected static final String WORKDIR_NAME_TEMPLATE = "workdirNameTemplate";

	public AbstractNestedWorkflowNode() {
		super();
	}

	protected String getWorkdirName(Map<String, String> properties, String sampleId) {
		String template = properties.get(WORKDIR_NAME_TEMPLATE);
		if (StringUtils.isEmpty(template))
			template = "workdir";
		return template + sampleId;
	}
	
}