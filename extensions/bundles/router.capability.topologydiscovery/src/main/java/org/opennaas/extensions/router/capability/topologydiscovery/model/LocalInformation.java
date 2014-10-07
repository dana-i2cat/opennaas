package org.opennaas.extensions.router.capability.topologydiscovery.model;

/*
 * #%L
 * OpenNaaS :: Router :: Topology Discovery capability
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

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class LocalInformation {

	private String				deviceId;
	// this map indicates the interface name (value) of the interface identified by a specific port ID (key)
	private Map<String, String>	interfacesMap;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Map<String, String> getInterfacesMap() {
		return interfacesMap;
	}

	public void setInterfacesMap(Map<String, String> interfacesMap) {
		this.interfacesMap = interfacesMap;
	}

	@Override
	public String toString() {
		return "LocalInformation [deviceId=" + deviceId + ", interfacesMap=" + interfacesMap + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
		result = prime * result + ((interfacesMap == null) ? 0 : interfacesMap.hashCode());
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
		LocalInformation other = (LocalInformation) obj;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		if (interfacesMap == null) {
			if (other.interfacesMap != null)
				return false;
		} else if (!interfacesMap.equals(other.interfacesMap))
			return false;
		return true;
	}

}
