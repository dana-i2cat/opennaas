package org.opennaas.extensions.router.capability.gretunnel.shell;

import java.io.IOException;
import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.gretunnel.IGRETunnelCapability;
import org.opennaas.extensions.router.model.GRETunnelEndpoint;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

/**
 * @author Jordi Puig
 */
@Command(scope = "gretunnel", name = "show", description = "Shows GRE tunnels in router.")
public class ShowTunnelsCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to show the GRE Tunnels", required = true, multiValued = false)
	private String	resourceId;

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
			List<GRETunnelService> lGRETunnelService = tunnelCapability.showGRETunnelConfiguration();
			if (lGRETunnelService == null || lGRETunnelService.isEmpty()) {
				printInfo("No GRE tunnels configured on the router");
			} else {
				printGRETunnelConfiguration(lGRETunnelService);
			}
			return null;
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
	 * @param lGRETunnelService
	 * @throws IOException
	 */
	private void printGRETunnelConfiguration(List<GRETunnelService> lGRETunnelService) throws IOException {
		if (lGRETunnelService == null) {
			printSymbol("No GRE tunnels configured on the router");
			return;
		}
		for (GRETunnelService greTunnelService : lGRETunnelService) {
			printSymbol("GRE tunnel: " + greTunnelService.getName());
			printSymbol("\tSource IP address: " + greTunnelService.getGRETunnelConfiguration().getSourceAddress());
			printSymbol("\tDestiny IP address: " + greTunnelService.getGRETunnelConfiguration().getDestinationAddress());
			if (!greTunnelService.getProtocolEndpoint().isEmpty()) {
				for (ProtocolEndpoint pep : greTunnelService.getProtocolEndpoint()) {
					GRETunnelEndpoint greTunnelEndpoint = (GRETunnelEndpoint) pep;
					printSymbol("\tTunnel IP address: " + getIPTunnelAddress(greTunnelEndpoint));
				}
			}
			printSymbol(doubleLine);
		}
	}

	/**
	 * Mount the ip address with the mask to show it.
	 * 
	 * @param greTunnelEndpoint
	 * @return ip/mask
	 */
	private String getIPTunnelAddress(GRETunnelEndpoint greTunnelEndpoint) {

		String ipAddress;
		String prefix;

		if (greTunnelEndpoint.getProtocolIFType().equals(ProtocolIFType.IPV4)) {
			ipAddress = greTunnelEndpoint.getIPv4Address();
			prefix = greTunnelEndpoint.getSubnetMask() != null ?
					IPUtilsHelper.parseLongToShortIpv4NetMask(greTunnelEndpoint.getSubnetMask()) : null;
		} else {
			ipAddress = greTunnelEndpoint.getIPv6Address();
			prefix = String.valueOf(greTunnelEndpoint.getPrefixLength());
		}

		return (ipAddress != null && prefix != null) ? ipAddress + "/" + prefix : "";
	}
}