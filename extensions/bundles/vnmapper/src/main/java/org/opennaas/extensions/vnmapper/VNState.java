package org.opennaas.extensions.vnmapper;

public enum VNState {
	SKIPPED("skipped"),
	ERROR("error"),
	SUCCESSFUL("successful");

	private String	value;

	VNState(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

}
