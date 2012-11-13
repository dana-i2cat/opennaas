package org.opennaas.core.resources.transport;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * An interface to describe transport types used to send/receive data from the devices.
 * This simply wraps up any Input/Output Streams.
 * <p>
 * Known Limitations: N/A
 * <p>
 * <b>History</b> <br>
 * <ul>
 * <li>2008/05: Initial file creation. </li>
 * </ul>
 * <p>
 * @author Mathieu Lemay - ITI
 * @version 2.0
 */
public interface IStreamTransport extends ITransport {
	
	/** 
	 * Sends a raw array of bytes to the device		
	 * @param rawInput Sends String to send
     */
	public void send(byte[] rawInput) throws TransportException;
	
	/** 
	 * Sends a raw array of chars to the device		
	 * @param rawInput Sends String to send
     */
	public void send(char[] rawInput) throws TransportException;
	
	/** 
	 * Gets the transport input stream	
	 */
	public InputStream getInputStream() throws TransportException;
	
	/**
	 * Getter for the OutputStream of the Transport 
	 * 
	 */
	public OutputStream getOutputStream() throws TransportException;
}
