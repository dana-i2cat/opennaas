package org.opennaas.extensions.genericnetwork.model.driver;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An identifier of what is being used to implement a NetworkConnection. This class is meant to be used for drivers to track the object being used to
 * implement a NetworkConnection. It would normally refer to a Circuit, or to an OFFlow, but that depends on the driver.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class NetworkConnectionImplementationId {

	private String	deviceId;

	private String	circuitId;

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId
	 *            the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return the circuitId
	 */
	public String getCircuitId() {
		return circuitId;
	}

	/**
	 * @param circuitId
	 *            the circuitId to set
	 */
	public void setCircuitId(String circuitId) {
		this.circuitId = circuitId;
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
		result = prime * result + ((circuitId == null) ? 0 : circuitId.hashCode());
		result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
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
		NetworkConnectionImplementationId other = (NetworkConnectionImplementationId) obj;
		if (circuitId == null) {
			if (other.circuitId != null)
				return false;
		} else if (!circuitId.equals(other.circuitId))
			return false;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		return true;
	}
}
