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
import org.opennaas.extensions.router.capability.vrrp.IVRRPCapability;
import org.opennaas.extensions.router.capability.vrrp.VRRPCapability;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.vcpe.capability.VCPEToRouterModelTranslator;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VRRP;

/**
 * @author Jordi
 */
public class VRRPHelper extends GenericHelper {

	/**
	 * Configure VRRP
	 * 
	 * @param resource
	 * @param model
	 * @throws ResourceException
	 */
	public static void configureVRRP(IResource resource, VCPENetworkModel model) throws ResourceException {
		// obtain VRRP from VCPENetworkModel
		VRRP vrrp = model.getVrrp();

		// create VRRPGroup and VRRPProtocolEndpoint's
		VRRPGroup vrrpGroup = new VRRPGroup();

		// set VRRPGroup parameters
		vrrpGroup.setVrrpName(vrrp.getGroup());
		vrrpGroup.setVirtualIPAddress(vrrp.getVirtualIPAddress());

		// obtain CIM model NetworkPort's (router interfaces)
		NetworkPort masterNetworkPort = VCPEToRouterModelTranslator.vCPEInterfaceToNetworkPort(vrrp.getMasterInterface(), model);
		NetworkPort backupNetworkPort = VCPEToRouterModelTranslator.vCPEInterfaceToNetworkPort(vrrp.getBackupInterface(), model);

		// configure both VRRPProtocolEndpoint's
		configureVRRPProtocolEndpoint(vrrp.getPriorityMaster(), vrrp.getMasterInterface(), vrrp.getMasterRouter(), vrrpGroup, masterNetworkPort);
		configureVRRPProtocolEndpoint(vrrp.getPriorityBackup(), vrrp.getBackupInterface(), vrrp.getBackupRouter(), vrrpGroup, backupNetworkPort);
	}

	/**
	 * Unconfigure VRRP
	 * 
	 * @param resource
	 * @param model
	 * @throws ResourceException
	 */
	public void unconfigureVRRP(IResource resource, VCPENetworkModel model) throws ResourceException {
		// TODO
	}

	/**
	 * @param vrrpPriority
	 * @param iface
	 * @param router
	 * @param vrrgrGroup
	 * @param networkPort
	 * @throws ResourceException
	 */
	private static void configureVRRPProtocolEndpoint(int vrrpPriority, Interface iface, Router router, VRRPGroup vrrgrGroup, NetworkPort networkPort)
			throws ResourceException {
		// create VRRPProtocolEndpoint
		VRRPProtocolEndpoint vrrpProtocolEndpoint = new VRRPProtocolEndpoint();
		vrrpProtocolEndpoint.setProtocolIFType(ProtocolIFType.IPV4);

		// set VRRPProtocolEndpoint' parameters
		vrrpProtocolEndpoint.setPriority(vrrpPriority);

		// link CIM VRRP model elements
		vrrpProtocolEndpoint.setService(vrrgrGroup);

		// link VRRP CIM model elements to CIM model elements
		// obtain router interface
		if (networkPort.getName().equals(iface.getPhysicalInterfaceName()) &&
				networkPort.getPortNumber() == (iface.getPort())) {
			// obtain master interface IP address
			List<ProtocolEndpoint> ipAddresses = networkPort.getProtocolEndpoint();
			for (ProtocolEndpoint protocolEndpoint : ipAddresses) {
				if (protocolEndpoint instanceof IPProtocolEndpoint &&
						(((IPProtocolEndpoint) protocolEndpoint).getIPv4Address() + "/" + IPUtilsHelper
								.parseLongToShortIpv4NetMask(((IPProtocolEndpoint) protocolEndpoint).getSubnetMask())).equals(iface.getIpAddress())) {
					// link VRRPProtocolEndpoint with IPProtocolEndpoint
					vrrpProtocolEndpoint.bindServiceAccessPoint(protocolEndpoint);
				}
			}
		}

		// obtain VRRPCapability and apply the configuration to interface
		IResource routerResource = getResourceManager().getResource(getResourceManager().getIdentifierFromResourceName("router", router.getName()));
		IVRRPCapability vrrpCapability = (IVRRPCapability) routerResource.getCapabilityByInterface(VRRPCapability.class);
		vrrpCapability.configureVRRP(vrrpProtocolEndpoint);
	}
}
