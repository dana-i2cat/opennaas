/**
 * 
 */
package org.opennaas.gui.nfvrouting.enums;

/**
 * @author Jordi
 */
/**
 * @author Jordi
 */
public enum Template {
	SINGLE_PROVIDER("sp_vcpe"), MULTIPLE_PROVIDER("mp_vcpe");

	private final String	template;

	/**
	 * @param text
	 */
	private Template(final String template) {
		this.template = template;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return template;
	}

}
