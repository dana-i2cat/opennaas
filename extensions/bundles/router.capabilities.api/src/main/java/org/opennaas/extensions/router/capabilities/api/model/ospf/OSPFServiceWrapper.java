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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;

@XmlRootElement(name = "ospfService")
@XmlAccessorType(XmlAccessType.FIELD)
public class OSPFServiceWrapper implements Serializable {

	private static final long			serialVersionUID	= 6379966189844614183L;

	@XmlElement(required = false)
	private String						routerId;

	@XmlElement
	private EnabledState				enabledState;

	@XmlElement(name = "ospfArea")
	private Collection<OSPFAreaWrapper>	ospfAreas;

	public String getRouterId() {
		return routerId;
	}

	public void setRouterId(String routerId) {
		this.routerId = routerId;
	}

	public EnabledState getEnabledState() {
		return enabledState;
	}

	public void setEnabledState(EnabledState enabledState) {
		this.enabledState = enabledState;
	}

	public Collection<OSPFAreaWrapper> getOspfAreas() {
		return ospfAreas;
	}

	public void setOspfArea(Collection<OSPFAreaWrapper> ospfAreas) {
		this.ospfAreas = ospfAreas;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((enabledState == null) ? 0 : enabledState.hashCode());
		result = prime * result + ((ospfAreas == null) ? 0 : ospfAreas.hashCode());
		result = prime * result + ((routerId == null) ? 0 : routerId.hashCode());
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
		OSPFServiceWrapper other = (OSPFServiceWrapper) obj;
		if (enabledState != other.enabledState)
			return false;
		if (ospfAreas == null) {
			if (other.ospfAreas != null)
				return false;
		} else if (!ospfAreas.equals(other.ospfAreas))
			return false;
		if (routerId == null) {
			if (other.routerId != null)
				return false;
		} else if (!routerId.equals(other.routerId))
			return false;
		return true;
	}
}
