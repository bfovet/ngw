package gov.sandia.dart.workflow.app.ApcWorkbench;

public class ApcWorkbenchOptions {

	private final static String FLAG_NO_GUI = "nogui";
	private final static String OPTION_PYTHON_SCRIPT = "p";
	private final static String OPTION_PROJECT = "project";
	
	private final static String [] flags = new String [] {FLAG_NO_GUI};
	private final static String [] options = new String [] {OPTION_PYTHON_SCRIPT, OPTION_PROJECT};

	private GetOpt getOpt;
	
	public ApcWorkbenchOptions(String [] args) throws GetOptException {
		getOpt = new GetOpt(args, flags, options);
	}
	
	public boolean getNoGui() {
		return getOpt.hasOption(FLAG_NO_GUI);
	}
	
	public String getPythonScript()
	{
		return getOpt.getOption(OPTION_PYTHON_SCRIPT);
	}
	
	public String getProject()
	{
		return getOpt.getOption(OPTION_PROJECT);
	}
	
	public String [] getParams()
	{
		return getOpt.params();
	}
	
}
