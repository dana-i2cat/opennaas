package net.i2cat.mantychore.network.capability.basic.shell;

import java.util.List;

import net.i2cat.mantychore.network.model.NetworkModel;
import net.i2cat.mantychore.network.model.NetworkModelHelper;
import net.i2cat.mantychore.network.model.topology.NetworkElement;
import net.i2cat.mantychore.network.repository.NetworkMapperModelToDescriptor;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;
import org.opennaas.core.resources.shell.GenericKarafCommand;

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
			IResourceManager manager = getResourceManager();
			String[] networkIdSplitted = splitResourceName(networkId);
			network = manager.getResource(manager.getIdentifierFromResourceName(networkIdSplitted[0], networkIdSplitted[1]));
		} catch (Exception e) {
			printError("Failed to get network resource: " + e.getLocalizedMessage());
			printEndCommand();
			return null;
		}

		NetworkModel networkModel = (NetworkModel) network.getModel();

		List<NetworkElement> resources = NetworkModelHelper.getNetworkElementsExceptTransportElements(networkModel);
		int pos = NetworkModelHelper.getNetworkElementByName(resourceId, resources);
		if (pos == -1) {
			printError("Could not find resource " + resourceId + " in network model");
			printEndCommand();
			return null;
		}

		NetworkElement toRemove = resources.get(pos);
		NetworkModelHelper.deleteNetworkElementAndReferences(toRemove, networkModel);
		networkModel.removeResourceRef(resourceId);
		NetworkTopology topology = NetworkMapperModelToDescriptor.modelToDescriptor(networkModel);
		network.getResourceDescriptor().setNetworkTopology(topology);

		printInfo("Resource " + resourceId + "removed from network " + networkId);
		printEndCommand();
		return null;
	}

}