package org.opennaas.extensions.router.capability.vrrp.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.vrrp.IVRRPCapability;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

/**
 * @author Julio Carlos Barrera
 */
@Command(scope = "vrrp", name = "configure", description = "Configure VRRP in given IP Address in an interface of a router")
public class ConfigureVRRPCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to configure VRRP", required = true, multiValued =
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

	@Argument(index = 4, name = "priority", description = "The priority of the router inside this VRRP group", required = true, multiValued =
			false)
	private int		priority;

	@Argument(index = 5, name = "virtualIPAddress", description = "The virtual IP address", required = true, multiValued =
			false)
	private String	virtualIPAddress;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Configure VRRP");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);

			IVRRPCapability vrrpCapability = (IVRRPCapability) router.getCapabilityByInterface(IVRRPCapability.class);

			VRRPGroup vrrpGroup = new VRRPGroup();
			VRRPProtocolEndpoint vrrpProtocolEndpoint = new VRRPProtocolEndpoint();

			// get interface name and port
			String[] argsInterface = splitInterfaces(interfaceName);
			String interfaceName = argsInterface[0];
			int port = Integer.parseInt(argsInterface[1]);

			NetworkPort routerInterface = new NetworkPort();
			routerInterface.setName(interfaceName);
			routerInterface.setPortNumber(port);

			// TODO setIPv4 or IPv6? now IPv4
			IPProtocolEndpoint interfaceIPProtocolEndpoint = new IPProtocolEndpoint();
			String[] ipParts = IPUtilsHelper.composedIPAddressToIPAddressAndMask(interfaceIPAddress);
			interfaceIPProtocolEndpoint.setIPv4Address(ipParts[0]);
			interfaceIPProtocolEndpoint.setSubnetMask(ipParts[1]);

			vrrpProtocolEndpoint.setPriority(priority);

			vrrpGroup.setVrrpName(vrrpGroupId);
			vrrpGroup.setVirtualIPAddress(virtualIPAddress);

			interfaceIPProtocolEndpoint.addLogiaclPort(routerInterface);
			vrrpProtocolEndpoint.bindServiceAccessPoint(interfaceIPProtocolEndpoint);
			vrrpProtocolEndpoint.setService(vrrpGroup);

			vrrpCapability.configureVRRP(vrrpProtocolEndpoint);
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error configuring VRRP.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}