package org.opennaas.extensions.router.capability.vrrp.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.vrrp.IVRRPCapability;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.NetworkPort.LinkTechnology;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

/**
 * @author Julio Carlos Barrera
 */
@Command(scope = "vrrp", name = "unconfigure", description = "Unconfigure VRRP in given IP Address in an interface of a router")
public class UnconfigureVRRPCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to unconfigure VRRP", required = true, multiValued =
			false)
	private String	resourceId;

	@Argument(index = 1, name = "interfaceName", description = "Name of the interface in the router", required = true, multiValued =
			false)
	private String	interfaceName;

	@Argument(index = 2, name = "interfaceIPAddress", description = "IP address of the interface in the router", required = true, multiValued =
			false)
	private String	interfaceIPAddress;

	@Argument(index = 3, name = "vrrpGroupId", description = "The VRRP group ID", required = true, multiValued =
			false)
	private int		vrrpGroupId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Unconfigure VRRP");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);

			IVRRPCapability vrrpCapability = (IVRRPCapability) router.getCapabilityByInterface(IVRRPCapability.class);

			// get interface name and port
			String[] argsInterface = splitInterfaces(interfaceName);
			String interfaceName = argsInterface[0];
			int port = Integer.parseInt(argsInterface[1]);

			NetworkPort routerInterface = new NetworkPort();
			routerInterface.setName(interfaceName);
			routerInterface.setPortNumber(port);
			routerInterface.setLinkTechnology(LinkTechnology.OTHER);

			// TODO setIPv4 or IPv6? now IPv4
			IPProtocolEndpoint interfaceIPProtocolEndpoint = new IPProtocolEndpoint();
			String[] ipParts = IPUtilsHelper.composedIPAddressToIPAddressAndMask(interfaceIPAddress);
			interfaceIPProtocolEndpoint.setIPv4Address(ipParts[0]);
			interfaceIPProtocolEndpoint.setSubnetMask(ipParts[1]);

			// TODO get VRRPProtocolEndpoint (from the refreshed model) with VRRPGroup and unconfigure it
			// VRRPProtocolEndpoint vrrpProtocolEndpoint = null;
			// List<ProtocolEndpoint> bindedProtocolEndpoints = interfaceIPProtocolEndpoint.getBindedProtocolEndpoints();
			// for (ProtocolEndpoint protocolEndpoint : bindedProtocolEndpoints) {
			// if (protocolEndpoint instanceof VRRPProtocolEndpoint) {
			// int vrrpName = ((VRRPGroup) ((VRRPProtocolEndpoint) protocolEndpoint).getService()).getVrrpName();
			// if (vrrpName == vrrpGroupId) {
			// // VRRPGroup found!
			// vrrpProtocolEndpoint = (VRRPProtocolEndpoint) protocolEndpoint;
			// break;
			// }
			// }
			// }
			// if (vrrpProtocolEndpoint != null) {
			// vrrpCapability.unconfigureVRRP(vrrpProtocolEndpoint);
			// }

			// reconstruct an uncompleted model
			VRRPGroup vrrpGroup = new VRRPGroup();
			vrrpGroup.setVrrpName(vrrpGroupId);

			VRRPProtocolEndpoint vrrpProtocolEndpoint = new VRRPProtocolEndpoint();
			interfaceIPProtocolEndpoint.addLogiaclPort(routerInterface);
			vrrpProtocolEndpoint.bindServiceAccessPoint(interfaceIPProtocolEndpoint);
			vrrpProtocolEndpoint.setService(vrrpGroup);

			vrrpCapability.unconfigureVRRP(vrrpProtocolEndpoint);
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error unconfiguring VRRP.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}
