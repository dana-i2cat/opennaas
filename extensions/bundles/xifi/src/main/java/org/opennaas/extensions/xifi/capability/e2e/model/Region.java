package org.opennaas.extensions.xifi.capability.e2e.model;

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

/**
 * XIFI Region configuration
 * 
 * @author Julio Carlos Barrera
 *
 */
public class Region {

	private String	name;
	private String	ryuEndpoint;
	private String	openstackEndpoint;
	private String	autobahnInterface;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRyuEndpoint() {
		return ryuEndpoint;
	}

	public void setRyuEndpoint(String ryuEndpoint) {
		this.ryuEndpoint = ryuEndpoint;
	}

	public String getOpenstackEndpoint() {
		return openstackEndpoint;
	}

	public void setOpenstackEndpoint(String openstackEndpoint) {
		this.openstackEndpoint = openstackEndpoint;
	}

	public String getAutobahnInterface() {
		return autobahnInterface;
	}

	public void setAutoBAHNInterface(String autobahnInterface) {
		this.autobahnInterface = autobahnInterface;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((autobahnInterface == null) ? 0 : autobahnInterface.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((openstackEndpoint == null) ? 0 : openstackEndpoint.hashCode());
		result = prime * result + ((ryuEndpoint == null) ? 0 : ryuEndpoint.hashCode());
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
		Region other = (Region) obj;
		if (autobahnInterface == null) {
			if (other.autobahnInterface != null)
				return false;
		} else if (!autobahnInterface.equals(other.autobahnInterface))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (openstackEndpoint == null) {
			if (other.openstackEndpoint != null)
				return false;
		} else if (!openstackEndpoint.equals(other.openstackEndpoint))
			return false;
		if (ryuEndpoint == null) {
			if (other.ryuEndpoint != null)
				return false;
		} else if (!ryuEndpoint.equals(other.ryuEndpoint))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Region [name=" + name + ", ryuEndpoint=" + ryuEndpoint + ", openstackEndpoint=" + openstackEndpoint + ", autobahnInterface=" + autobahnInterface + "]";
	}

}
