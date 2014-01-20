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

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.router.capability.staticroute.IStaticRouteCapability;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

/**
 * @author Jordi
 */
public class StaticRouteHelper extends GenericHelper {
	/**
	 * Set a static route
	 * 
	 * @param router
	 * @param model
	 * @param ipRange
	 * @param nextHopIpAddress
	 * @throws ResourceException
	 */
	public static void setStaticRoute(Router router, VCPENetworkModel model, String ipRange, String nextHopIpAddress, boolean isDiscard)
			throws ResourceException {
		IResource routerResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", router.getName()));

		String[] ipRangeAddressAndMask = IPUtilsHelper.composedIPAddressToIPAddressAndMask(ipRange);

		if (ipRangeAddressAndMask.length < 1) {
			throw new ResourceException("Invalid IP address range (missing mask): " + ipRange);
		}

		// nextHopIpAddress should have no mask
		if (nextHopIpAddress != null && nextHopIpAddress.contains("/")) {
			nextHopIpAddress = IPUtilsHelper.composedIPAddressToIPAddressAndMask(nextHopIpAddress)[0];
		}

		IStaticRouteCapability capability = (IStaticRouteCapability) routerResource.getCapabilityByInterface(IStaticRouteCapability.class);
		capability.createStaticRoute(ipRangeAddressAndMask[0], ipRangeAddressAndMask[1], nextHopIpAddress, String.valueOf(isDiscard));
	}

	/**
	 * Delete a static route in router
	 * 
	 * @param router
	 * @param model
	 * @param ipRange
	 * @param nextHopIpAddress
	 * @throws ResourceException
	 */
	public static void deleteStaticRoute(Router router, VCPENetworkModel model, String ipRange, String nextHopIpAddress) throws ResourceException {
		IResource routerResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", router.getName()));

		String[] ipRangeAddressAndMask = IPUtilsHelper.composedIPAddressToIPAddressAndMask(ipRange);

		if (ipRangeAddressAndMask.length < 1) {
			throw new ResourceException("Invalid IP address range (missing mask): " + ipRange);
		}

		IStaticRouteCapability capability = (IStaticRouteCapability) routerResource.getCapabilityByInterface(IStaticRouteCapability.class);
		capability.deleteStaticRoute(ipRangeAddressAndMask[0], ipRangeAddressAndMask[1], nextHopIpAddress);
	}

}
