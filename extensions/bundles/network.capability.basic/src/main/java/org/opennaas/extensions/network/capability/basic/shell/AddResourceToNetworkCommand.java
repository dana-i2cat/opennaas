package org.opennaas.extensions.network.capability.basic.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.network.capability.basic.INetworkBasicCapability;

@Command(scope = "net", name = "addResource", description = "Add a resource to the network")
public class AddResourceToNetworkCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "network:networkName", description = "The network where to add a resource", required = true, multiValued = false)
	private String	networkId;

	@Argument(index = 1, name = "resourceType:resourceName", description = "The resource to be added", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("add resource to network");

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
			networkCapability.addResource(resource);
		} catch (CapabilityException e) {
			printError("Error adding resource.");
			printError(e);
			printEndCommand();
			return null;
		}

		printInfo("Resource " + resourceId + " added to network " + networkId);
		printEndCommand();
		return null;
	}

}
