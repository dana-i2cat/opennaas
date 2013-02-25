/**
 * 
 */
package org.opennaas.gui.vcpe.entities;

import java.util.List;

/**
 * @author Jordi
 */
public class Network {

	private Integer			aSNumber;
	private List<String>	iPAddressRanges;
	private Interface		networkInterface;

	/**
	 * @return the aSNumber
	 */
	public Integer getaSNumber() {
		return aSNumber;
	}

	/**
	 * @param aSNumber the aSNumber to set
	 */
	public void setaSNumber(Integer aSNumber) {
		this.aSNumber = aSNumber;
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

}
