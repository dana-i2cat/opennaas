package org.opennaas.core.protocols.sessionmanager;

import org.opennaas.core.resources.protocol.IProtocolMessageFilter;

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
