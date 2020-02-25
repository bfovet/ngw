package gov.sandia.dart.workflow.runtime.components.script;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;

public class CubitPythonScriptNode extends PythonScriptNode {

	public CubitPythonScriptNode() throws IOException {}

	@Override
	protected
	PrintWriter initializeScript(File workDir, List<String> commandArgs, RuntimeData runtime) throws IOException {
		addInterpreterArguments("CUBIT_PYTHON", runtime, "cubit", commandArgs );		
		String scriptName = getFilenameRoot() + ".py";				
		File scriptFile = new File(workDir, scriptName);
		commandArgs.add("-nographics");
		commandArgs.add("-nojournal");
		commandArgs.add(scriptName);
		PrintWriter fos = new PrintWriter(scriptFile);
		fos.println("######################################################################");
		fos.println("##### DO NOT EDIT - WILL BE OVERWRITTEN ON EACH SCRIPT EXECUTION #####");
		fos.println("######################################################################");
		fos.println();
		fos.println("# beginning of workflow node variable definitions for ports and properties");
		fos.println();
		return fos;
	}

}
