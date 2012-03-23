package org.opennaas.extensions.network.capability.basic.shell;

import org.opennaas.extensions.router.model.ManagedElement;
import org.opennaas.extensions.router.model.mappers.Cim2NdlMapper;
import org.opennaas.extensions.network.capability.basic.ITopologyManager;
import org.opennaas.extensions.network.capability.basic.NetworkBasicCapability;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.repository.NetworkMapperModelToDescriptor;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;
import org.opennaas.core.resources.shell.GenericKarafCommand;

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

		ICapability networkCapability = getCapability(network.getCapabilities(), NetworkBasicCapability.CAPABILITY_NAME);
		if (! (networkCapability instanceof ITopologyManager)) {
			printError("Failed to get required capability.");
			printEndCommand();
			return null;
		}

		try {
			((ITopologyManager)networkCapability).addResource(resource);
		} catch (CapabilityException e){
			printError("Error adding resource.");
			printError(e);
			printEndCommand();
			return null;
		}

		printInfo("Resource " + resourceId + "added to network " + networkId);
		printEndCommand();
		return null;
	}

}
