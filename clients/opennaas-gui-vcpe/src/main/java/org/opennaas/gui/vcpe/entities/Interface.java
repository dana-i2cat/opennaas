/**
 * 
 */
package org.opennaas.gui.vcpe.entities;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Jordi
 */
public class Interface {

	@Size(min = 1, max = 25, message = "{message.error.field.mandatory}")
	private String	name;
	@DecimalMin(value = "0", message = "{message.error.field.format.port}")
	private String	port;
	@Pattern(regexp = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])/(\\d{1}|[0-2]{1}\\d{1}|3[0-2])$", message = "{message.error.field.format.ipandmask}")
	private String	ipAddress;
	@DecimalMin(value = "0", message = "{message.error.field.format.vlan}")
	@DecimalMax(value = "4094", message = "{message.error.field.format.vlan}")
	private Integer	vlan;
	private String	templateName;
	private String	type;

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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
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

	/**
	 * @author Jordi
	 */
	public enum Types {
		INTER("Inter"), UP("Up"), DOWN("Down"), CLIENT("Client"), LOOPBACK("Loopback");

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

}
