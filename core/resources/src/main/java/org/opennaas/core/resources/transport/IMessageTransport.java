package org.opennaas.core.resources.transport;

import java.io.IOException;

/**
 * An interface for Message based transports. These transports receive individual messages from the communications channel
 * (either synchronous responses or asynchronous events) instead of receiving a continuous stream of characters or bytes
 * 
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
 * </p>
 */
public interface IMessageTransport extends ITransport{
	
	/** 
	 * Sends a raw object to the device		
	 * @param rawInput the object to send
     * @throws IOException
	 */
	public Object sendMessage(Object message);
	
	/**
	 * Subscribers will get asynchronous messages
	 * @param listener
	 */
	public void subscribe(IMessageTransportListener listener);
	
	/**
	 * Cancel the subscription to asynchronous messages
	 * @param listener
	 */
	public void unsubscribe(IMessageTransportListener listener);
}
