package org.opennaas.extensions.router.capabilities.api.model.vlanbridge;

/*
 * #%L
 * OpenNaaS :: Router :: VLAN bridge Capability
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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents the configuration of a BridgeDomain.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@XmlRootElement(name = "bridgeDomain", namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class BridgeDomain {

	private String			domainName;
	private int				vlanid;
	@XmlElementWrapper(name = "interfacesNames")
	@XmlElement(name = "interfaceName")
	private List<String>	interfacesNames;
	private String			description;

	private String			ipAddress;

	public BridgeDomain() {
		interfacesNames = new ArrayList<String>();
	}

	/**
	 * @return the domainName
	 */
	public String getDomainName() {
		return domainName;
	}

	/**
	 * @param domainName
	 *            the domainName to set
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	/**
	 * @return the vlanid
	 */
	public int getVlanid() {
		return vlanid;
	}

	/**
	 * @param vlanid
	 *            the vlanid to set
	 */
	public void setVlanid(int vlanid) {
		this.vlanid = vlanid;
	}

	/**
	 * @return the interfacesNames
	 */
	public List<String> getInterfacesNames() {
		return interfacesNames;
	}

	/**
	 * @param interfacesNames
	 *            the interfacesNames to set
	 */
	public void setInterfacesNames(List<String> interfacesNames) {
		this.interfacesNames = interfacesNames;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((domainName == null) ? 0 : domainName.hashCode());
		result = prime * result + ((interfacesNames == null) ? 0 : interfacesNames.hashCode());
		result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result + vlanid;
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
		BridgeDomain other = (BridgeDomain) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (domainName == null) {
			if (other.domainName != null)
				return false;
		} else if (!domainName.equals(other.domainName))
			return false;
		if (interfacesNames == null) {
			if (other.interfacesNames != null)
				return false;
		} else if (!interfacesNames.equals(other.interfacesNames))
			return false;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		if (vlanid != other.vlanid)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BridgeDomain [domainName=" + domainName + ", vlanid=" + vlanid + ", interfacesNames=" + interfacesNames + ", description=" + description + ", ipAddress=" + ipAddress + "]";
	}

}
