package net.i2cat.nexus.protocols.sessionmanager;

import net.i2cat.nexus.resources.protocol.IProtocolMessageFilter;

/**
 * The protocol session listener will always be notified with this filter
 * 
 * @author eduardgrasa
 * 
 */
public class NotifyAlwaysProtocolMessageFilter implements IProtocolMessageFilter {
	public boolean notify(Object message) {
		return true;
	}

}
