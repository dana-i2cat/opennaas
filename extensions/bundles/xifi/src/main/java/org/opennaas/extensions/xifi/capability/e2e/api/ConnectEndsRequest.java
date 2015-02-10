package org.opennaas.extensions.xifi.capability.e2e.api;

/*
 * #%L
 * OpenNaaS :: XIFI
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
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

import org.opennaas.extensions.xifi.capability.e2e.IE2ECapability;

/**
 * Request for {@link IE2ECapability#connectEnds(ConnectEndsRequest)} method
 * 
 * @author Julio Carlos Barrera
 *
 */
@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectEndsRequest {

	private String	sourceInstance;
	private String	sourceRegion;
	private String	destinationInstance;
	private String	destinationRegion;

	public String getSourceInstance() {
		return sourceInstance;
	}

	public void setSourceInstance(String sourceInstance) {
		this.sourceInstance = sourceInstance;
	}

	public String getSourceRegion() {
		return sourceRegion;
	}

	public void setSourceRegion(String sourceRegion) {
		this.sourceRegion = sourceRegion;
	}

	public String getDestinationInstance() {
		return destinationInstance;
	}

	public void setDestinationInstance(String destinationInstance) {
		this.destinationInstance = destinationInstance;
	}

	public String getDestinationRegion() {
		return destinationRegion;
	}

	public void setDestinationRegion(String destinationRegion) {
		this.destinationRegion = destinationRegion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destinationInstance == null) ? 0 : destinationInstance.hashCode());
		result = prime * result + ((destinationRegion == null) ? 0 : destinationRegion.hashCode());
		result = prime * result + ((sourceInstance == null) ? 0 : sourceInstance.hashCode());
		result = prime * result + ((sourceRegion == null) ? 0 : sourceRegion.hashCode());
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
		ConnectEndsRequest other = (ConnectEndsRequest) obj;
		if (destinationInstance == null) {
			if (other.destinationInstance != null)
				return false;
		} else if (!destinationInstance.equals(other.destinationInstance))
			return false;
		if (destinationRegion == null) {
			if (other.destinationRegion != null)
				return false;
		} else if (!destinationRegion.equals(other.destinationRegion))
			return false;
		if (sourceInstance == null) {
			if (other.sourceInstance != null)
				return false;
		} else if (!sourceInstance.equals(other.sourceInstance))
			return false;
		if (sourceRegion == null) {
			if (other.sourceRegion != null)
				return false;
		} else if (!sourceRegion.equals(other.sourceRegion))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConnectEndsRequest [sourceInstance=" + sourceInstance + ", sourceRegion=" + sourceRegion + ", destinationInstance=" + destinationInstance + ", destinationRegion=" + destinationRegion + "]";
	}

}
