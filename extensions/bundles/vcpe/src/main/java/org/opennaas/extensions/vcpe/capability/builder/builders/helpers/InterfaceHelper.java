/**
 * 
 */
package org.opennaas.extensions.vcpe.capability.builder.builders.helpers;

/*
 * #%L
 * OpenNaaS :: vCPENetwork
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

import java.util.List;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.router.capability.chassis.ChassisCapability;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.vcpe.capability.VCPEToRouterModelTranslator;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

/**
 * @author Jordi
 */
public class InterfaceHelper extends GenericHelper {

	/**
	 * @param router
	 * @param ifaces
	 * @param model
	 * @throws ResourceException
	 */
	public static void createInterfaces(Router router, List<Interface> ifaces, VCPENetworkModel model) throws ResourceException {
		IResource routerResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", router.getName()));
		IChassisCapability chassisCapability = (IChassisCapability) routerResource.getCapabilityByInterface(ChassisCapability.class);

		for (Interface iface : ifaces) {
			NetworkPort port = VCPEToRouterModelTranslator.vCPEInterfaceToNetworkPort(iface, model);
			chassisCapability.createSubInterface(port);
			// Note: this call will NOT assign IP addresses to given interfaces
		}
	}

	/**
	 * @param phy
	 * @param ifaces
	 * @param model
	 * @throws ResourceException
	 */
	public static void removeInterfaces(Router phy, List<Interface> ifaces, VCPENetworkModel model) throws ResourceException {
		IResource phyResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", phy.getName()));

		IChassisCapability chassisCapability = (IChassisCapability) phyResource.getCapabilityByInterface(ChassisCapability.class);

		for (Interface iface : ifaces) {
			NetworkPort port = VCPEToRouterModelTranslator.vCPEInterfaceToNetworkPort(iface, model);
			chassisCapability.deleteSubInterface(port);
		}
	}

}
