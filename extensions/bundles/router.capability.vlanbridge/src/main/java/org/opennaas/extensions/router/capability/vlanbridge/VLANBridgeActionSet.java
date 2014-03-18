package org.opennaas.extensions.router.capability.vlanbridge;

/*
 * #%L
 * OpenNaaS :: Router :: VLAN bridge Capability
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


import org.opennaas.core.resources.action.IActionSetDefinition;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class VLANBridgeActionSet implements IActionSetDefinition {

	/**
	 * If executed, configures given BridgeDomain in the resource in whose queue is executed.
	 * 
	 * @param bridgeDomain
	 * @throws ActionException
	 *             if failed to configure given BridgeDomain.
	 */
	public static final String	CREATE_VLAN_BRIDGE_DOMAIN_ACTION	= "createVlanBridgeDomainAction";

	/**
	 * If executed, updates the BridgeDomain with given domainName with the configuration in given BridgeDomain object.
	 * 
	 * @param domainName
	 * @param bridgeDomain
	 * @throws ActionException
	 *             if failed to configure given BridgeDomain.
	 */
	public static final String	UPDATE_VLAN_BRIDGE_DOMAIN_ACTION	= "updateVlanBridgeDomainAction";

	/**
	 * If executed, deletes the BridgeDomain with given domainName.
	 * 
	 * @param domainName
	 * @throws ActionException
	 *             if failed to delete given BridgeDomain.
	 */
	public static final String	DELETE_VLAN_BRIDGE_DOMAIN_ACTION	= "deleteVlanBridgeDomainAction";

	/**
	 * If executed, sets given InterfaceVLANOptions for interface with given ifaceName.
	 * 
	 * @param interfaceName
	 * @param InterfaceVLANOptions
	 *            to set
	 * @throws ActionException
	 *             if failed to configure given InterfaceVLANOptions
	 */
	public static final String	SET_INTERFACE_VLAN_OPTIONS_ACTION	= "setInterfaceVlanOptionsAction";
}
