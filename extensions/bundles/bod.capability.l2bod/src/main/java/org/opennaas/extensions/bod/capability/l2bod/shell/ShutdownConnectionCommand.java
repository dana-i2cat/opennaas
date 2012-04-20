package org.opennaas.extensions.bod.capability.l2bod.shell;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.bod.actionsets.dummy.ActionConstants;
import org.opennaas.extensions.bod.capability.l2bod.L2BoDCapability;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.Interface;

@Command(scope = "l2bod", name = "shutdownConnection", description = "Shutdown L2 connectivity between specified interfaces.")
public class ShutdownConnectionCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id to shutdown the connectivity.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "interface1", description = "The name of interface 1 connected", required = true, multiValued = false)
	private String	interfaceName1;

	@Argument(index = 2, name = "interface2", description = "The name of interface 2 connected", required = true, multiValued = false)
	private String	interfaceName2;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("shutdown connectivity of resource: " + resourceId + " and interfaces: " + interfaceName1 + " - " + interfaceName2);

		try {

			IResource resource = getResourceFromFriendlyName(resourceId);

			ICapability ipCapability = resource.getCapabilityByType(L2BoDCapability.CAPABILITY_NAME);

			ipCapability.sendMessage(ActionConstants.SHUTDOWNCONNECTION, getInterfaces((NetworkModel) resource.getModel()));

		} catch (Exception e) {
			printError("Error requesting connectivity for resource: " + resourceId);
			printError(e);
			printEndCommand();
			return "";
		}
		printEndCommand();
		return null;

	}

	/**
	 * Get the interfaces from the model
	 * 
	 * @param networkModel
	 * 
	 * @return list of interfaces
	 */
	private List<Interface> getInterfaces(NetworkModel networkModel) {

		List<Interface> listInterfaces = new ArrayList<Interface>();

		// Add Interface 1
		Interface interface1 = NetworkModelHelper.getInterfaceByName(networkModel.getNetworkElements(), interfaceName1);
		listInterfaces.add(interface1);

		// Add Interface 2
		Interface interface2 = NetworkModelHelper.getInterfaceByName(networkModel.getNetworkElements(), interfaceName2);
		listInterfaces.add(interface2);

		return listInterfaces;
	}

}