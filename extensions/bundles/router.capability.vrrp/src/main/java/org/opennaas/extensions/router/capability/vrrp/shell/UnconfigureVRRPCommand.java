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

			VRRPProtocolEndpoint vrrpProtocolEndpoint = new VRRPProtocolEndpoint();
			VRRPGroup vrrpGroup = new VRRPGroup();
			vrrpGroup.setVrrpName(vrrpGroupId);

			NetworkPort netPort = buildNetworkPort();
			IPProtocolEndpoint ipProtocolEndpoint = buildIPProtocolEndpoint();

			ipProtocolEndpoint.addLogiaclPort(netPort);

			vrrpProtocolEndpoint.bindServiceAccessPoint(ipProtocolEndpoint);
			vrrpProtocolEndpoint.setProtocolIFType(ipProtocolEndpoint.getProtocolIFType());
			vrrpProtocolEndpoint.setService(vrrpGroup);

			vrrpCapability.unconfigureVRRP(vrrpProtocolEndpoint);
			printEndCommand();
			return null;

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
