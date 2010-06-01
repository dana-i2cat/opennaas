package net.i2cat.mantychore.engine.tests.transports.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import net.i2cat.mantychore.engine.transports.ssh.SSHTransport;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.iaasframework.engines.transports.TransportException;
//import com.iaasframework.engines.transports.telnet.TelnetTransport;

public class SSHTransportTest {
	private SSHTransport sshTransport = null;
	
	@Before
	public void setUp(){
		sshTransport = new SSHTransport("i2CAT","Yct4KgYR4F3fdhRV","194.68.13.29",22);
	}
	
	@Test
	public void testAll(){
		testConnect();
		System.out.println("... is connected");
		testSend();
		System.out.println("... is sent");
		testGetAndReadInputStream();
		System.out.println("... is received");
		testDisconnect();
		System.out.println("... is disconnected");
	}
	
	private void testConnect(){
		try{
			sshTransport.connect();
			SSHTransport.setDELIMITER("}");
			sshTransport.setWithShell(true);
		}catch(TransportException ex){
			ex.printStackTrace();
			Assert.assertTrue(false);
			return;
		}
	}
	
	private void testSend(){
		try{
			String message = "show configuration";
			sshTransport.send(message.toCharArray()); 
		}catch(TransportException ex){
			ex.printStackTrace();
			Assert.assertTrue(false);
			return;
		}
	}
	
	
	private static List<String> receive(SSHTransport sshtransport) throws TransportException {
		StringBuilder result = new StringBuilder();


		InputStream stdout = sshtransport.getInputStream();
		InputStream stderr = sshtransport.getInputStreamError();

		byte[] buffer = new byte[SSHTransport.SIZE_BUFFER];

		try {

			boolean continueBucle = true;
			int timeouts = 0;
			while (continueBucle) {
				if ((stdout.available() == 0) && (stderr.available() == 0)) {

					int conditions = sshtransport.getSess().waitForCondition(
							SSHTransport.STDOUT_DATA
									| SSHTransport.STDERR_DATA
									| SSHTransport.EOF, SSHTransport.TIMEOUT_MILISECS);

					/* Wait no longer than 10 seconds (= 10000 milliseconds) */

					if ((conditions & SSHTransport.TIMEOUT) != 0) {
						timeouts++;
						/* A timeout occured. */
						if (timeouts == 10) {
							throw new TransportException(
									"Timeout while waiting for data from peer.");
						}

					}

					/*
					 * Here we do not nebreak;ed to check separately for CLOSED,
					 * since CLOSED implies EOF
					 */

					if ((conditions & SSHTransport.EOF) != 0) {
						/* The remote side won't send us further data... */

						if ((conditions & (SSHTransport.STDOUT_DATA | SSHTransport.STDERR_DATA)) == 0) {
							/*
							 * ... and we have consumed all data in the local
							 * arrival window.
							 */
							break;
						}
					}

				}

				while (stdout.available() > 0) {
					int len = stdout.read(buffer);
					if (len > 0) {
						// this check is somewhat paranoid
						result.append(new String(buffer, 0, len));
					}
					// It is final
	
					if (result.indexOf(SSHTransport.DELIMITER) != -1)
						continueBucle = false;

				}

				while (stderr.available() > 0) {
					int len = stderr.read(buffer);
					if (len > 0) // this check is somewhat paranoid
						System.out.println(new String(buffer, 0, len));
				}
			}
		} catch (IOException e) {
			// e.printStackTrace();
			throw new TransportException("The message couldn't be received");
		}

		String[] arrayResult = result.toString().split(SSHTransport.DELIMITER);
		List<String> listStrings = Arrays.asList(arrayResult);


		return listStrings;
	}
	
	
	private void testGetAndReadInputStream(){
		try{
			
			List<String> listResults = receive(sshTransport);
			for (String result: listResults) {
				System.out.println(result);				
			}

			Assert.assertTrue(listResults.size() > 0);

		}catch(Exception ex){
			ex.printStackTrace();
			Assert.assertTrue(false);
			return;
		}
	}
	
	private void testDisconnect(){
		try{
			sshTransport.disconnect();
		}catch(TransportException ex){
			ex.printStackTrace();
			Assert.assertTrue(false);
			return;
		}
	}
}
