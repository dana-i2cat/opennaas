/*
 * HwdOp.java
 *
 * Created on 8 de abril de 2008, 15:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.wonesys.emsModule.hwd;

/*
 * #%L
 * OpenNaaS :: ROADM :: W-Onesys EMSModule
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author mbeltran
 */
public class HwdOp {

	public static final int					DEFAULT_PORT	= 27773;
	public static final int					DEFAULT_TIMEOUT	= 1000;

	private Socket							sock;
	private List<IMessageArrivalListener>	listeners		= new ArrayList<IMessageArrivalListener>();

	public HwdOp() {
		sock = new Socket();
	}

	public void connect(String ip, IMessageArrivalListener listener) throws IOException {
		connect(ip, DEFAULT_PORT, listener);
	}

	public void connect(String ip, int port, IMessageArrivalListener listener) throws IOException {

		registerListener(listener);

		int timeout = DEFAULT_TIMEOUT;

		// Bind to a local ephemeral port
		sock.bind(null);
		sock.connect(new InetSocketAddress(ip, port), timeout);

		// attach reader to receive async msgs
		SocketReader reader = new SocketReader();
		reader.start();

	}

	public void disconnect() throws IOException {
		if (!sock.isClosed())
			sock.close();
	}

	public void sendOp(String operation) throws IOException {

		byte[] bts = new BigInteger(operation, 16).toByteArray();

		OutputStream out = sock.getOutputStream();

		out.write(bts);
		out.flush();
	}

	public void registerListener(IMessageArrivalListener listener) {
		if (listener != null && !listeners.contains(listener))
			listeners.add(listener);
	}

	public void unregisterListener(IMessageArrivalListener listener) {
		listeners.remove(listener);
	}

	public void notifyListeners(String message) {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).messageReceived(message);
		}
	}

	public void notifyErrorToListeners(Exception e) {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).errorHappened(e);
		}
	}

	class SocketReader extends Thread {

		public void run() {

			try {
				InputStream in = sock.getInputStream();

				while (!sock.isClosed()) {
					String message = readMessage(in);
					if (!message.equals("")) {
						notifyListeners(message);
					}
				}
			} catch (IOException e1) {
				notifyErrorToListeners(e1);
			}
		}

		private String readMessage(InputStream in) throws IOException {

			String outS = "";
			byte[] buffer = new byte[256];

			// while ((i = in.read(buffer)) != 0){

			int i = in.read(buffer);

			String salida = ""; //$NON-NLS-1$

			for (int j = 0; j < i; j++) {
				byte o = buffer[j];
				if ((o < 0x10) && (o >= 0))
					salida += "0"; //$NON-NLS-1$
				String bite = Integer.toHexString(o);
				if (bite.length() > 2)
					salida += bite.substring(bite.length() - 2);
				else
					salida += bite;
			}

			outS += salida;
			// }
			return outS;
		}
	}

}

// class Timer extends Thread {
// /** Rate at which timer is checked */
// protected int m_rate = 100;
//
// /** Length of timeout */
// private int m_length;
// /** Rate at which timer is checked */
//
// /** Time elapsed */
// private int m_elapsed;
//
// /**
// * Creates a timer of a specified length
// *
// * @param length
// * Length of time before timeout occurs
// */
// public Timer(int length) {
// // Assign to member variable
// m_length = length;
//
// // Set time elapsed
// m_elapsed = 0;
// }
//
// /** Resets the timer back to zero */
// public synchronized void reset() {
// m_elapsed = 0;
// }
//
// /** Performs timer specific code */
// public void run() {
// // Keep looping
// for (;;) {
// // Put the timer to sleep
// try {
// Thread.sleep(m_rate);
// } catch (InterruptedException ioe) {
// continue;
// }
//
// // Use 'synchronized' to prevent conflicts
// synchronized (this) {
// // Increment time remaining
// m_elapsed += m_rate;
//
// // Check to see if the time has been exceeded
// if (m_elapsed > m_length) {
// // Trigger a timeout
// timeout();
// break;
// }
// }
//
// }
// }
//
// public void timeout() {
// // System.exit(1);
// }
// }
