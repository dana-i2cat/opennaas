package org.opennaas.extensions.gim.model.energy;

public enum EnergyType {
	Oil("oil"), Gas("gas"), Coal("coal"), Wind("wind"), Solar("solar"), Hydro("hydro");

	private final String	text;

	/**
	 * @param text
	 */
	private EnergyType(final String text) {
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return text;
	}

	public static EnergyType fromString(String text) {
		for (EnergyType type : EnergyType.values()) {
			if (type.toString().equals(text))
				return type;
		}
		throw new IllegalArgumentException("Invalid value" + text);
	}
}
