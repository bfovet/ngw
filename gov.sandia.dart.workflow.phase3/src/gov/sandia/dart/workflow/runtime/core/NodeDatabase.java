/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import gov.sandia.dart.workflow.runtime.components.AcosNode;
import gov.sandia.dart.workflow.runtime.components.AddNode;
import gov.sandia.dart.workflow.runtime.components.ApostproNode;
import gov.sandia.dart.workflow.runtime.components.AsinNode;
import gov.sandia.dart.workflow.runtime.components.AtanNode;
import gov.sandia.dart.workflow.runtime.components.ColumnNode;
import gov.sandia.dart.workflow.runtime.components.CompareNode;
import gov.sandia.dart.workflow.runtime.components.ConstantNode;
import gov.sandia.dart.workflow.runtime.components.CosNode;
import gov.sandia.dart.workflow.runtime.components.DecrementNode;
import gov.sandia.dart.workflow.runtime.components.DivideNode;
import gov.sandia.dart.workflow.runtime.components.EndLoopNode;
import gov.sandia.dart.workflow.runtime.components.ExitNode;
import gov.sandia.dart.workflow.runtime.components.ExpNode;
import gov.sandia.dart.workflow.runtime.components.ExternalProcessNode;
import gov.sandia.dart.workflow.runtime.components.FailNode;
import gov.sandia.dart.workflow.runtime.components.FileNode;
import gov.sandia.dart.workflow.runtime.components.FolderNode;
import gov.sandia.dart.workflow.runtime.components.ForLoopNode;
import gov.sandia.dart.workflow.runtime.components.GnuplotNode;
import gov.sandia.dart.workflow.runtime.components.IncrementNode;
import gov.sandia.dart.workflow.runtime.components.ListWorkflowConductor;
import gov.sandia.dart.workflow.runtime.components.LnNode;
import gov.sandia.dart.workflow.runtime.components.LogNode;
import gov.sandia.dart.workflow.runtime.components.MaxNode;
import gov.sandia.dart.workflow.runtime.components.MeanNode;
import gov.sandia.dart.workflow.runtime.components.MinNode;
import gov.sandia.dart.workflow.runtime.components.MultiSwitchNode;
import gov.sandia.dart.workflow.runtime.components.MultiplyNode;
import gov.sandia.dart.workflow.runtime.components.NegateNode;
import gov.sandia.dart.workflow.runtime.components.NestedInternalWorkflowNode;
import gov.sandia.dart.workflow.runtime.components.NestedWorkflowNode;
import gov.sandia.dart.workflow.runtime.components.ParameterNode;
import gov.sandia.dart.workflow.runtime.components.PiNode;
import gov.sandia.dart.workflow.runtime.components.PowNode;
import gov.sandia.dart.workflow.runtime.components.PrintNode;
import gov.sandia.dart.workflow.runtime.components.RandomNode;
import gov.sandia.dart.workflow.runtime.components.RegexNode;
import gov.sandia.dart.workflow.runtime.components.RejoinCheckpointNode;
import gov.sandia.dart.workflow.runtime.components.RepeatWorkflowConductor;
import gov.sandia.dart.workflow.runtime.components.ScriptNode;
import gov.sandia.dart.workflow.runtime.components.SetCheckpointNode;
import gov.sandia.dart.workflow.runtime.components.SignumNode;
import gov.sandia.dart.workflow.runtime.components.SimpleWorkflowConductor;
import gov.sandia.dart.workflow.runtime.components.SinNode;
import gov.sandia.dart.workflow.runtime.components.SleepNode;
import gov.sandia.dart.workflow.runtime.components.SqrtNode;
import gov.sandia.dart.workflow.runtime.components.SquareNode;
import gov.sandia.dart.workflow.runtime.components.StdDevNode;
import gov.sandia.dart.workflow.runtime.components.StringCatNode;
import gov.sandia.dart.workflow.runtime.components.StringCompareNode;
import gov.sandia.dart.workflow.runtime.components.StringReplaceNode;
import gov.sandia.dart.workflow.runtime.components.StringSearchNode;
import gov.sandia.dart.workflow.runtime.components.StringTemplateSubstitutionNode;
import gov.sandia.dart.workflow.runtime.components.SubstringNode;
import gov.sandia.dart.workflow.runtime.components.SubtractNode;
import gov.sandia.dart.workflow.runtime.components.SumNode;
import gov.sandia.dart.workflow.runtime.components.SweepWorkflowConductor;
import gov.sandia.dart.workflow.runtime.components.TanNode;
import gov.sandia.dart.workflow.runtime.components.WorkflowConductor;
import gov.sandia.dart.workflow.runtime.components.aprepro.ApreproNode;
import gov.sandia.dart.workflow.runtime.components.cubit.CubitComponentNode;
import gov.sandia.dart.workflow.runtime.components.localsubmit.LocalSierraSubmit;
import gov.sandia.dart.workflow.runtime.components.remote.DownloadFileNode;
import gov.sandia.dart.workflow.runtime.components.remote.RemoteCommandNode;
import gov.sandia.dart.workflow.runtime.components.remote.UploadFileNode;
import gov.sandia.dart.workflow.runtime.components.script.BashScriptNode;
import gov.sandia.dart.workflow.runtime.components.script.CshScriptNode;
import gov.sandia.dart.workflow.runtime.components.script.PythonScriptNode;
import gov.sandia.dart.workflow.runtime.components.script.WindowsBatchScriptNode;
import gov.sandia.dart.workflow.runtime.util.Indenter;

public class NodeDatabase {

	private static Map<String, Class<? extends SAWCustomNode>> nodeTypes;
	private static Map<String, Class<? extends WorkflowConductor>> conductorTypes;
	
	public synchronized static Map<String, Class<? extends SAWCustomNode>> nodeTypes() {
		if (nodeTypes == null)
			throw new SAWWorkflowException("Node database not loaded");
		return nodeTypes;
	}
	
	public synchronized static Map<String, Class<? extends WorkflowConductor>> conductorTypes() {
		if (conductorTypes == null)
			throw new SAWWorkflowException("Node database not loaded");
		return conductorTypes;
	}	
	
	public static synchronized void dump(String dumpFileName) throws IOException, InstantiationException, IllegalAccessException {
		if (nodeTypes == null)
			throw new IOException("Node database not loaded");
		
		try (Indenter out = new Indenter(new FileWriter(dumpFileName))) {
			out.printAndIndent("<workflowData>");
			dumpNodeTypes(nodeTypes, out);
			dumpConductorTypes(conductorTypes, out);

			out.unindentAndPrint("</workflowData>");
		}
	}
	
	private static void dumpConductorTypes(Map<String, Class<? extends WorkflowConductor>> conductorTypes, Indenter out) throws InstantiationException, IllegalAccessException {
		out.printAndIndent("<conductorTypes>");
		for (String name : conductorTypes.keySet()) {
			Class<? extends WorkflowConductor> clazz = conductorTypes.get(name);
			WorkflowConductor node = clazz.newInstance();
			out.printAndIndent(String.format("<conductorType name='%s'>", StringEscapeUtils.escapeXml10(name)));
			List<String> properties = node.getDefaultProperties();
			List<String> types = node.getDefaultPropertyTypes();
			if (!properties.isEmpty()) {
				out.printAndIndent("<properties>");
				for (int i = 0; i<properties.size(); ++i) {
					String property = properties.get(i);
					String type = types.get(i);
					out.printIndented(String.format("<property name='%s' type='%s'/>", StringEscapeUtils.escapeXml10(property), StringEscapeUtils.escapeXml10(type)));
				}
				out.unindentAndPrint("</properties>");
			}
			out.unindentAndPrint("</conductorType>");

		}
		out.unindentAndPrint("</conductorTypes>");	
	}

	private static void dumpNodeTypes(Map<String, Class<? extends SAWCustomNode>> nodeTypes, Indenter out)
			throws InstantiationException, IllegalAccessException {
		out.printAndIndent("<nodeTypes>");
		for (String name : nodeTypes.keySet()) {
			Class<? extends SAWCustomNode> clazz = nodeTypes.get(name);
			SAWCustomNode node = clazz.newInstance();
			String category = node.getCategory();
			out.printAndIndent(String.format("<nodeType name='%s' category='%s'>", StringEscapeUtils.escapeXml10(name), StringEscapeUtils.escapeXml10(category)));

			List<String> properties = node.getDefaultProperties();
			List<String> types = node.getDefaultPropertyTypes();
			if (!properties.isEmpty()) {
				out.printAndIndent("<properties>");
				for (int i = 0; i<properties.size(); ++i) {
					String property = properties.get(i);
					String type = types.get(i);
					out.printIndented(String.format("<property name='%s' type='%s'/>", StringEscapeUtils.escapeXml10(property), StringEscapeUtils.escapeXml10(type)));
				}
				out.unindentAndPrint("</properties>");
			}

			List<String> inputs = node.getDefaultInputNames();
			types = node.getDefaultInputTypes();
			if (!inputs.isEmpty()) {
				out.printAndIndent("<inputs>");
				for (int i=0; i<inputs.size(); ++i) {
					String input = inputs.get(i);
					String type = types.get(i);
					out.printIndented(String.format("<input name='%s' type='%s'/>", StringEscapeUtils.escapeXml10(input), StringEscapeUtils.escapeXml10(type)));
				}
				out.unindentAndPrint("</inputs>");
			}
			List<String> outputs = node.getDefaultOutputNames();
			types = node.getDefaultOutputTypes();

			if (!outputs.isEmpty()) {
				out.printAndIndent("<outputs>");
				for (int i=0; i<outputs.size(); ++i) {
					String output = outputs.get(i);
					String type = types.get(i);
					out.printIndented(String.format("<output name='%s' type='%s'/>", StringEscapeUtils.escapeXml10(output), StringEscapeUtils.escapeXml10(type)));
				}
				out.unindentAndPrint("</outputs>");
			}

			out.unindentAndPrint("</nodeType>");
		}
		out.unindentAndPrint("</nodeTypes>");
	}

	public synchronized static void loadDefinitions(SAWWorkflowLogger log, File pluginDir) {
		if (nodeTypes != null)
			return;
		
		nodeTypes = new HashMap<>();
		conductorTypes = new HashMap<>();

		// TODO This ought to be moved to a configuration file.
		nodeTypes.put("add", AddNode.class);
		nodeTypes.put("subtract", SubtractNode.class);
		nodeTypes.put("multiply", MultiplyNode.class); 
		nodeTypes.put("divide", DivideNode.class);
		nodeTypes.put("square", SquareNode.class);
		nodeTypes.put("negate", NegateNode.class);
		nodeTypes.put("sin", SinNode.class);
		nodeTypes.put("cos", CosNode.class);
		nodeTypes.put("tan", TanNode.class);
		nodeTypes.put("asin", AsinNode.class);
		nodeTypes.put("acos", AcosNode.class);
		nodeTypes.put("atan", AtanNode.class);
	
		nodeTypes.put("exp", ExpNode.class);
		nodeTypes.put("pow", PowNode.class);
		nodeTypes.put("ln", LnNode.class);
		nodeTypes.put("log", LogNode.class);
		nodeTypes.put("sqrt", SqrtNode.class);
		nodeTypes.put("signum", SignumNode.class);
		nodeTypes.put("pi",  PiNode.class);
		
		nodeTypes.put("stringCompare", StringCompareNode.class);
		nodeTypes.put("concat", StringCatNode.class);
		nodeTypes.put("stringSearch", StringSearchNode.class);
		nodeTypes.put("substring", SubstringNode.class);
		nodeTypes.put("replaceAll", StringReplaceNode.class);
		nodeTypes.put("templateSubstitution", StringTemplateSubstitutionNode.class);
				
		nodeTypes.put("print", PrintNode.class);
		nodeTypes.put("constant", ConstantNode.class);
		nodeTypes.put("parameter", ParameterNode.class);
		nodeTypes.put("compare", CompareNode.class);
		nodeTypes.put("fail", FailNode.class);
		nodeTypes.put("exit", ExitNode.class);
		nodeTypes.put("random", RandomNode.class);
		nodeTypes.put("sleep", SleepNode.class);

		nodeTypes.put("regex", RegexNode.class);
		nodeTypes.put("increment", IncrementNode.class);
		nodeTypes.put("decrement", DecrementNode.class);
		nodeTypes.put("file", FileNode.class);
		nodeTypes.put("folder", FolderNode.class);
		nodeTypes.put("externalProcess", ExternalProcessNode.class);
		nodeTypes.put("script", ScriptNode.class);
		nodeTypes.put("apostpro", ApostproNode.class);
		nodeTypes.put("cubit", CubitComponentNode.class);
		nodeTypes.put("sierra", LocalSierraSubmit.class);
		nodeTypes.put("bashScript", BashScriptNode.class);
		nodeTypes.put("cshScript", CshScriptNode.class);
		nodeTypes.put("pythonScript", PythonScriptNode.class);
		nodeTypes.put("windowsBatchScript", WindowsBatchScriptNode.class);
		
		nodeTypes.put("multiSwitch", MultiSwitchNode.class);
	
		nodeTypes.put("aprepro", ApreproNode.class);
		nodeTypes.put("column", ColumnNode.class);
		nodeTypes.put("nestedWorkflow", NestedWorkflowNode.class);
		nodeTypes.put("nestedInternalWorkflow", NestedInternalWorkflowNode.class);

		nodeTypes.put("gnuplot", GnuplotNode.class);
		
		nodeTypes.put("forLoop", ForLoopNode.class);
		nodeTypes.put("endloop", EndLoopNode.class);
		nodeTypes.put("checkpoint", SetCheckpointNode.class);
		nodeTypes.put("rejoinCheckpoint", RejoinCheckpointNode.class);
		nodeTypes.put("remoteCommand", RemoteCommandNode.class);
		nodeTypes.put("uploadFile", UploadFileNode.class);
		nodeTypes.put("downloadFile", DownloadFileNode.class);
		
		nodeTypes.put("sum", SumNode.class);
		nodeTypes.put("min", MinNode.class);
		nodeTypes.put("max", MaxNode.class);
		nodeTypes.put("mean", MeanNode.class);
		nodeTypes.put("stddev", StdDevNode.class);		
		
		conductorTypes.put("simple", SimpleWorkflowConductor.class);
		conductorTypes.put("list", ListWorkflowConductor.class);
		conductorTypes.put("repeat", RepeatWorkflowConductor.class);
		conductorTypes.put("sweep", SweepWorkflowConductor.class);

		if (pluginDir != null && log != null) {
			PluginLoader.loadPlugins(pluginDir, log);
		}
	}
	
	static synchronized void addNodeType(String name, Class<? extends SAWCustomNode> node) {
		if (nodeTypes == null)
			throw new SAWWorkflowException("Node database not loaded");
		nodeTypes.put(name, node);
	}
	
	public static boolean hasNodeType(String name) {
		return nodeTypes.containsKey(name);
	}
}
