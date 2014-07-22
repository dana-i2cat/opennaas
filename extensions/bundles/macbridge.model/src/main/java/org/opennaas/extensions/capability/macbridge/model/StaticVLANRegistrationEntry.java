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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a static VLAN Registration Entry in the Filtering database as specified by standard IEEE 802.1q
 * 
 * @author eduardgrasa
 * 
 */
@XmlRootElement
public class StaticVLANRegistrationEntry {

	/**
	 * The VLAN id
	 */
	private int						vlanID				= 0;

	/**
	 * A Port Map, consisting of a control element for each outbound Port
	 */
	private List<PortConfiguration>	portConfigurations	= null;

	public StaticVLANRegistrationEntry() {
		this.portConfigurations = new ArrayList<PortConfiguration>();
	}

	public List<PortConfiguration> getPortConfigurations() {
		return this.portConfigurations;
	}

	public int getVlanID() {
		return vlanID;
	}

	public void setVlanID(int vlanID) {
		this.vlanID = vlanID;
	}
}
