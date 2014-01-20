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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a VLAN Configuration, with a name and a vlanId
 * 
 * @author eduardgrasa
 * 
 */
@XmlRootElement
public class VLANConfiguration {

	/**
	 * A name describing the VLAN
	 */
	private String	name	= null;

	/**
	 * The VLAN ID
	 */
	private int		vlanID	= 0;

	public VLANConfiguration() {
	}

	public VLANConfiguration(String name, int vlanID) {
		this.name = name;
		this.vlanID = vlanID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getVlanID() {
		return vlanID;
	}

	public void setVlanID(int vlanID) {
		this.vlanID = vlanID;
	}

}
