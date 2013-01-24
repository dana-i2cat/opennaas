/**
 * 
 */
package org.opennaas.extensions.vcpe.capability.builder.builders.helpers;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.extensions.router.capability.ip.IIPCapability;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.vcpe.capability.VCPEToRouterModelTranslator;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

/**
 * @author Jordi
 */
public class IPHelper extends GenericHelper {

	/**
	 * @param router
	 * @param iface
	 * @param model
	 * @throws ResourceException
	 */
	public static void setIP(Router router, Interface iface, VCPENetworkModel model) throws ResourceException {
		IResource routerResource = getResourceManager().getResource(
				getResourceManager().getIdentifierFromResourceName("router", router.getName()));

		IIPCapability capability = (IIPCapability) routerResource.getCapabilityByInterface(IIPCapability.class);

		LogicalPort port = VCPEToRouterModelTranslator.vCPEInterfaceToLogicalPort(iface, model);
		for (ProtocolEndpoint pep : port.getProtocolEndpoint()) {
			if (pep instanceof IPProtocolEndpoint) {
				capability.setIPv4(port, (IPProtocolEndpoint) pep);
			}
		}
	}
}
