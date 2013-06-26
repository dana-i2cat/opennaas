package org.opennaas.extensions.gim.model.energy;

public enum EnergyClass {
	Green("green"), Brown("brown"), Mixed("mixed");

	private final String	text;

	/**
	 * @param text
	 */
	private EnergyClass(final String text) {
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

	public static EnergyClass fromString(String text) {
		for (EnergyClass type : EnergyClass.values()) {
			if (type.toString().equals(text))
				return type;
		}
		throw new IllegalArgumentException("Invalid value" + text);
	}
}
