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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wraper for a list of bridged domain names.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@XmlRootElement(name = "bridgeDomains", namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class BridgeDomains {

	@XmlElement(name = "domainName")
	private List<String>	domainNames;

	public BridgeDomains() {
		domainNames = new ArrayList<String>();
	}

	/**
	 * @return the domainNames
	 */
	public List<String> getDomainNames() {
		return domainNames;
	}

	/**
	 * @param domainNames
	 *            the domainNames to set
	 */
	public void setDomainNames(List<String> domainNames) {
		this.domainNames = domainNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domainNames == null) ? 0 : domainNames.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BridgeDomains other = (BridgeDomains) obj;
		if (domainNames == null) {
			if (other.domainNames != null)
				return false;
		} else if (!domainNames.equals(other.domainNames))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BridgeDomains [domainNames=" + domainNames + "]";
	}
}
