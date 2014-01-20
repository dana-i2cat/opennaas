/**
 * 
 */
package org.opennaas.extensions.vcpe.model;

/*
 * #%L
 * OpenNaaS :: vCPENetwork
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.opennaas.extensions.router.model.ComputerSystem;

/**
 * @author Jordi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BGP {

	private String			clientASNumber;
	private String			nocASNumber;
	private List<String>	customerPrefixes;

	@XmlTransient
	private ComputerSystem	bgpConfigForMaster;
	@XmlTransient
	private ComputerSystem	bgpConfigForBackup;

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

	public ComputerSystem getBgpConfigForMaster() {
		return bgpConfigForMaster;
	}

	public void setBgpConfigForMaster(ComputerSystem bgpConfigForMaster) {
		this.bgpConfigForMaster = bgpConfigForMaster;
	}

	public ComputerSystem getBgpConfigForBackup() {
		return bgpConfigForBackup;
	}

	public void setBgpConfigForBackup(ComputerSystem bgpConfigForBackup) {
		this.bgpConfigForBackup = bgpConfigForBackup;
	}

	// TODO Include bgpConfigForMaster and bgpConfigForBackup in equals and hashcode
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientASNumber == null) ? 0 : clientASNumber.hashCode());
		result = prime * result + ((customerPrefixes == null) ? 0 : customerPrefixes.hashCode());
		result = prime * result + ((nocASNumber == null) ? 0 : nocASNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BGP other = (BGP) obj;
		if (clientASNumber == null) {
			if (other.clientASNumber != null)
				return false;
		} else if (!clientASNumber.equals(other.clientASNumber))
			return false;
		if (customerPrefixes == null) {
			if (other.customerPrefixes != null)
				return false;
		} else if (!customerPrefixes.equals(other.customerPrefixes))
			return false;
		if (nocASNumber == null) {
			if (other.nocASNumber != null)
				return false;
		} else if (!nocASNumber.equals(other.nocASNumber))
			return false;
		return true;
	}

}
