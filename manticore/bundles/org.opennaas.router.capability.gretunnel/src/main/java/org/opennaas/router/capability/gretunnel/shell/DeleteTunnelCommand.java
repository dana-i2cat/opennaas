package org.opennaas.router.capability.gretunnel.shell;

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
@Command(scope = "gretunnel", name = "delete", description = "Delete a GRE tunnel in router.")
public class DeleteTunnelCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to show the GRE Tunnels", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "tunnelName", description = "Name of the tunnel to delete", required = true, multiValued = false)
	private String	tunnelName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.karaf.shell.console.AbstractAction#doExecute()
	 */
	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Delete GRE tunnel " + tunnelName);
		try {
			IResource router = getResourceFromFriendlyName(resourceId);
			GRETunnelCapability tunnelCapability = (GRETunnelCapability) getCapability(router.getCapabilities(), GRETunnelCapability.CAPABILITY_NAME);
			Response response = tunnelCapability.deleteGRETunnel(getTunnelService());
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
	 * @return
	 */
	private GRETunnelService getTunnelService() {
		GRETunnelService greTunnelService = new GRETunnelService();
		greTunnelService.setName(tunnelName);
		return greTunnelService;
	}

}