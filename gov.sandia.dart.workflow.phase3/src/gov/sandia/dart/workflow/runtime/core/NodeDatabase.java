/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;

import gov.sandia.dart.workflow.runtime.components.AcosNode;
import gov.sandia.dart.workflow.runtime.components.AddNode;
import gov.sandia.dart.workflow.runtime.components.ApostproNode;
import gov.sandia.dart.workflow.runtime.components.ArrayElementNode;
import gov.sandia.dart.workflow.runtime.components.AsinNode;
import gov.sandia.dart.workflow.runtime.components.AtanNode;
import gov.sandia.dart.workflow.runtime.components.ColumnNode;
import gov.sandia.dart.workflow.runtime.components.CompareNode;
import gov.sandia.dart.workflow.runtime.components.ConstantNode;
import gov.sandia.dart.workflow.runtime.components.CosNode;
import gov.sandia.dart.workflow.runtime.components.DecrementNode;
import gov.sandia.dart.workflow.runtime.components.DemultiplexColumnsNode;
import gov.sandia.dart.workflow.runtime.components.DivideNode;
import gov.sandia.dart.workflow.runtime.components.EndLoopNode;
import gov.sandia.dart.workflow.runtime.components.ExitNode;
import gov.sandia.dart.workflow.runtime.components.ExpNode;
import gov.sandia.dart.workflow.runtime.components.ExternalProcessNode;
import gov.sandia.dart.workflow.runtime.components.FailNode;
import gov.sandia.dart.workflow.runtime.components.FileNode;
import gov.sandia.dart.workflow.runtime.components.FolderNode;
import gov.sandia.dart.workflow.runtime.components.ForLoopNode;
import gov.sandia.dart.workflow.runtime.components.GetColumnsNode;
import gov.sandia.dart.workflow.runtime.components.GnuplotNode;
import gov.sandia.dart.workflow.runtime.components.IncrementNode;
import gov.sandia.dart.workflow.runtime.components.IntNode;
import gov.sandia.dart.workflow.runtime.components.LnNode;
import gov.sandia.dart.workflow.runtime.components.LogNode;
import gov.sandia.dart.workflow.runtime.components.MaxNode;
import gov.sandia.dart.workflow.runtime.components.MeanNode;
import gov.sandia.dart.workflow.runtime.components.MinNode;
import gov.sandia.dart.workflow.runtime.components.MultiSwitchNode;
import gov.sandia.dart.workflow.runtime.components.MultiplyNode;
import gov.sandia.dart.workflow.runtime.components.NegateNode;
import gov.sandia.dart.workflow.runtime.components.OrNode;
import gov.sandia.dart.workflow.runtime.components.ParameterFileNode;
import gov.sandia.dart.workflow.runtime.components.ParameterNode;
import gov.sandia.dart.workflow.runtime.components.PiNode;
import gov.sandia.dart.workflow.runtime.components.PowNode;
import gov.sandia.dart.workflow.runtime.components.PrintNode;
import gov.sandia.dart.workflow.runtime.components.RandomNode;
import gov.sandia.dart.workflow.runtime.components.RegexNode;
import gov.sandia.dart.workflow.runtime.components.RejoinCheckpointNode;
import gov.sandia.dart.workflow.runtime.components.ScriptNode;
import gov.sandia.dart.workflow.runtime.components.SetCheckpointNode;
import gov.sandia.dart.workflow.runtime.components.SignumNode;
import gov.sandia.dart.workflow.runtime.components.SinNode;
import gov.sandia.dart.workflow.runtime.components.SleepNode;
import gov.sandia.dart.workflow.runtime.components.SqrtNode;
import gov.sandia.dart.workflow.runtime.components.SquareNode;
import gov.sandia.dart.workflow.runtime.components.StatusNode;
import gov.sandia.dart.workflow.runtime.components.StdDevNode;
import gov.sandia.dart.workflow.runtime.components.StringCatNode;
import gov.sandia.dart.workflow.runtime.components.StringCompareNode;
import gov.sandia.dart.workflow.runtime.components.StringReplaceNode;
import gov.sandia.dart.workflow.runtime.components.StringSearchNode;
import gov.sandia.dart.workflow.runtime.components.StringTemplateSubstitutionNode;
import gov.sandia.dart.workflow.runtime.components.SubstringNode;
import gov.sandia.dart.workflow.runtime.components.SubtractNode;
import gov.sandia.dart.workflow.runtime.components.SumNode;
import gov.sandia.dart.workflow.runtime.components.TanNode;
import gov.sandia.dart.workflow.runtime.components.aprepro.ApreproNode;
import gov.sandia.dart.workflow.runtime.components.cubit.CubitComponentNode;
import gov.sandia.dart.workflow.runtime.components.localsubmit.LocalQueueSubmit;
import gov.sandia.dart.workflow.runtime.components.nested.ConditionalWorkflowConductor;
import gov.sandia.dart.workflow.runtime.components.nested.ListWorkflowConductor;
import gov.sandia.dart.workflow.runtime.components.nested.NestedInternalWorkflowNode;
import gov.sandia.dart.workflow.runtime.components.nested.NestedWorkflowNode;
import gov.sandia.dart.workflow.runtime.components.nested.RepeatWorkflowConductor;
import gov.sandia.dart.workflow.runtime.components.nested.SimpleWorkflowConductor;
import gov.sandia.dart.workflow.runtime.components.nested.SweepWorkflowConductor;
import gov.sandia.dart.workflow.runtime.components.nested.WorkflowConductor;
import gov.sandia.dart.workflow.runtime.components.remote.DownloadFileNode;
import gov.sandia.dart.workflow.runtime.components.remote.RemoteCommandNode;
import gov.sandia.dart.workflow.runtime.components.remote.RemoteNestedWorkflowNode;
import gov.sandia.dart.workflow.runtime.components.remote.UploadFileNode;
import gov.sandia.dart.workflow.runtime.components.script.BashScriptNode;
import gov.sandia.dart.workflow.runtime.components.script.CshScriptNode;
import gov.sandia.dart.workflow.runtime.components.script.CubitPythonScriptNode;
import gov.sandia.dart.workflow.runtime.components.script.PythonScriptNode;
import gov.sandia.dart.workflow.runtime.components.script.TclScriptNode;
import gov.sandia.dart.workflow.runtime.components.script.WindowsBatchScriptNode;
import gov.sandia.dart.workflow.runtime.controls.ABSwitchNode;
import gov.sandia.dart.workflow.runtime.controls.OnOffSwitchNode;
import gov.sandia.dart.workflow.runtime.controls.RotaryInputSwitchNode;
import gov.sandia.dart.workflow.runtime.controls.RotaryOutputSwitchNode;
import gov.sandia.dart.workflow.runtime.util.Indenter;

public class NodeDatabase {

	private static Map<String, Class<? extends SAWCustomNode>> nodeTypes;
	private static Set<String> nodesToExcludeFromDump;
	private static Map<String, Class<? extends WorkflowConductor>> conductorTypes;
	private static Set<String> plugins = new HashSet<>();
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
			List<PropertyInfo> properties = node.getDefaultProperties();
			if (!properties.isEmpty()) {
				out.printAndIndent("<properties>");
				for (PropertyInfo prop : properties) {
					String property = prop.getName();
					String type = prop.getType();
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
			if (nodesToExcludeFromDump.contains(name))
				continue;
			Class<? extends SAWCustomNode> clazz = nodeTypes.get(name);
			SAWCustomNode node = clazz.newInstance();
			out.printAndIndent(String.format("<nodeType name='%s'>", StringEscapeUtils.escapeXml10(name)));

			List<String> categories = node.getCategories();
			if (!categories.isEmpty()) {
				out.printAndIndent("<categories>");
				for (String category : categories) {
					out.printIndented(String.format("<category name='%s'/>", StringEscapeUtils.escapeXml10(category)));
				}
				out.unindentAndPrint("</categories>");
			}

			List<PropertyInfo> properties = node.getDefaultProperties();
			if (!properties.isEmpty()) {
				out.printAndIndent("<properties>");
				for (PropertyInfo prop : properties) {
					String property = prop.getName();
					String type = prop.getType();
					String value = prop.getDefaultValue();
					boolean advanced = prop.isAdvanced();

					if (value != null) 
						out.printIndented(String.format("<property name='%s' type='%s' value='%s' advanced='%s'/>", StringEscapeUtils.escapeXml10(property),
							StringEscapeUtils.escapeXml10(type), StringEscapeUtils.escapeXml10(value), String.valueOf(advanced)));
					else
						out.printIndented(String.format("<property name='%s' type='%s' advanced='%s'/>", StringEscapeUtils.escapeXml10(property), StringEscapeUtils.escapeXml10(type), String.valueOf(advanced)));
				}
				out.unindentAndPrint("</properties>");
			}

			List<InputPortInfo> inputs = node.getDefaultInputs();
			if (!inputs.isEmpty()) {
				out.printAndIndent("<inputs>");
				for (InputPortInfo port : inputs) {
					String input = port.getName();
					String type = port.getType();
					out.printIndented(String.format("<input name='%s' type='%s'/>", StringEscapeUtils.escapeXml10(input), StringEscapeUtils.escapeXml10(type)));
				}
				out.unindentAndPrint("</inputs>");
			}

			List<OutputPortInfo> outputs = node.getDefaultOutputs();
			if (!outputs.isEmpty()) {
				out.printAndIndent("<outputs>");
				for (OutputPortInfo port : outputs) {
					String output = port.getName();
					String type = port.getType();
					out.printIndented(String.format("<output name='%s' type='%s'/>", StringEscapeUtils.escapeXml10(output), StringEscapeUtils.escapeXml10(type)));
				}
				out.unindentAndPrint("</outputs>");
			}

			out.unindentAndPrint("</nodeType>");
		}
		out.unindentAndPrint("</nodeTypes>");
	}

	public synchronized static void loadDefinitions(SAWWorkflowLogger log) {
		if (nodeTypes != null)
			return;
		
		nodeTypes = new HashMap<>();
		nodesToExcludeFromDump = new TreeSet<>();
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
		nodeTypes.put("int", IntNode.class);		

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
				
		nodeTypes.put("status", StatusNode.class);
		nodeTypes.put("print", PrintNode.class);
		nodeTypes.put("constant", ConstantNode.class);
		nodeTypes.put("parameter", ParameterNode.class);
		nodeTypes.put("parameterFile", ParameterFileNode.class);
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
		nodeTypes.put("javascript", ScriptNode.class);
		nodeTypes.put("apostpro", ApostproNode.class);
		nodeTypes.put("cubit", CubitComponentNode.class);
		nodeTypes.put("sierra", LocalQueueSubmit.class);
		nodeTypes.put("queueSubmit", LocalQueueSubmit.class);

		nodeTypes.put("bashScript", BashScriptNode.class);
		nodeTypes.put("cshScript", CshScriptNode.class);
		nodeTypes.put("pythonScript", PythonScriptNode.class);
		nodeTypes.put("cubitPythonScript", CubitPythonScriptNode.class);
		nodeTypes.put("windowsBatchScript", WindowsBatchScriptNode.class);
		nodeTypes.put("tclScript", TclScriptNode.class);
		
		nodeTypes.put("multiSwitch", MultiSwitchNode.class);
	
		nodeTypes.put("aprepro", ApreproNode.class);
		nodeTypes.put("column", ColumnNode.class);
		nodeTypes.put("arrayElement", ArrayElementNode.class);
		nodeTypes.put("demultiplexColumns", DemultiplexColumnsNode.class);
		nodeTypes.put("getColumns", GetColumnsNode.class);
		nodeTypes.put("nestedWorkflow", NestedWorkflowNode.class);
		nodeTypes.put("nestedInternalWorkflow", NestedInternalWorkflowNode.class);
		nodesToExcludeFromDump.add("nestedInternalWorkflow");
		nodeTypes.put(RemoteNestedWorkflowNode.TYPE, RemoteNestedWorkflowNode.class);

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
		
		nodeTypes.put("onOffSwitch", OnOffSwitchNode.class);		
		nodeTypes.put("abSwitch", ABSwitchNode.class);		
		nodeTypes.put("rotaryOutputSwitch", RotaryOutputSwitchNode.class);		
		nodeTypes.put("rotaryInputSwitch", RotaryInputSwitchNode.class);		

		nodeTypes.put("or", OrNode.class);
		
		conductorTypes.put("simple", SimpleWorkflowConductor.class);
		conductorTypes.put("list", ListWorkflowConductor.class);
		conductorTypes.put("repeat", RepeatWorkflowConductor.class);
		conductorTypes.put("sweep", SweepWorkflowConductor.class);
		conductorTypes.put("conditional", ConditionalWorkflowConductor.class);

		
		// Load plugins from WFLIB/plugins (standalone install)
		File pluginDir = new File(RuntimeData.getWFLIB(), "plugins");
		if (pluginDir != null && log != null) {
			if  (!pluginDir.exists()) {
				log.info("Plugin dir {0} does not exist", pluginDir.getAbsolutePath());
			} else {
				File[] files = pluginDir.listFiles(f -> {return f.getName().endsWith(".jar");});

				if  (files == null || files.length == 0) {
					log.info("No extensions found in plugin dir {0}", pluginDir.getAbsolutePath());
				} else {
					PluginLoader.loadPlugins(files, log);
				}
			}
		}		
	
		// Load explicit plugins
		List<File> files = plugins.stream().map(File::new).collect(Collectors.toList());
		PluginLoader.loadPlugins((File[]) files.toArray(new File[files.size()]), log);
	}
	
	static synchronized void addNodeType(String name, Class<? extends SAWCustomNode> node) {
		if (nodeTypes == null)
			throw new SAWWorkflowException("Node database not loaded");
		nodeTypes.put(name, node);
	}
	
	public static boolean hasNodeType(String name) {
		return nodeTypes.containsKey(name);
	}

	public static void addPlugin(String plugin) {
		plugins.add(plugin);
	}
}
