package org.opennaas.extensions.openflowswitch.capability.controllerinformation.model;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public enum HealthState {

	OK("ok"),
	KO("ko");
	private final String	value;

	HealthState(String value) {
		this.value = value;
	}

	public static HealthState fromValue(String v) {
		for (HealthState c : HealthState.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
