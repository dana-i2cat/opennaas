package net.i2cat.mantychore.network.capability.basic.shell;

import net.i2cat.mantychore.network.model.NetworkModel;
import net.i2cat.mantychore.network.model.NetworkModelHelper;
import net.i2cat.mantychore.network.model.topology.NetworkElement;

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

		// load network
		IResource network;
		try {
			IResourceManager manager = getResourceManager();
			String[] networkIdSplitted = splitResourceName(networkId);
			network = manager.getResource(manager.getIdentifierFromResourceName(networkIdSplitted[0], networkIdSplitted[1]));
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
