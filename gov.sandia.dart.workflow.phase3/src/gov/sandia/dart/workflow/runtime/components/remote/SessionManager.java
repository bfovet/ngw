/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.remote;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

class SessionManager {
	private Map<String, Session> sessions = new HashMap<>();

	static final SessionManager INSTANCE = new SessionManager();
	
	
	Session getSession(String user, String host) throws JSchException {
		Session s = sessions.get(host);		
		if (s != null && user.equals(s.getUserName()) && s.isConnected()) {
			return s;
		}
		JSch jsch = new JSch();
		s = jsch.getSession(user, host, 22);
		
		Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		config.put("PreferredAuthentications", "gssapi-with-mic");
		s.setConfig(config);
		s.connect(20000);

		sessions.put(host, s);
		return s;
	}

}
