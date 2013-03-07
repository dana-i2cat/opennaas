/**
 * 
 */
package org.opennaas.gui.vcpe.entities;

import java.util.List;

/**
 * @author Jordi
 */
public class Network {

	private String			name;
	private String			templateName;
	private String			asNumber;
	private List<String>	iPAddressRanges;
	private Interface		networkInterface;

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
	 * @return the iPAddressRanges
	 */
	public List<String> getiPAddressRanges() {
		return iPAddressRanges;
	}

	/**
	 * @param iPAddressRanges the iPAddressRanges to set
	 */
	public void setiPAddressRanges(List<String> iPAddressRanges) {
		this.iPAddressRanges = iPAddressRanges;
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
