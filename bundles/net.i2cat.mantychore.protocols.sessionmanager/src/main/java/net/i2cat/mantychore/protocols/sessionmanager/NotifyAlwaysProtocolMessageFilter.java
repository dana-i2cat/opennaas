package net.i2cat.mantychore.protocols.sessionmanager;

/**
 * The protocol session listener will always be notified with this filter
 * 
 * @author eduardgrasa
 * 
 */
public class NotifyAlwaysProtocolMessageFilter implements IProtocolMessageFilter {
	@Override
	public boolean notify(Object message) {
		return true;
	}

}
