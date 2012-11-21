/**
 * 
 */
package org.opennaas.gui.vcpe.entities;

import java.util.List;

/**
 * @author Jordi
 */
public class BGP {

	private String			clientASNumber;
	private String			nocASNumber;
	private List<String>	customerPrefixes;

	/**
	 * @return the clientASNumber
	 */
	public String getClientASNumber() {
		return clientASNumber;
	}

	/**
	 * @param clientASNumber
	 *            the clientASNumber to set
	 */
	public void setClientASNumber(String clientASNumber) {
		this.clientASNumber = clientASNumber;
	}

	/**
	 * @return the nocASNumber
	 */
	public String getNocASNumber() {
		return nocASNumber;
	}

	/**
	 * @param nocASNumber
	 *            the nocASNumber to set
	 */
	public void setNocASNumber(String nocASNumber) {
		this.nocASNumber = nocASNumber;
	}

	/**
	 * @return the customerPrefixes
	 */
	public List<String> getCustomerPrefixes() {
		return customerPrefixes;
	}

	/**
	 * @param customerPrefixes
	 *            the customerPrefixes to set
	 */
	public void setCustomerPrefixes(List<String> customerPrefixes) {
		this.customerPrefixes = customerPrefixes;
	}

}
