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

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class Neighbours {

	// This map indicates to which device a port belongs. The key of the map is the port ID, and the value is the device ID.
	private Map<String, String>	devicePortMap;

	public Map<String, String> getDevicePortMap() {
		return devicePortMap;
	}

	public void setDevicePortMap(Map<String, String> devicePortMap) {
		this.devicePortMap = devicePortMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((devicePortMap == null) ? 0 : devicePortMap.hashCode());
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
		if (devicePortMap == null) {
			if (other.devicePortMap != null)
				return false;
		} else if (!devicePortMap.equals(other.devicePortMap))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Neighbours [devicePortMap=" + devicePortMap + "]";
	}

}
