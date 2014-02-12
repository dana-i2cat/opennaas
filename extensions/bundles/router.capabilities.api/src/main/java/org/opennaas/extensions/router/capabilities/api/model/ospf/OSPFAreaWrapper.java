package org.opennaas.extensions.router.capabilities.api.model.ospf;

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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.router.model.OSPFArea.AreaType;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@XmlRootElement(name = "ospfArea", namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class OSPFAreaWrapper implements Serializable {

	private static final long						serialVersionUID	= -4577590599939054176L;

	private String									areaId;

	private AreaType								areaType;

	@XmlElementWrapper(name = "ospfProtocolEndpoints")
	private Collection<OSPFProtocolEndpointWrapper>	ospfProtocolEndpoint;

	public Collection<OSPFProtocolEndpointWrapper> getOspfProtocolEndpoints() {
		return ospfProtocolEndpoint;
	}

	public void setOspfProtocolEndpoints(Collection<OSPFProtocolEndpointWrapper> ospfProtocolEndpoints) {
		this.ospfProtocolEndpoint = ospfProtocolEndpoints;
	}

	public String getAreaID() {
		return areaId;
	}

	public void setAreaID(String name) {
		this.areaId = name;
	}

	public AreaType getAreaType() {
		return areaType;
	}

	public void setAreaType(AreaType areaType) {
		this.areaType = areaType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((areaId == null) ? 0 : areaId.hashCode());
		result = prime * result + ((areaType == null) ? 0 : areaType.hashCode());
		result = prime * result + ((ospfProtocolEndpoint == null) ? 0 : ospfProtocolEndpoint.hashCode());
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
		if (areaType != other.areaType)
			return false;
		if (ospfProtocolEndpoint == null) {
			if (other.ospfProtocolEndpoint != null)
				return false;
		} else if (!ospfProtocolEndpoint.equals(other.ospfProtocolEndpoint))
			return false;
		return true;
	}

}
