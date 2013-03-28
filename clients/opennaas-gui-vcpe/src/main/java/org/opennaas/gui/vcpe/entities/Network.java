/**
 * 
 */
package org.opennaas.gui.vcpe.entities;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Jordi
 */
public class Network {

	@NotNull
	@Size(min = 1, max = 25)
	private String		name;
	private String		templateName;
	private String		asNumber;
	@Pattern(regexp = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])/(\\d{1}|[0-2]{1}\\d{1}|3[0-2])$", message = "{message.error.field.format.ipandmask}")
	private String		iPAddressRange;
	@Valid
	private Interface	networkInterface;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the aSNumber
	 */
	public String getASNumber() {
		return asNumber;
	}

	/**
	 * @param asNumber the asNumber to set
	 */
	public void setASNumber(String asNumber) {
		this.asNumber = asNumber;
	}

	/**
	 * @return the iPAddressRange
	 */
	public String getiPAddressRange() {
		return iPAddressRange;
	}

	/**
	 * @param iPAddressRange the iPAddressRange to set
	 */
	public void setiPAddressRange(String iPAddressRange) {
		this.iPAddressRange = iPAddressRange;
	}

	/**
	 * @return the networkInterface
	 */
	public Interface getNetworkInterface() {
		return networkInterface;
	}

	/**
	 * @param networkInterface the networkInterface to set
	 */
	public void setNetworkInterface(Interface networkInterface) {
		this.networkInterface = networkInterface;
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

}
