package net.i2cat.mantychore.protocols.sessionmanager;

/**
 * Evaluates if a protocol session listener should be notified when the protocol
 * session receives an asynchronous message from a device
 * 
 * @author eduardgrasa
 * 
 */
public interface IProtocolMessageFilter {
	public boolean notify(Object message);
}
