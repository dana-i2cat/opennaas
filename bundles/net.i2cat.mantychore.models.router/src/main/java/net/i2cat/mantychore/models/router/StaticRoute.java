package net.i2cat.mantychore.models.router;

public class StaticRoute {

	protected String	destinationNetworkIPAddress;
	protected String	nextHopIPAddress;
	protected boolean	isIPv6;

	/**
	 * Gets the value of the destinationNetworkIPAddress property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDestinationNetworkIPAddress() {
		return destinationNetworkIPAddress;
	}

	/**
	 * Sets the value of the destinationNetworkIPAddress property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDestinationNetworkIPAddress(String value) {
		this.destinationNetworkIPAddress = value;
	}

	/**
	 * Gets the value of the nextHopIPAddress property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNextHopIPAddress() {
		return nextHopIPAddress;
	}

	/**
	 * Sets the value of the nextHopIPAddress property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setNextHopIPAddress(String value) {
		this.nextHopIPAddress = value;
	}

	/**
	 * Gets the value of the isIPv6 property.
	 * 
	 */
	public boolean isIsIPv6() {
		return isIPv6;
	}

	/**
	 * Sets the value of the isIPv6 property.
	 * 
	 */
	public void setIsIPv6(boolean value) {
		this.isIPv6 = value;
	}

}
