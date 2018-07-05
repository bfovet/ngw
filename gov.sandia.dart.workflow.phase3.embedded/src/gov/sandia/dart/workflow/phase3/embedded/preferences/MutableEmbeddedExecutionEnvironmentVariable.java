/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.preferences;

import java.io.File;
import java.io.IOException;

import com.strikewire.snl.apc.properties.MutablePropertiesInstance;
import com.strikewire.snl.apc.properties.MutablePropertiesInstanceHolder;

public class MutableEmbeddedExecutionEnvironmentVariable
	extends EmbeddedExecutionEnvironmentVariable
	implements MutablePropertiesInstance<EmbeddedExecutionEnvironmentVariable>
{

	private MutablePropertiesInstanceHolder<EmbeddedExecutionEnvironmentVariable> holder_;

	public MutableEmbeddedExecutionEnvironmentVariable(EmbeddedExecutionEnvironmentVariables parent)
	{
		super(parent);
		holder_ = new MutablePropertiesInstanceHolder<EmbeddedExecutionEnvironmentVariable>(this, parent);
	}

	public MutableEmbeddedExecutionEnvironmentVariable(EmbeddedExecutionEnvironmentVariables parent, File file)
			throws Exception {
		super(file, parent);
		holder_ = new MutablePropertiesInstanceHolder<EmbeddedExecutionEnvironmentVariable>(this, parent);
	}

	@Override
	public void setName(String newName) {
		holder_.setName(newName);
	}

	public void setValue(String value) {
		setProperty(VALUE, value);
	}
	
	@Override
	public void setProperty(String key, String value) {
		holder_.setProperty(key, value);
	}

	@Override
	public void saveChanges() throws IOException {
		holder_.saveChanges();
	}

	@Override
	public boolean isModified() {
		return holder_.isModified();
	}

	@Override
	public void setModified() {
		holder_.setModified();
	}

	@Override
	public boolean isOverriding() {
		return holder_.isOverriding();
	}

	@Override
	public void setHolder(
			MutablePropertiesInstanceHolder<EmbeddedExecutionEnvironmentVariable> holder) {
		holder_ = holder;
	}
	
}
