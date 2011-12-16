package net.i2cat.mantychore.network.capability.basic.shell;

import net.i2cat.mantychore.model.ManagedElement;
import net.i2cat.mantychore.model.mappers.Cim2NdlMapper;
import net.i2cat.mantychore.network.model.NetworkModel;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "net", name = "addResource", description = "Add a resource to the network")
public class AddResourceToNetworkCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "network:networkName", description = "The network where to add a resource", required = true, multiValued = false)
	private String	networkId;

	@Argument(index = 1, name = "resourceType:resourceName", description = "The resource to be added", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		// load resources
		IResource network;
		IResource resource;
		try {

			IResourceManager manager = getResourceManager();

			String[] networkIdSplitted = splitResourceName(networkId);
			String[] resourceIdSplitted = splitResourceName(resourceId);

			resource = manager.getResource(manager.getIdentifierFromResourceName(resourceIdSplitted[0], resourceIdSplitted[1]));
			network = manager.getResource(manager.getIdentifierFromResourceName(networkIdSplitted[0], networkIdSplitted[1]));

		} catch (Exception e) {
			printError("Failed to get required resources: " + e.getLocalizedMessage());
			printEndCommand();
			return null;
		}

		// transform resource model to NDL
		if (!resource.getState().equals(State.ACTIVE)) {
			printError("Resource should be started before adding it to a network.");
		}

		IModel resourceModel = resource.getModel();
		NetworkModel networkModel = (NetworkModel) network.getModel();

		// FIXME should use a generic getIModel2NdlWrapper method placed in IModel (NetworkModel should be moved to OpenNaaS for that)
		// IModel2NdlWrapper wrapper = resourceModel.getIModel2NdlWrapper();
		// wrapper.addModelToNetworkModel(resource.getModel(), networkModel);
		if (resourceModel instanceof ManagedElement) {
			Cim2NdlMapper.addModelToNetworkModel(resource.getModel(), networkModel);
		}
		printEndCommand();
		return null;
	}

}
