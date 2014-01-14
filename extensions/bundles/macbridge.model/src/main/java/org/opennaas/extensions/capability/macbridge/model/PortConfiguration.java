package org.opennaas.extensions.capability.macbridge.model;

/*
 * #%L
 * OpenNaaS :: MAC Bridge :: Model
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

public class PortConfiguration {

	public enum RegistrationType {
		REGISTRATION_FIXED, REGISTRATION_FORBIDDEN, NORMAL_REGISTRATION
	};

	/**
	 * The number of the port to whom this configuration applies
	 */
	private String				portInterfaceId		= null;

	/**
	 * The Registrar Administrative Control values for the GVRP protocol (Clause 11) for the VLAN. In addition to providing control over the operation
	 * of GVRP, these values can also directly affect the forwarding behavior of the Bridge, as described in 8.8.9.
	 */
	private RegistrationType	registrationType	= RegistrationType.NORMAL_REGISTRATION;

	/**
	 * Whether frames are to be VLAN-tagged or untagged when transmitted. The entries in the Port Map that specify untagged transmission compose the
	 * untagged set for the VLAN. The untagged set is empty if no Static VLAN Registration Entry exists for the VLAN.
	 */
	private boolean				tagged				= false;

	public PortConfiguration() {
	}

	public PortConfiguration(String portInterfaceId, boolean tagged) {
		this.portInterfaceId = portInterfaceId;
		this.tagged = tagged;
	}

	public String getPortInterfaceId() {
		return portInterfaceId;
	}

	public void setPortInterfaceId(String portInterfaceId) {
		this.portInterfaceId = portInterfaceId;
	}

	public RegistrationType getRegistrationType() {
		return registrationType;
	}

	public void setRegistrationType(RegistrationType registrationType) {
		this.registrationType = registrationType;
	}

	public boolean isTagged() {
		return tagged;
	}

	public void setTagged(boolean tagged) {
		this.tagged = tagged;
	}
}
