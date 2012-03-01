package org.opennaas.router.capability.gretunnel.shell;

import java.io.IOException;
import java.util.List;

import net.i2cat.mantychore.model.GRETunnelEndpoint;
import net.i2cat.mantychore.model.GRETunnelService;
import net.i2cat.mantychore.model.ProtocolEndpoint;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.router.capability.gretunnel.GRETunnelCapability;

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
			GRETunnelCapability tunnelCapability = (GRETunnelCapability) getCapability(router.getCapabilities(), GRETunnelCapability.CAPABILITY_NAME);
			List<GRETunnelService> lGRETunnelService = tunnelCapability.showGRETunnelConfiguration();
			if (lGRETunnelService == null || lGRETunnelService.size() <= 0) {
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
			printSymbol("No GRE Tunnels in this router");
			return;
		}
		for (GRETunnelService greTunnelService : lGRETunnelService) {
			printSymbol("Gre Tunnel Name:" + greTunnelService.getName());
			printSymbol("Source IP address: " + greTunnelService.getGRETunnelConfiguration().getSourceAddress());
			printSymbol("Destiny IP address: " + greTunnelService.getGRETunnelConfiguration().getDestinationAddress());
			if (!greTunnelService.getProtocolEndpoint().isEmpty()) {
				for (ProtocolEndpoint pep : greTunnelService.getProtocolEndpoint()) {
					GRETunnelEndpoint greTunnelEndpoint = (GRETunnelEndpoint) pep;
					printSymbol("Tunnel IP address: " + greTunnelEndpoint.getIPv4Address());
				}
			}
			printSymbol(doubleLine);
		}
	}
}