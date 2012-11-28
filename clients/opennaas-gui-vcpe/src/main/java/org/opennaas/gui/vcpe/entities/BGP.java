/**
 * 
 */
package org.opennaas.gui.vcpe.entities;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Jordi
 */
public class BGP {

	@NotBlank(message = "{error.field.mandatory}")
	private String			clientASNumber;
	@NotBlank(message = "{error.field.mandatory}")
	private String			nocASNumber;
	@NotNull
	@Size(min = 1, message = "{error.field.mandatory}")
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
