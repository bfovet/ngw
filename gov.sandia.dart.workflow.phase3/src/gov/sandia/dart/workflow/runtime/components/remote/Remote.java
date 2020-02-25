/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowLogger;

public class Remote {
	private String host;
	private String user;
	private Session session;
	private String path;
	private String jmpHost;
	private String jmpUser;


	public Remote(String host, String user) {
		this.host = host;
		this.user = user;
	}

	public Remote(String host, String user, String jmpHost, String jmpUser) {
		this.host = host;
		this.user = user;
		this.jmpHost = jmpHost;
		this.jmpUser = jmpUser;
	}	

	public void setPath(String path) {
		this.path = path.replaceAll("\\\\", "/").trim();
	}

	public String getPath() {
		return path;
	}

	public void connect(SAWWorkflowLogger log) throws IOException  {
		if (session != null) {
			throw new IOException("Already connected");
		}

		try {
						
			initKerberos(log);

			Session aSession;
			if (StringUtils.isNoneEmpty(jmpUser, jmpHost, user, host)) {
				aSession = SessionManager.INSTANCE.getSession(user, host, jmpUser, jmpHost, log);
			} else if (StringUtils.isNoneEmpty(user, host)) {			
				aSession = SessionManager.INSTANCE.getSession(user, host, log);
			} else {
				throw new IOException("Cannot connect; user and host must both be specified");
			}
			session = aSession;
		} catch (JSchException e) {
			throw new IOException("Error connecting to remote host", e);
		}
	}
	private static AtomicBoolean initialized = new AtomicBoolean();
	public static void initKerberos(SAWWorkflowLogger log) {
		if (initialized.getAndSet(true))
			return;
		File krb5File = getConfigFile("krb5.conf");
		if (!krb5File.exists())
			log.warn("krb5File does not exist: {0}", krb5File.getAbsolutePath());
		System.setProperty("java.security.krb5.conf", krb5File.getAbsolutePath());
		File jaasFile = getConfigFile("jaas.conf");
		if (!jaasFile.exists())
			log.warn("jaasFile does not exist: {0}", jaasFile.getAbsolutePath());
		System.setProperty("java.security.auth.login.config", jaasFile.getAbsolutePath());
		System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
		// System.setProperty("sun.security.krb5.debug", "true");
	}

	private static File getConfigFile(String name) {
		String wflib = RuntimeData.getWFLIB();
		if (new File(wflib).exists())
			return new File(wflib, name);
		URL resource = null;
		try {
		    resource = Remote.class.getResource("/resources/" + name);
			return Paths.get(resource.toURI()).toFile();
		} catch (Exception e) {
			// Fall through				
		}
		try {
			String data = IOUtils.toString(resource, Charset.defaultCharset());	
			File tempFile = File.createTempFile("dart", "conf");
			tempFile.deleteOnExit();			
			FileUtils.write(tempFile, data);
		} catch (Exception e) {
			// Fall through
		}
		
		return new File(name);
	}
	public int execute(String command, OutputStream out, OutputStream err, RuntimeData runtime) throws IOException {
		Channel channel = null;
		try {
			channel = session.openChannel("exec");
			if (!StringUtils.isEmpty(path)) {
				command = "cd " + path + "; " + command;
			}
			((ChannelExec) channel).setCommand(command);

			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(err);
			((ChannelExec) channel).setOutputStream(err);


			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			while (!runtime.isCancelled()) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					if (out != null) {
						out.write(tmp, 0, i);					
						out.flush();
					}					
				}
				if (channel.isClosed()) {
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			return channel.getExitStatus();
		} catch (JSchException e) {
			throw new IOException("Error executing remote command", e);
		} finally {
			if (channel != null && 	channel.isConnected()) {
				channel.disconnect();
			}
		}
	}

	public void execute(String command, OutputStream err, Function<String, Void> callback, RuntimeData runtime) throws IOException {
		Channel channel = null;
		try {
			channel = session.openChannel("exec");
			if (!StringUtils.isEmpty(path)) {
				command = "cd " + path + "; " + command;
			}
			((ChannelExec) channel).setCommand(command);

			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(err);
			((ChannelExec) channel).setOutputStream(err);


			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			while (!runtime.isCancelled() && !Thread.interrupted()) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					callback.apply(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					break;
				}
				Thread.sleep(1000);
			}
		} catch (JSchException e) {
			throw new IOException("Error executing remote command", e);
		} catch (InterruptedException e) {
			throw new IOException("Remote command interrupted", e);
		} finally {
			if (channel != null && channel.isConnected())
				channel.disconnect();
		}

	}


	public boolean upload(File lfile, String rfile, RuntimeData runtime) throws IOException {
		Channel channel = null;
		try {
			if (!StringUtils.isEmpty(path)) {				

				rfile = path + (path.endsWith("/") ? "" : "/") + rfile;
			}
			String command = "scp -t " + rfile;

			channel = session.openChannel("exec");

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

			return true;
		} catch (JSchException e) {
			throw new IOException("Error uploading file", e);
		} finally {
			if (channel != null && channel.isConnected())
				channel.disconnect();
		}

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

	public boolean download(File lfile, String rfile, RuntimeData runtime) throws IOException {
		Channel channel = null;
		try {
			if (!StringUtils.isEmpty(path)) {				

				rfile = path + (path.endsWith("/") ? "" : "/") + rfile;
			}
			String command = "scp -f " + rfile;
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();
			FileOutputStream fos=null;

			channel.connect();


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

				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						break;
					}
					if (runtime.isCancelled())
						break;
				}
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


			return true;
		} catch (JSchException e) {
			throw new IOException("Error dowloading file", e);
		} finally {
			if (channel != null && channel.isConnected()) {	
				channel.disconnect();
			}
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
