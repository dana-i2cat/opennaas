package org.opennaas.core.resources.protocol;

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
