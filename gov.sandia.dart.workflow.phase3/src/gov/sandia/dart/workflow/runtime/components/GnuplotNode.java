/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class GnuplotNode extends SAWCustomNode {

	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		byte[] x = (byte[]) runtime.getInput(getName(), "x", byte[].class);
		byte[] y = (byte[]) runtime.getInput(getName(), "y", byte[].class);
		BufferedReader xreader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(x)));
		BufferedReader yreader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(y)));
		String commands = getCommands(properties);
		String imageFile = getImageFile(properties);
		
		try {
			File componentWorkDir = getComponentWorkDir(runtime, properties);
			PrintWriter out = new PrintWriter(new FileWriter(new File(componentWorkDir, "data.tmp")));
			out.println("# Temporary data file");
			String xp = xreader.readLine();
			String yp = yreader.readLine();
			while (xp != null) {
				out.println(xp + "\t" + yp);
				xp = xreader.readLine();
				yp = yreader.readLine();
			}
			out.close();
			out = new PrintWriter(new FileWriter(new File(componentWorkDir, "commands.tmp")));
			out.println(commands);
			out.println("set terminal \"png\"");
			out.println("set output \"" + imageFile + "\""); 
			out.println("plot \"data.tmp\" title \"\"");
			out.close();
			
			ProcessBuilder builder = new ProcessBuilder();
			builder.command("gnuplot", "commands.tmp");
			builder.directory(componentWorkDir);
			Map<String, String> environment = builder.environment();
			environment.remove("LD_PRELOAD");
			environment.remove("DYLD_INSERT_LIBRARIES");
			environment.remove("DYLD_FORCE_FLAT_NAMESPACE");
			environment.putAll(runtime.getenv());
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			PrintStream log = new PrintStream(output);

			try {
				Process process = builder.start();
				Thread t1, t2;
				(t1  = new Thread(new Squirter(process.getInputStream(), log), "GNUPLOT stdout")).start();
				(t2 = new Thread(new Squirter(process.getErrorStream(), log), "GNUPLOT stderr")).start();
				
				if (process.waitFor() != 0) {
					throw new SAWWorkflowException("Error executing GNUPLOT");
				}
				t1.join(1000);
				t2.join(1000);
			} catch (Exception e) {
				throw new SAWWorkflowException(getName() + ": Error during execution. See log for details.");
			} finally {
				log.flush();
				FileUtils.write(new File(componentWorkDir, getName() + ".log"), output.toString());
			}
			return Collections.emptyMap();	
			
		} catch (IOException e) {
			throw new SAWWorkflowException(getName() + ": Problem while reading data", e);
		} 
	}
	
	@Override public List<String> getDefaultInputNames() { return Arrays.asList("x", "y"); }
	@Override public List<String> getDefaultProperties() { return Arrays.asList("commands", "imageFile"); }
	@Override public List<String> getDefaultPropertyTypes() { return Arrays.asList("multitext", "text"); }
	@Override public String getCategory() { return "Engineering"; }
		
	public String getCommands(Map<String, String> properties) {
		String raw = properties.get("commands");
		return raw;
	}
	
	public String getImageFile(Map<String, String> properties) {
		String raw = properties.get("imageFile");
		return raw;
	}
}
