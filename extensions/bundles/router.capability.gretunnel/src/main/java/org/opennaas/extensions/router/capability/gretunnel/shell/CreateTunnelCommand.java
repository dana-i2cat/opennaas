package org.opennaas.extensions.router.capability.gretunnel.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.gretunnel.IGRETunnelCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.GREService;
import org.opennaas.extensions.router.model.GRETunnelConfiguration;
import org.opennaas.extensions.router.model.GRETunnelEndpoint;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.ProtocolEndpoint;

/**
 * @author Jordi Puig
 */
@Command(scope = "gretunnel", name = "create", description = "Create a tunnel in router.")
public class CreateTunnelCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to create the GRE Tunnel", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "interfaceName", description = "GRE interface name", required = true, multiValued = false)
	private String	interfaceName;

	@Argument(index = 2, name = "ipv4Address", description = "IP of the tunnel", required = true, multiValued = false)
	private String	ipv4Address;

	@Argument(index = 3, name = "subnetMask", description = "Mask of the tunnel", required = true, multiValued = false)
	private String	subnetMask;

	@Argument(index = 4, name = "ipSource", description = "IP source address", required = true, multiValued = false)
	private String	ipSource;

	@Argument(index = 5, name = "ipDestiny", description = "IP destination address", required = true, multiValued = false)
	private String	ipDestiny;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.karaf.shell.console.AbstractAction#doExecute()
	 */
	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Shows GRE tunnels");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);
			IGRETunnelCapability tunnelCapability = (IGRETunnelCapability) router.getCapabilityByInterface(IGRETunnelCapability.class);
			tunnelCapability.createGRETunnel(getTunnelService());
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error showing GRE tunnels");
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

		IResource router = getResourceFromFriendlyName(resourceId);
		ComputerSystem model = (ComputerSystem) router.getModel();
		GREService greService = model.getAllHostedServicesByType(new GREService()).get(0);

		boolean found = false;
		for (ProtocolEndpoint pE : greService.getProtocolEndpoint()) {
			if (pE.getName().equals(interfaceName))
				found = true;
		}
		if (!found)
			throw new Exception("GRE Interface not available at this router.");

		GRETunnelService greTunnelService = new GRETunnelService();

		greTunnelService.setName(interfaceName);

		// Create the tunnel configuration
		GRETunnelConfiguration greTunnelConfiguration = new GRETunnelConfiguration();
		greTunnelConfiguration.setSourceAddress(ipSource);
		greTunnelConfiguration.setDestinationAddress(ipDestiny);
		greTunnelService.setGRETunnelConfiguration(greTunnelConfiguration);

		// Create the protocol endpoint
		GRETunnelEndpoint greTunnelEndpoint = new GRETunnelEndpoint();
		greTunnelEndpoint.setIPv4Address(ipv4Address);
		greTunnelEndpoint.setSubnetMask(subnetMask);
		greTunnelService.addProtocolEndpoint(greTunnelEndpoint);
		return greTunnelService;
	}
}