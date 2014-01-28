package org.opennaas.extensions.router.capability.ospf.api;

/*
 * #%L
 * OpenNaaS :: Router :: OSPF capability
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

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ospfArea")
@XmlAccessorType(XmlAccessType.FIELD)
public class OSPFAreaWrapper implements Serializable {

	private static final long						serialVersionUID	= -4577590599939054176L;

	private String									areaId;

	@XmlElement(name = "ospfProtocolEndpoint")
	private Collection<OSPFProtocolEndpointWrapper>	ospfProtocolEndpoints;

	public Collection<OSPFProtocolEndpointWrapper> getOspfProtocolEndpoints() {
		return ospfProtocolEndpoints;
	}

	public void setOspfProtocolEndpoints(Collection<OSPFProtocolEndpointWrapper> ospfProtocolEndpoints) {
		this.ospfProtocolEndpoints = ospfProtocolEndpoints;
	}

	public String getAreaID() {
		return areaId;
	}

	public void setAreaID(String name) {
		this.areaId = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((areaId == null) ? 0 : areaId.hashCode());
		result = prime * result + ((ospfProtocolEndpoints == null) ? 0 : ospfProtocolEndpoints.hashCode());
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
		OSPFAreaWrapper other = (OSPFAreaWrapper) obj;
		if (areaId == null) {
			if (other.areaId != null)
				return false;
		} else if (!areaId.equals(other.areaId))
			return false;
		if (ospfProtocolEndpoints == null) {
			if (other.ospfProtocolEndpoints != null)
				return false;
		} else if (!ospfProtocolEndpoints.equals(other.ospfProtocolEndpoints))
			return false;
		return true;
	}

}
