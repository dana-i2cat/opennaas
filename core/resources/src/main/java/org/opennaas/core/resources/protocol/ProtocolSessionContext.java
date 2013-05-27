package org.opennaas.core.resources.protocol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Encapsulates the parameters required to create a protocol session, including the data required to connect to the remote device and the parameters
 * that define the behavior of the session (keepAlive, ...)
 * 
 * @author eduardgrasa
 * 
 */
@XmlRootElement
public class ProtocolSessionContext implements Cloneable {

	public static final String	PROTOCOL			= "protocol";
	public static final String	AUTH_TYPE			= "protocol.authType";
	public static final String	PROTOCOL_URI		= "protocol.uri";
	public static final String	PROTOCOL_VERSION	= "protocol.version";
	public static final String	KEEP_ALIVE			= "protocol.keepAlive";
	public static final String	USERNAME			= "protocol.username";
	public static final String	PASSWORD			= "protocol.password";
	public static final String	KEY_USERNAME		= "protocol.keyUsername";
	public static final String	KEY_PATH			= "protocol.keyPath";
	public static final String	KEY_PASSPHRASE		= "protocol.keyPassphrase";

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

	public boolean equals(Object object) {
		ProtocolSessionContext other = null;

		if (object == null) {
			return false;
		}

		if (object instanceof ProtocolSessionContext) {
			other = (ProtocolSessionContext) object;
		} else {
			return false;
		}

		// Check that I contain all the keys that the other
		// protocolsessioncontext contains
		if (!sessionParameters.keySet().containsAll(other.getSessionParameters().keySet()))
			return false;
		// Check that the other protocolsessioncontext contains all the keys
		// I contain
		if (!other.getSessionParameters().keySet().containsAll(sessionParameters.keySet()))
			return false;

		// Check that values are the same
		Iterator<String> iterator = sessionParameters.keySet().iterator();
		String key;
		Object value = null;
		Object othervalue = null;
		while (iterator.hasNext()) {
			key = iterator.next();
			value = sessionParameters.get(key);
			othervalue = other.getSessionParameters().get(key);
			// consider null equals null
			if (value == null) {
				if (othervalue != null) {
					return false;
				}
			} else if (!value.equals(othervalue)) {
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

	@Override
	public ProtocolSessionContext clone() {
		ProtocolSessionContext clone = new ProtocolSessionContext();
		clone.getSessionParameters().putAll(sessionParameters);
		return clone;
	}

}
