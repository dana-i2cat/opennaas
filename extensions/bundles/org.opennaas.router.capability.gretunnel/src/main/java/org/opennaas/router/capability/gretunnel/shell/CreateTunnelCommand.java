package org.opennaas.router.capability.gretunnel.shell;

import net.i2cat.mantychore.model.GRETunnelConfiguration;
import net.i2cat.mantychore.model.GRETunnelEndpoint;
import net.i2cat.mantychore.model.GRETunnelService;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.router.capability.gretunnel.GRETunnelCapability;

/**
 * @author Jordi Puig
 */
@Command(scope = "gretunnel", name = "create", description = "Create a tunnel in router.")
public class CreateTunnelCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to create the GRE Tunnel", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "tunnelName", description = "Name of the tunnel to create", required = true, multiValued = false)
	private String	tunnelName;

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
			GRETunnelCapability tunnelCapability = (GRETunnelCapability) getCapability(router.getCapabilities(), GRETunnelCapability.CAPABILITY_NAME);
			Response response = tunnelCapability.createGRETunnel(getTunnelService());
			return printResponseStatus(response);
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
	}

	/**
	 * @return GRETunnelService with the in parameters setted
	 */
	private GRETunnelService getTunnelService() {
		// Create the GRETunnelService
		GRETunnelService greTunnelService = new GRETunnelService();
		greTunnelService.setName(tunnelName);

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