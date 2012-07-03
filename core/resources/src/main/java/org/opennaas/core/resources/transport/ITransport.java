package org.opennaas.core.resources.transport;

/**
 * An interface to describe transport types used to send/receive data from the devices.
 * 
 * <p>
 * Known Limitations: N/A
 * <p>
 * <b>History</b> <br>
 * <ul>
 * <li>2008/05: Changed to fit IaaS Engine.</li>
 * <li>2006/04: Change in the transport to be integrated in the engine. </li>
 * <li>2003/08: Initial file creation. </li>
 * </ul>
 * <p>
 * Communications Research Centre and University of Ottawa - Copyright &copy 2003 <br>
 * </p>
 * @author Mathieu Lemay - Research Technologist, Communications Research Centre
 * @version 2.0
 */

public interface ITransport
{
	/** 
	 * Connects to the device
	 * @throws TransportException 
	 */
	public void connect() throws TransportException;

	/** Disconnects from the device 
	 * @throws TransportException 
	 * */    
	public void disconnect() throws TransportException;
	
}

