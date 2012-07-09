package org.opennaas.core.resources.protocol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Encapsulates the parameters required to create a protocol session, including the data required to connect to the remote device and the parameters
 * that define the behavior of the session (keepAlive, ...)
 * 
 * @author eduardgrasa
 * 
 */
public class ProtocolSessionContext {

	public static final String	PROTOCOL			= "protocol";
	public static final String	PROTOCOL_URI		= "protocol.uri";
	public static final String	PROTOCOL_VERSION	= "protocol.version";
	public static final String	KEEP_ALIVE			= "protocol.keepAlive";
	public static final String USERNAME = "protocol.username";
	public static final String PASSWORD = "protocol.password";

	private Map<String, Object>	sessionParameters	= null;

	public ProtocolSessionContext() {
		sessionParameters = new HashMap<String, Object>();
	}

	/**
	 * @param sessionParameters
	 *            the sessionParameters to set
	 */
	public void setSessionParameters(Map<String, Object> sessionParameters) {
		this.sessionParameters = sessionParameters;
	}

	public void addParameter(String key, String value) {
		sessionParameters.put(key, value);
	}

	public void removeParamter(String key) {
		sessionParameters.remove(key);
	}

	public Map<String, Object> getSessionParameters() {
		return sessionParameters;
	}

	@Override
	public boolean equals(Object object) {
		ProtocolSessionContext other = null;
		Entry<String, Object> entry = null;
		Object value = null;

		if (object == null) {
			return false;
		}

		if (object instanceof ProtocolSessionContext) {
			other = (ProtocolSessionContext) object;
		} else {
			return false;
		}

		// Check that the other protocolsessioncontext contains all the keys and
		// values I contain
		Iterator<Entry<String, Object>> iterator = sessionParameters.entrySet().iterator();
		while (iterator.hasNext()) {
			entry = iterator.next();
			value = other.getSessionParameters().get(entry.getKey());
			if (value == null) {
				return false;
			}
			if (!value.equals(entry.getValue())) {
				return false;
			}
		}

		// Check that I contain all the keys and values that the other other
		// protocolsessioncontext contains
		iterator = other.getSessionParameters().entrySet().iterator();
		while (iterator.hasNext()) {
			entry = iterator.next();
			value = sessionParameters.get(entry.getKey());
			if (value == null) {
				return false;
			}
			if (!value.equals(entry.getValue())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		String result = "";
		Entry<String, Object> entry = null;

		Iterator<Entry<String, Object>> iterator = sessionParameters.entrySet().iterator();
		while (iterator.hasNext()) {
			entry = iterator.next();
			result = result + entry.getKey() + " = " + entry.getValue() + "\n";
		}

		return result;
	}

}
