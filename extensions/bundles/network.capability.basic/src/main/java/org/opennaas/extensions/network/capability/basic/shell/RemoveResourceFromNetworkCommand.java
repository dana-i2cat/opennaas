package org.opennaas.extensions.network.capability.basic.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.network.capability.basic.INetworkBasicCapability;

@Command(scope = "net", name = "removeResource", description = "Remove a resource from the network")
public class RemoveResourceFromNetworkCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "network:networkName", description = "The network where to remove the resource from", required = true, multiValued = false)
	private String	networkId;

	@Argument(index = 1, name = "resourceType:resourceName", description = "The resource to be removed", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("add resource from network");

		// load resources
		IResource network;
		IResource resource;
		try {
			network = getResourceFromFriendlyName(networkId);
			resource = getResourceFromFriendlyName(resourceId);
		} catch (Exception e) {
			printError("Failed to get required resources: " + e.getLocalizedMessage());
			printEndCommand();
			return null;
		}

		INetworkBasicCapability networkCapability =
				(INetworkBasicCapability) network.getCapabilityByInterface(INetworkBasicCapability.class);

		try {
			networkCapability.removeResource(resource);
		} catch (CapabilityException e) {
			printError("Error deleting resource.");
			printError(e);
			printEndCommand();
			return null;
		}

		printInfo("Resource " + resourceId + " removed from network " + networkId);
		printEndCommand();
		return null;
	}

}