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
public class Neighbours {

	// This map indicates to which remote port and device a port is connected. The key of the map is the local port name, and the value is the remote
	// port, containing the port id and the device id
	private Map<String, Port>	connectionMap;

	public Map<String, Port> getDevicePortMap() {
		return connectionMap;
	}

	public void setDevicePortMap(Map<String, Port> devicePortMap) {
		this.connectionMap = devicePortMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connectionMap == null) ? 0 : connectionMap.hashCode());
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
		Neighbours other = (Neighbours) obj;
		if (connectionMap == null) {
			if (other.connectionMap != null)
				return false;
		} else if (!connectionMap.equals(other.connectionMap))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Neighbours [devicePortMap=" + connectionMap + "]";
	}

}
