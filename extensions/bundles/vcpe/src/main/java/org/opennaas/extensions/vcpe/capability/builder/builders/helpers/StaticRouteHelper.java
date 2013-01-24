/**
 * 
 */
package org.opennaas.extensions.vcpe.capability.builder.builders.helpers;

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
