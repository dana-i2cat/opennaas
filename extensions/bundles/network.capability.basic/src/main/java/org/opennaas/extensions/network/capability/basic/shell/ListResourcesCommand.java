package org.opennaas.extensions.network.capability.basic.shell;

import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.NetworkElement;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "net", name = "listResources", description = "List resource of the network")
public class ListResourcesCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The network where to add a resource", required = true, multiValued = false)
	private String	networkId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("list resources in network");

		// load network
		IResource network;
		try {
			network = getResourceFromFriendlyName(networkId);
		} catch (Exception e) {
			printError("Failed to get required resources: " + e.getLocalizedMessage());
			printEndCommand();
			return null;
		}

		// get and print network elements
		NetworkModel networkModel = (NetworkModel) network.getModel();
		for (NetworkElement netElem : NetworkModelHelper.getNetworkElementsExceptTransportElements(networkModel)) {
			printNetworkElement(netElem);
		}

		printEndCommand();
		return null;
	}

	private void printNetworkElement(NetworkElement netElem) {
		printSymbol(netElem.getName() + "\n");
	}
}
