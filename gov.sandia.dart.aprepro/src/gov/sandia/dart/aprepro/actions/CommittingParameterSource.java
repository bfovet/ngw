/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.aprepro.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This wraps a parameter source in order to delay the actual creation of new parameters 
 * until a later event.  This can be used for an action panel or dialog so that parameters 
 * are only saved to the base source when "OK" is clicked and not when "Cancel" is clicked  
 * @author arothfu
 *
 */
public class CommittingParameterSource implements IParameterSource{

	private IParameterSource baseSource_;
	
	Map<String, String> newParameters_ = new HashMap<String,String>();
	
	public CommittingParameterSource(IParameterSource parameterSource){
		baseSource_ = parameterSource;
	}

	@Override
	public Map<String, String> getParameters(){
		Map<String, String> base = baseSource_.getParameters();
		base.putAll(newParameters_);
		return base;
	}
	
	@Override
	public boolean createParameter(String name, String value){
		if(allowCreate()){
			newParameters_.put(name, value);
			return true;
		}
		return false;
	}
	
	public boolean commitNewParameters(){
		if(!allowCreate()){
			return false;
		}
		
		boolean success = true;
		for(Entry<String, String> entry: newParameters_.entrySet()){
			String name = entry.getKey();
			String value = entry.getValue();			
			
			if(!baseSource_.createParameter(name, value)){
				success = false;
			}
		}
		return success;
	}

	@Override
	public boolean allowCreate() {
		return baseSource_.allowCreate();
	}
}
