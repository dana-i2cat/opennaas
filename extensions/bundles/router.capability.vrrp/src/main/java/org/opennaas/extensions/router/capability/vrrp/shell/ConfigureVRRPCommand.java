package org.opennaas.extensions.router.capability.vrrp.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.vrrp.IVRRPCapability;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
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

			NetworkPort routerInterface = buildNetworkPort();

			IPProtocolEndpoint interfaceIPProtocolEndpoint = buildIPProtocolEndpoint();

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

	private NetworkPort buildNetworkPort() throws Exception {

		NetworkPort netPort = new NetworkPort();
		String[] argsInterface = splitInterfaces(interfaceName);
		String interfaceName = argsInterface[0];
		int port = Integer.parseInt(argsInterface[1]);

		netPort.setName(interfaceName);
		netPort.setPortNumber(port);

		return netPort;
	}

	private IPProtocolEndpoint buildIPProtocolEndpoint() throws CommandException {

		IPProtocolEndpoint pE = new IPProtocolEndpoint();

		if (IPUtilsHelper.isIPv4ValidAddress(interfaceIPAddress)) {

			String address = IPUtilsHelper.getAddressFromIP(interfaceIPAddress);
			String mask = IPUtilsHelper.getPrefixFromIp(address);

			pE.setIPv4Address(address);
			pE.setSubnetMask(IPUtilsHelper.parseShortToLongIpv4NetMask(mask));
			pE.setProtocolIFType(ProtocolIFType.IPV4);

		} else if (IPUtilsHelper.isIPv4ValidAddress(interfaceIPAddress)) {

			String address = IPUtilsHelper.getAddressFromIP(interfaceIPAddress);
			String prefix = IPUtilsHelper.getPrefixFromIp(address);

			pE.setIPv6Address(address);
			pE.setPrefixLength(Short.valueOf(prefix));
			pE.setProtocolIFType(ProtocolIFType.IPV6);

		} else
			throw new CommandException("Invalid IP format.");

		return pE;
	}
}