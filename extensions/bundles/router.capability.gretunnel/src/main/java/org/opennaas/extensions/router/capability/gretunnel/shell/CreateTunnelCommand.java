package org.opennaas.extensions.router.capability.gretunnel.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.gretunnel.IGRETunnelCapability;
import org.opennaas.extensions.router.model.GRETunnelConfiguration;
import org.opennaas.extensions.router.model.GRETunnelEndpoint;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

/**
 * @author Jordi Puig
 */
@Command(scope = "gretunnel", name = "create", description = "Create a tunnel in router.")
public class CreateTunnelCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to create the GRE Tunnel", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "interfaceName", description = "GRE interface name. Be aware that given name SHOULD match an existing gr interface in the router, otherwise GRE will be configured but will NOT work.", required = true, multiValued = false)
	private String	interfaceName;

	@Argument(index = 2, name = "ipAddress", description = "IP of the tunnel", required = true, multiValued = false)
	private String	ipAddress;

	@Argument(index = 3, name = "ipSource", description = "IP source address", required = true, multiValued = false)
	private String	ipSource;

	@Argument(index = 4, name = "ipDestiny", description = "IP destination address", required = true, multiValued = false)
	private String	ipDestiny;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.karaf.shell.console.AbstractAction#doExecute()
	 */
	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Create GRE tunnel");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);
			IGRETunnelCapability tunnelCapability = (IGRETunnelCapability) router.getCapabilityByInterface(IGRETunnelCapability.class);
			tunnelCapability.createGRETunnel(getTunnelService());
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error creating GRE tunnels");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

	// TODO change this function when we fix the showconfiguration command.
	// so we will be able to know which GRE interface it's in use at the Router.

	/**
	 * @return GRETunnelService with the in parameters setted
	 * @throws Exception
	 */
	private GRETunnelService getTunnelService() throws Exception {
		// Create the GRETunnelService

		GRETunnelService greTunnelService = new GRETunnelService();

		greTunnelService.setName(interfaceName);

		// Create the tunnel configuration
		GRETunnelConfiguration greTunnelConfiguration = new GRETunnelConfiguration();
		greTunnelConfiguration.setSourceAddress(ipSource);
		greTunnelConfiguration.setDestinationAddress(ipDestiny);
		greTunnelService.setGRETunnelConfiguration(greTunnelConfiguration);

		GRETunnelEndpoint greTunnelEndpoint = new GRETunnelEndpoint();

		if (!IPUtilsHelper.isIPValidAddress(ipAddress))
			throw new CommandException("The tunnel address is not a valid ipv4/ipv6 address.");

		String address = IPUtilsHelper.getAddressFromIP(ipAddress);
		String prefix = IPUtilsHelper.getPrefixFromIp(ipAddress);

		// Create the protocol endpoint
		if (IPUtilsHelper.isIPv4ValidAddress(ipAddress)) {
			greTunnelEndpoint.setIPv4Address(address);
			greTunnelEndpoint.setSubnetMask(IPUtilsHelper.parseShortToLongIpv4NetMask(prefix));
			greTunnelEndpoint.setProtocolIFType(ProtocolIFType.IPV4);
		}
		else {
			greTunnelEndpoint.setIPv6Address(address);
			greTunnelEndpoint.setPrefixLength(Short.valueOf(prefix));
			greTunnelEndpoint.setProtocolIFType(ProtocolIFType.IPV6);
		}

		greTunnelService.addProtocolEndpoint(greTunnelEndpoint);
		return greTunnelService;
	}
}