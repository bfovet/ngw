/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*
 * Created on Jan 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.strikewire.snl.apc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author gpike TODO To change the template for this generated type comment go
 *         to Window - Preferences - Java - Code Style - Code Templates
 */
public class URITransfer extends ByteArrayTransfer {
	private static Logger logger = Logger
			.getLogger("com.strikewire.snl.apc.FileManager.views.URITransfer");

	private static URITransfer instance = new URITransfer();

	private static final String TYPE_NAME = "uri-transfer-format";

	private static final int TYPEID = registerType(TYPE_NAME);

	/**
	 * Returns the singleton file transfer instance.
	 */
	public static URITransfer getInstance() {
		return instance;
	}

	/**
	 * 
	 */
	private URITransfer() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
	 */
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
	 */
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	public void javaToNative(Object object, TransferData transferData) {
		if (object == null || !(object instanceof URI[]))
			return;

		if (isSupportedType(transferData)) {
			byte[] bytes = toByteArray((URI[]) object);
			if (bytes != null)
				super.javaToNative(bytes, transferData);
		}
	}

	public Object nativeToJava(TransferData transferData) {

		if (isSupportedType(transferData)) {
			byte[] bytes = (byte[]) super.nativeToJava(transferData);
			return fromByteArray(bytes);
		}

		return null;
	}

	public byte[] toByteArray(URI[] uris) {

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOut);

		byte[] bytes = null;

		try {
			/* write number of markers */
			out.writeInt(uris.length);

			/* write markers */
			for (int i = 0; i < uris.length; i++) {
				writeURI((URI) uris[i], out);
			}
			out.close();
			bytes = byteOut.toByteArray();
		} catch (IOException e) {
			// when in doubt send nothing
			logger.log(Level.WARNING,
					"Exception caught while writing byt array", e);
		}
		return bytes;
	}

	private void writeURI(URI uri, DataOutputStream dataOut) throws IOException {
		dataOut.writeUTF(uri.toString());
	}

	private URI readURI(URI uri, DataInputStream dataIn) throws IOException {
		String uriString = dataIn.readUTF();
		URI newUri = null;
		try {
			newUri = new URI(uriString);
		} catch (URISyntaxException e) {
			logger.log(Level.WARNING, "Exception caught while reading URI", e);
		}
		return newUri;
	}

	protected URI[] fromByteArray(byte[] bytes) {
	  if (bytes == null || bytes.length == 0) {
	    return new URI[0];
	  }
	  
		DataInputStream in = new DataInputStream(
				new ByteArrayInputStream(bytes));

		try {
			/* read number of uris */
			int n = in.readInt();
			/* read uris */
			URI[] uris = new URI[n];
			for (int i = 0; i < n; i++) {
				URI uri = readURI(null, in);
				if (uri == null) {
					return null;
				}
				uris[i] = uri;
			}
			return uris;
		} catch (IOException e) {
			return null;
		}
	}

}
