package org.opennaas.extensions.router.junos.commandsets.digester;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet
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
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;
import org.opennaas.extensions.router.model.BridgeDomain;
import org.opennaas.extensions.router.model.System;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * @author Julio Carlos Barrera
 * 
 */
public class VLANParser extends DigesterEngine {

	private static final String	VLAN_INTERFACE_NAME		= "vlan";

	// temporary maps
	private Map<String, String>	l3VlanInterfaceMap		= new HashMap<String, String>();
	private Map<String, String>	vlanUnitIPAddressMap	= new HashMap<String, String>();

	// temporary vars
	private String				l3Interface				= null;

	private String				vlanName				= null;

	private String				interfaceName			= null;
	private String				unit					= null;

	class ParserRuleSet extends RuleSetBase {

		private String	prefix	= "";

		protected ParserRuleSet() {

		}

		protected ParserRuleSet(String prefix) {
			this.prefix = prefix;
		}

		@Override
		public void addRuleInstances(Digester digester) {

			// FIXME the path pattern can't be global , must distinguish between routers

			// ***************
			// * parse VLANS *
			// ***************
			addObjectCreate("*/vlans/vlan", BridgeDomain.class);
			addMyRule("*/vlans/vlan/name", "setVLANName", 0);
			addCallMethod("*/vlans/vlan/vlan-id", "setVlanId", 0, new Class[] { Integer.TYPE });
			addCallMethod("*/vlans/vlan/description", "setDescription", 0);
			addCallMethod("*/vlans/vlan/interface", "addNetworkPort", 0);

			// L3 Bridge Domains have interface.unit
			addMyRule("*/vlans/vlan/l3-interface", "setL3Interface", 0);

			// add BridgeDomain to model when closing tag
			addSetNext("*/vlans/vlan", "addBridgeDomain");

			// *************************
			// * parse VLAN interfaces *
			// *************************
			addMyRule("*/interfaces/interface/name", "setInterfaceName", 0);
			// unit
			addMyRule("*/interfaces/interface/unit", "setUnit", 0);
			// IPv4 or IPv6
			addMyRule("*/interfaces/interface/unit/family/inet/address/name", "setIPAddress", 0);
			addMyRule("*/interfaces/interface/unit/family/inet6/address/name", "setIPAddress", 0);

			addSetNext("/", "setVLANIPAddresses");

		}
	}

	private System	model;

	public VLANParser(System model) {
		ruleSet = new ParserRuleSet();
		setModel(model);
	}

	public System getModel() {
		return model;
	}

	public void setModel(System model) {
		this.model = model;
	}

	public void addBridgeDomain(BridgeDomain domain) {

		model.addHostedCollection(domain);

		// update l3VlanInterfaceMap when adding BridgeDomain
		updateL3VlanInterfaceMap();

	}

	public void setVLANName(String vlanName) {
		// get BridgeDomain from the stack and set its ElementName
		BridgeDomain bridgeDomain = (BridgeDomain) peek(0);
		bridgeDomain.setElementName(vlanName);

		// story vlanName in the temporary var
		this.vlanName = vlanName;
	}

	public void setL3Interface(String l3Interface) {
		this.l3Interface = l3Interface;
	}

	private void updateL3VlanInterfaceMap() {
		if (vlanName == null || l3Interface == null) {
			log.error("There must be valid 'vlanName' and 'l3Interface' values");
			return;
		}
		// store the relation between the vlanName and the associated interface
		l3VlanInterfaceMap.put(vlanName, l3Interface);

		// unset temporary var
		vlanName = null;
	}

	public void setInterfaceName(String interfaceName) {
		// parse only Junos VLAN interfaces
		if (interfaceName.equals(VLAN_INTERFACE_NAME)) {
			this.interfaceName = interfaceName;
			return;
		}

		this.interfaceName = null;
	}

	public void setUnit(String unit) {
		// parse only Junos VLAN interfaces
		if (interfaceName != null && interfaceName.equals(VLAN_INTERFACE_NAME)) {
			this.unit = unit;
			return;
		}

		this.unit = null;
	}

	public void setIPAddress(String ipAddress) {
		// parse only Junos VLAN interfaces
		if (interfaceName != null && interfaceName.equals(VLAN_INTERFACE_NAME)) {
			vlanUnitIPAddressMap.put(VLAN_INTERFACE_NAME + "." + unit, ipAddress);
		}

		// unset temporary vars
		this.interfaceName = null;
		this.unit = null;
	}

	public void setVLANIPAddresses() {
		// validate collected data
		if (l3VlanInterfaceMap.size() != vlanUnitIPAddressMap.size()) {
			log.error("Invalid L3 VLANs data collected.");
			return;
		}

		for (String l3Vlan : l3VlanInterfaceMap.keySet()) {
			// match data to get IP address
			String interfaceName = l3VlanInterfaceMap.get(l3Vlan);
			String ipAddress = vlanUnitIPAddressMap.get(interfaceName);

			// store IP address into its BridgeDomain
			setBridgeDomainIPAddress(l3Vlan, ipAddress);
		}
	}

	private void setBridgeDomainIPAddress(String bridgeDomainName, String ipAddress) {
		for (BridgeDomain bridgeDomain : model.getAllHostedCollectionsByType(BridgeDomain.class)) {
			if (bridgeDomainName.equals(bridgeDomainName)) {
				bridgeDomain.setIpAddress(ipAddress);
				return;
			}
		}

		throw new IllegalArgumentException("BrdgeDomain not found with name: " + bridgeDomainName);
	}

}
