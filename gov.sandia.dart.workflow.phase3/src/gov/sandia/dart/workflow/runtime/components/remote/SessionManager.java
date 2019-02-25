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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;

import gov.sandia.dart.IUserAndHost;
import gov.sandia.dart.impl.UserAndHost;
import gov.sandia.dart.jh.ISSHJumpHost;
import gov.sandia.dart.jh.impl.SSHJumpHost;

public class SessionManager {
	private static final int CONNECTION_TIMEOUT = 20000;
	private static final int RETRIES = 3;

	private Map<String, Session> sessions = new HashMap<>();

	public static final SessionManager INSTANCE = new SessionManager();

	
	private SessionManager() {
		Logger logger = new JSchLogger(getLogLevel());
		JSch.setLogger(logger);
	}
	
	Session getSession(String dstUser, String dstHost, String jmpUser, String jmpHost) throws JSchException, IOException {

	    IUserAndHost dst = new UserAndHost(dstUser, dstHost);
	    IUserAndHost jmp = new UserAndHost(jmpUser, jmpHost);

	    setKrb5Conf("/etc/krb5.conf");
	    setKrb5Debug(true);

	    ISSHJumpHost sshJumpHost = new SSHJumpHost();
	    IOException ex = null;
	    for (int tries=0; tries<RETRIES; ++tries) {
			try {
				return sshJumpHost.connect(dst, jmp);
			} catch (IOException e) { 				
				ex = e;
			}
		}
	    throw ex;
	}
	
	
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
		// JSch doesn't actually support this option
		// config.put("ConnectionAttempts", RETRIES);
		s.setConfig(config);
		JSchException ex = null;
		for (int tries=0; tries<RETRIES; ++tries) {
			try {
				s.connect(CONNECTION_TIMEOUT);
				sessions.put(host, s);
				return s;
			} catch (JSchException e) {	
				ex = e;
			}
		}
		throw ex;		
	}

	public void shutdown() {
		sessions.values().forEach(s -> {
			try { 
				s.disconnect();
			} catch (Throwable t) {
				// Ignore
			}
		});
	}
	
	private int getLogLevel() {
		String property = System.getProperty("com.jcraft.jsch.logLevel", String.valueOf(JSchLogger.WARN));
		try {
			return Integer.parseInt(property);
		} catch (NumberFormatException ex) {
			return JSchLogger.ERROR;
		}
	}

	  private void setKrb5Debug(boolean val)
	  {
	    // put the debug thing in
	    System.setProperty("sun.security.krb5.debug", Boolean.toString(val));
	  }

	  private void setKrb5Conf(String filename)
	  {
	    if (StringUtils.isBlank(filename)) {
	      throw new IllegalArgumentException("blank/null krb5.conf filename");
	    }

	    final String KCNF = "java.security.krb5.conf";	    
	    System.setProperty(KCNF, filename);
	  }
	
	
}
