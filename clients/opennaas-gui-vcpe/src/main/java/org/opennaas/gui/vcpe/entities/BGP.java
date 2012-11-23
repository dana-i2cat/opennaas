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
	private List<String>	clientPrefixes;

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
	 * @return the clientPrefixes
	 */
	public List<String> getClientPrefixes() {
		return clientPrefixes;
	}

	/**
	 * @param clientPrefixes
	 *            the clientPrefixes to set
	 */
	public void setClientPrefixes(List<String> clientPrefixes) {
		this.clientPrefixes = clientPrefixes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BGP [clientASNumber=" + clientASNumber + ", nocASNumber=" + nocASNumber + ", clientPrefixes=" + clientPrefixes + "]";
	}

}
