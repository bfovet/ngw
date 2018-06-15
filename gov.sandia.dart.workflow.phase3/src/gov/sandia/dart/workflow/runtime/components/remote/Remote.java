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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;

public class Remote {
	private String host;
	private String user;
	private Session session;
	private String path;


	public Remote(String host, String user) {
		this.host = host;
		this.user = user;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public void connect() throws JSchException {
		if (session != null) {
			throw new JSchException("Already connected");
		}
		
		Logger logger = new JSchLogger(getLogLevel());
		JSch.setLogger(logger);

		String path = RuntimeData.getWFLIB();
		System.setProperty("java.security.krb5.conf", new File(path, "krb5.conf").getAbsolutePath());
		System.setProperty("java.security.auth.login.config", new File(path, "jaas.conf").getAbsolutePath());
		System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
		// System.setProperty("sun.security.krb5.debug", "true");

		Session aSession = SessionManager.INSTANCE.getSession(user, host);
		session = aSession;
	}

	public String execute(String command, OutputStream err, RuntimeData runtime) throws JSchException, IOException {
		Channel channel = session.openChannel("exec");
		if (!StringUtils.isEmpty(path)) {
			command = "cd " + path + "; " + command;
		}
		((ChannelExec) channel).setCommand(command);

		channel.setInputStream(null);
		((ChannelExec) channel).setErrStream(err);

		InputStream in = channel.getInputStream();
		channel.connect();
		byte[] tmp = new byte[1024];
		StringBuilder output = new StringBuilder();
		while (!runtime.isCancelled()) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0)
					break;
				output.append(new String(tmp, 0, i));
			}
			if (channel.isClosed()) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (Exception ee) {
			}
		}
		channel.disconnect();
		return output.toString();
	}

	public boolean upload(File lfile, String rfile, RuntimeData runtime)
			throws IOException, JSchException {
		if (!StringUtils.isEmpty(path)) {				
			rfile = path + (path.endsWith("/") ? "" : "/") + rfile;
		}
		String command = "scp -t " + rfile;
		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);

		// get I/O streams for remote scp
		OutputStream out = channel.getOutputStream();
		InputStream in = channel.getInputStream();

		channel.connect();

		if (checkAck(in) != 0) {
			return false;
		}

		// send "C0644 filesize filename", where filename should not include '/'
		long filesize = lfile.length();
		command = "C0644 " + filesize + " " + lfile.getName() + "\n";
		out.write(command.getBytes());
		out.flush();
		if (checkAck(in) != 0) {
			return false;
		}

		// send contents of lfile
		FileInputStream fis = new FileInputStream(lfile);
		byte[] buf = new byte[1024];
		while (!runtime.isCancelled()) {
			int len = fis.read(buf, 0, buf.length);
			if (len <= 0)
				break;
			out.write(buf, 0, len);
			out.flush();
		}
		fis.close();
		fis = null;
		if (!runtime.isCancelled()) {
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if (checkAck(in) != 0) {
				return false;
			}
		}
		out.close();

		channel.disconnect();
		return true;
	}
	
	public boolean disconnect() {
		if (session != null) {
			Session s = session;
			session = null;
			s.disconnect();
			return true;
		} else {
			return false;
		}
	}

	public boolean download(File lfile, String rfile, RuntimeData runtime) throws JSchException, IOException {
		if (!StringUtils.isEmpty(path)) {				
			rfile = path + (path.endsWith("/") ? "" : "/") + rfile;
		}
		String command = "scp -f " + rfile;
		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);

		// get I/O streams for remote scp
		OutputStream out = channel.getOutputStream();
		InputStream in = channel.getInputStream();
		FileOutputStream fos=null;

		channel.connect();
		
		try {
			byte[] buf = new byte[1024];

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			while (!runtime.isCancelled()) {
				int c = checkAck(in);
				if (c != 'C') {
					break;
				}

				// read '0644 '
				in.read(buf, 0, 5);

				long filesize = 0L;
				while (!runtime.isCancelled()) {
					if (in.read(buf, 0, 1) < 0) {
						throw new JSchException("Incomplete file on download");					
					}
					if (buf[0] == ' ')
						break;
					filesize = filesize * 10L + buf[0] - '0';
				}

				String file = null;
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						file = new String(buf, 0, i);
						break;
					}
					if (runtime.isCancelled())
						break;
				}

				// System.out.println("filesize="+filesize+", file="+file);

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();

				// read a content of lfile
				try {
					fos = new FileOutputStream(lfile);

					int foo;
					while (!runtime.isCancelled()) {
						if (buf.length < filesize)
							foo = buf.length;
						else
							foo = (int) filesize;
						foo = in.read(buf, 0, foo);
						if (foo < 0) {
							throw new JSchException("Incomplete file on download");
						}
						fos.write(buf, 0, foo);
						filesize -= foo;
						if (filesize == 0L)
							break;
					}
				} finally {
					if (fos != null)
						fos.close();
				}
				
				if (!runtime.isCancelled()) {
					if (checkAck(in) != 0) {
						return false;
					}
					// send '\0'
					buf[0] = 0;
					out.write(buf, 0, 1);
					out.flush();
				}
			}
			
		} finally {
			channel.disconnect();
		}

		return true;
	}


	private int getLogLevel() {
		String property = System.getProperty("com.jcraft.jsch.logLevel", String.valueOf(JSchLogger.WARN));
		try {
			return Integer.parseInt(property);
		} catch (NumberFormatException ex) {
			return JSchLogger.ERROR;
		}
	}
	
	private int checkAck(InputStream in) throws IOException {
		int b=in.read();
		// b may be 0 for success,
		//          1 for error,
		//          2 for fatal error,
		//          -1
		if(b==0) return b;
		if(b==-1) return b;

		if(b==1 || b==2){
			StringBuffer sb=new StringBuffer();
			int c;
			do {
				c=in.read();
				sb.append((char)c);
			} while(c!='\n');
			throw new IOException(sb.toString());
		}
		return b;
	}
}
