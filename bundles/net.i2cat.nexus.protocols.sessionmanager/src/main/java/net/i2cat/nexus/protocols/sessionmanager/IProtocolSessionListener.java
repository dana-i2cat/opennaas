package net.i2cat.nexus.protocols.sessionmanager;

/**
 * Listens for messages received by the protocol session
 * 
 * @author eduardgrasa
 * 
 */
public interface IProtocolSessionListener {

	/**
	 * Callback operation to receive the messages sent by the device
	 * 
	 * @param message
	 */
	public void messageReceived(Object message);
}
