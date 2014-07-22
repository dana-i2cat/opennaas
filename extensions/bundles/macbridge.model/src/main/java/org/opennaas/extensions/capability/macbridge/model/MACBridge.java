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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.LogicalDevice;

/**
 * A class that represents a managed MAC Bridge as specified by the IEEE 802.1d and IEEE 802.1q standards
 * 
 * @author eduardgrasa
 * 
 */
public class MACBridge extends ComputerSystem {

	private static final long				serialVersionUID	= -5917279387174821799L;

	/**
	 * The VLAN database
	 */
	private Map<Integer, VLANConfiguration>	vlanDatabase		= null;

	/**
	 * The filtering database
	 */
	private FilteringDatabase				filteringDatabase	= null;

	public MACBridge() {
		super();
		this.vlanDatabase = new HashMap<Integer, VLANConfiguration>();
		this.filteringDatabase = new FilteringDatabase();
	}

	public void clearVLANDatabase() {
		this.vlanDatabase.clear();
	}

	public void createOrUpdateVLANConfiguration(String name, int vlanID) {
		this.vlanDatabase.put(new Integer(vlanID), new VLANConfiguration(name, vlanID));
	}

	public void deleteVLANConfiguration(int vlanID) {
		this.vlanDatabase.remove(new Integer(vlanID));
	}

	public Map<Integer, VLANConfiguration> getVLANDatabase() {
		return this.vlanDatabase;
	}

	public FilteringDatabase getFilteringDatabase() {
		return this.filteringDatabase;
	}

	public EthernetPort getPort(String portName) {
		List<LogicalDevice> ports = this.getLogicalDevices();
		EthernetPort currentPort = null;

		for (int i = 0; i < ports.size(); i++) {
			currentPort = (EthernetPort) ports.get(i);
			if (currentPort.getName().equals(portName)) {
				return currentPort;
			}
		}

		return null;
	}
}
