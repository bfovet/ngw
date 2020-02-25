/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.argv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

// Arguments are of two types: 1) flags, which include only a key word and 2) options,
// which contain a key word AND one additional parameter.

public class CommandLineParser {
	
	private ArrayList<OptionValue> optionValues = new ArrayList<OptionValue>();
	private ArrayList<OptionSet> pluginOptions = new ArrayList<OptionSet>();
	private Map<String, IConfigurationElement> argumentHandlers = new HashMap<String, IConfigurationElement>();
	
    private static CommandLineParser INSTANCE;
	public static synchronized CommandLineParser get() {
		if (INSTANCE == null)
			INSTANCE = new CommandLineParser();
		return INSTANCE;
	}
	
	private CommandLineParser() {
		// Get all of the contributions from extensions
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint("gov.sandia.dart.argv", "argvProvider");
		IConfigurationElement[] elements = extensionPoint.getConfigurationElements();
		for (int i=0; i < elements.length; i++) {
			IConfigurationElement config = elements[i];
			String name = config.getName();
			if (name.equals("startupOption")) {
				OptionSet set = new OptionSet();
				set.label = config.getAttribute("optionLabel");
				set.type = config.getAttribute("optionType");
				set.help = config.getAttribute("optionHelp");
				set.group = config.getAttribute("groupName");
				pluginOptions.add(set);
			} else if (name.equals("argumentHandler")) {
				argumentHandlers .put(config.getAttribute("groupName"), config);
			}
		}
	}

	/**
	 * The return list is the list of args that are not consumed by this
	 * handler. The assumption is those args are system (Eclipse) arguments.
	 * This method is called from the startup application. Input is the list of
	 * all command-line arguments.
	 */
	
	public String[] parseArguments(String[] args) {
		ArrayList<String> rawArgsList = new ArrayList<String>();
		ArrayList<String> unusedArgsList = new ArrayList<String>();
		
		Collections.addAll(rawArgsList, args);
		
		// look for -help first. If found, process it only and return
		if (rawArgsList.indexOf("-help") >= 0) {
			processHelp();
			unusedArgsList.add(null);
		}
		else {
			// compare this raw list with the registered options. Those options not
			// registered will be considered unused and will be returned to the
			// caller
			for (int i=0; i < rawArgsList.size(); i++) {
				String arg = rawArgsList.get(i);
				OptionSet option = isOptionRegistered(arg);
				if (option != null) {					
					OptionValue optValue = new OptionValue();
					optValue.value = arg;
					optValue.group = option.group;
					optionValues.add(optValue);
					
					// if this is an option, the next raw arg should also be inserted here
					// Example -input xyz.jou
					if (option.type.equals("option")) {
						OptionValue newVal = new OptionValue();
						if(++i < rawArgsList.size()){
							newVal.value = rawArgsList.get(i);
						}else{
							String errorMsg = "Argument missing for option: " + getHelpForOption(option);							
							System.out.println(errorMsg);
							newVal.value = "";
						}
						newVal.group = option.group;
						optionValues.add(newVal);							
					}
				}
				else
					unusedArgsList.add(arg);
			}
		}
				
		return unusedArgsList.toArray(new String[unusedArgsList.size()]);
	}

	public void invokeHandlers() {
		for (String groupName: argumentHandlers.keySet()) {
			List<String> options = getPluginOptions(groupName);
			if (!options.isEmpty()) {
				try {
					IArgumentHandler handler = (IArgumentHandler) argumentHandlers.get(groupName).createExecutableExtension("class");
					handler.execute(options);
				} catch (CoreException e) {
					System.out.println("Internal error in CommandLineParser: bad handler for group name " + groupName);
				}
			}
		}
	}
	
	// See if the particular parameter/token is one of those registered by plugins.
	private OptionSet isOptionRegistered(String opt) {
		for (OptionSet option: pluginOptions) {
			if (opt.equals(option.label))
				return option;
		}
		return null;
	}
	
	private void processHelp() {
		System.out.println("List of registered options");
		for (OptionSet option: pluginOptions) {
			String str = getHelpForOption(option);
			System.out.println(str);
		}
	}

	public String getHelpForOption(OptionSet option) {
		String str = option.group + ": ";
		str += option.label + ", ";
		str += option.help;
		return str;
	}

	// This is called by a plugin as that plugin is started up. The plugin knows
	// its group identifier since that same identifier is used in the extension xml.
	public List<String> getPluginOptions(String group) {
		ArrayList<String> returnList = new ArrayList<String>();
		
		for (OptionValue value: optionValues) {
			if (value.group.equals(group)) {
				returnList.add(value.value);
			}
		}
		return returnList;
	}

	////////////////////////////////////
	// optionSet (1 <--> M) optionValue
	////////////////////////////////////
	
	private static class OptionValue {
		String value;
		String group;
	}
	
	private static class OptionSet {
		String label;
		String type;
		String help;
		String group;
	}
}
