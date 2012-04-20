package org.opennaas.extensions.bod.capability.l2bod.shell;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.NoSuchElementException;

import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.NetworkElement;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.extensions.bod.actionsets.dummy.ActionConstants;
import org.opennaas.extensions.bod.capability.l2bod.L2BoDCapability;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "l2bod", name = "requestConnection", description = "Request L2 connectivity between specified interfaces.")
public class RequestConnectionCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id to request the connectivity.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "interface1", description = "The name of interface 1 to connect", required = true, multiValued = false)
	private String	interfaceName1;

	@Argument(index = 2, name = "interface2", description = "The name of interface 2 to connect", required = true, multiValued = false)
	private String	interfaceName2;

	@Override
	protected Object doExecute() throws Exception {
		Object result = null;

		printInitCommand("request connectivity of resource: " + resourceId + " and interfaces: " + interfaceName1 + " - " + interfaceName2);

		try {

			IResource resource = getResourceFromFriendlyName(resourceId);

			ICapability ipCapability = getCapability(resource.getCapabilities(), L2BoDCapability.CAPABILITY_NAME);

			result = ipCapability.sendMessage(ActionConstants.REQUESTCONNECTION, getInterfaces((NetworkModel) resource.getModel()));

		} catch (Exception e) {
			printError("Error requesting connectivity for resource: " + resourceId);
			printError(e);
			printEndCommand();
		}

		printEndCommand();

		return result;
	}

	/**
	 * Get the interfaces from the model
	 *
	 * @param networkModel
	 *
	 * @return list of interfaces
	 */
	private List<Interface> getInterfaces(NetworkModel model)
	{
		return Lists.newArrayList(getInterface(model, interfaceName1),
								  getInterface(model, interfaceName2));
	}

	private Interface getInterface(NetworkModel model, String name)
	{
		List<NetworkElement> elements = model.getNetworkElements();
		Interface i =
			NetworkModelHelper.getInterfaceByName(elements, name);
		if (i == null) {
			throw new NoSuchElementException("No such interface: " + name);
		}
		return i;
	}
}