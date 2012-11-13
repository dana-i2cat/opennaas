package org.opennaas.extensions.bod.capability.l2bod.shell;

import java.util.NoSuchElementException;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.bod.capability.l2bod.BoDLink;
import org.opennaas.extensions.bod.capability.l2bod.IL2BoDCapability;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;

@Command(scope = "l2bod", name = "shutdownConnection", description = "Shutdown L2 connectivity between specified interfaces.")
public class ShutdownConnectionCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id to shutdown the connectivity.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "connectionName", description = "The name of the link to be connection", required = true, multiValued = false)
	private String	connectionName;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("shutdown connectivity of resource: " + resourceId + " and link: " + connectionName);

		try {
			IResource resource = getResourceFromFriendlyName(resourceId);
			IL2BoDCapability ipCapability = (IL2BoDCapability) resource.getCapabilityByInterface(IL2BoDCapability.class);
			ipCapability.shutDownConnection(getLink((NetworkModel) resource.getModel()));
		} catch (Exception e) {
			printError("Error shutting down connection in resource: " + resourceId);
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;

	}

	/**
	 * Get the link from the model. Uses connectionName argument to find it.
	 * 
	 * @param model
	 * @return Link representing the connection with given name in given model.
	 * @throws NoSuchElementException
	 *             if there is no Link in model named <code>connectionName</code>.
	 * 
	 */
	private BoDLink getLink(NetworkModel model) {
		int linkIndex = NetworkModelHelper.getNetworkElementByName(connectionName, model.getNetworkElements());
		if (linkIndex == -1) {
			throw new NoSuchElementException("No such link " + connectionName);
		}

		if (!(model.getNetworkElements().get(linkIndex) instanceof BoDLink)) {
			throw new NoSuchElementException("No such link " + connectionName);
		}

		return (BoDLink) model.getNetworkElements().get(linkIndex);
	}
}