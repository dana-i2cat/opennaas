/**
 * 
 */
package org.opennaas.gui.vcpe.entities;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Jordi
 */
public class Interface {

	public enum Types {
		INTER("Inter"), UP("Up"), DOWN("Down");

		private final String	text;

		/**
		 * @param text
		 */
		private Types(final String text) {
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

	}

	@NotNull
	@Size(min = 1, max = 25)
	private String	name;

	@NotNull
	@Size(min = 1, max = 25)
	private String	port;

	@NotNull
	@Size(min = 1, max = 25)
	private String	ipAddress;

	@NotNull
	@Size(min = 1, max = 25)
	private Integer	vlan;

	private String	templateName;

	private String	labelName;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the vlan
	 */
	public Integer getVlan() {
		return vlan;
	}

	/**
	 * @param vlan
	 *            the vlan to set
	 */
	public void setVlan(Integer vlan) {
		this.vlan = vlan;
	}

	/**
	 * @return the labelName
	 */
	public String getLabelName() {
		return labelName;
	}

	/**
	 * @param labelName
	 *            the labelName to set
	 */
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName
	 *            the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * @return
	 */
	public String getCompleteName() {
		return getName() + "." + getPort();
	}

	/**
	 * @return
	 */
	public static String getNameFromCompleteName(String completeName) {
		String aCompleteName[] = completeName.split("\\.");
		return aCompleteName[0];
	}

	/**
	 * @return
	 */
	public static String getPortFromCompleteName(String completeName) {
		String aCompleteName[] = completeName.split("\\.");
		return aCompleteName[1];
	}
}
