/**
 * 
 */
package org.opennaas.web.entities;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Jordi
 */
public class Interface {

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

}
