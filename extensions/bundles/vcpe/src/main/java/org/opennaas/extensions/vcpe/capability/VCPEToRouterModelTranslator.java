package org.opennaas.extensions.vcpe.capability;

import java.util.List;

import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.VLANEndpoint;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class VCPEToRouterModelTranslator {

	public static ComputerSystem vCPERouterToRouter(Router router, VCPENetworkModel model) {
		ComputerSystem cimRouter = vCPERouterToRouterWithoutIfaces(router, model);

		for (Interface iface : router.getInterfaces()) {
			LogicalPort port = vCPEInterfaceToLogicalPort(iface, model);
			cimRouter.addLogicalDevice(port);
		}
		return cimRouter;
	}

	public static ComputerSystem vCPERouterToRouterWithoutIfaces(Router router, VCPENetworkModel model) {
		ComputerSystem cimRouter = new ComputerSystem();
		cimRouter.setName(router.getName());
		cimRouter.setElementName(router.getName());

		return cimRouter;
	}

	public static LogicalPort vCPEInterfaceToLogicalPort(Interface iface, VCPENetworkModel model) {
		// split name into name and unit number.
		String[] ifaceNameParts = iface.getName().split("\\.");
		boolean isLogical = ifaceNameParts.length > 1;

		LogicalPort port;
		if (isLogical) {
			port = vCPEInterfaceToNetworkPort(iface, model);
		} else {
			port = new LogicalPort();
			port.setName(iface.getName());

			port = assignIPAddressAndVlan(iface, port);
		}
		return port;
	}

	public static NetworkPort vCPEInterfaceToNetworkPort(Interface iface, VCPENetworkModel model) {
		// split name into name and unit number.
		String[] ifaceNameParts = iface.getName().split("\\.");
		boolean isLogical = ifaceNameParts.length > 1;
		if (!isLogical) {
			// TODO launch more appropriate exception
			throw new IllegalArgumentException("Cannot convert a physical interface into NetworkPort");
		}

		NetworkPort port;
		// decide port type from iface name (eth, lt...)
		if (iface.getName().startsWith("lt")) {
			port = new LogicalTunnelPort();
			// set lt peer unit
			Interface peerIface = findPeerInterface(iface, model);
			String[] peerIfaceNameParts = peerIface.getName().split("\\.");
			((LogicalTunnelPort) port).setPeer_unit(Long.parseLong(peerIfaceNameParts[1]));

		} else {
			port = new EthernetPort();
		}

		port.setName(ifaceNameParts[0]);
		port.setPortNumber(Integer.parseInt(ifaceNameParts[1]));

		port = (NetworkPort) assignIPAddressAndVlan(iface, port);
		return port;
	}

	private static LogicalPort assignIPAddressAndVlan(Interface source, LogicalPort target) {

		if (source.getVlan() != 0) {
			VLANEndpoint vlanEP = new VLANEndpoint();
			vlanEP.setVlanID(Integer.parseInt(Long.toString(source.getVlan())));
			target.addProtocolEndpoint(vlanEP);
		}
		if (source.getIpAddress() != null) {
			target.addProtocolEndpoint(ipAddressToProtocolEndpoint(source.getIpAddress()));
		}
		return target;
	}

	public static IPProtocolEndpoint ipAddressToProtocolEndpoint(String ipAddress) {
		String[] addressAndMask = IPUtilsHelper.composedIPAddressToIPAddressAndMask(ipAddress);

		IPProtocolEndpoint ipEP = new IPProtocolEndpoint();
		ipEP.setIPv4Address(addressAndMask[0]);
		if (addressAndMask.length > 1)
			ipEP.setSubnetMask(addressAndMask[1]);
		return ipEP;
	}

	private static Interface findPeerInterface(Interface iface, VCPENetworkModel model) {
		Interface peerIface = null;

		// find lt link
		Link ltLink = null;
		List<Link> links = VCPENetworkModelHelper.getLinks(model.getElements());
		for (Link link : links) {
			if (link.getType().equals(VCPETemplate.LINK_TYPE_LT)) {
				if (link.getSource().equals(iface) || link.getSink().equals(iface)) {
					ltLink = link;
					break;
				}
			}
		}

		if (ltLink != null) {
			if (ltLink.getSource().equals(iface)) {
				peerIface = ltLink.getSink();
			} else {
				peerIface = ltLink.getSource();
			}
		}
		return peerIface;
	}

}
