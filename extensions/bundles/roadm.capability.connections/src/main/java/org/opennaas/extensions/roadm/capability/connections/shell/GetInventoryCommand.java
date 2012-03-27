package org.opennaas.extensions.roadm.capability.connections.shell;

import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "connections", name = "getInventory", description = "Shows given resource cards and connections information.")
public class GetInventoryCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id to get inventory.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("get inventory of resource :" + resourceId);

		try {

			IResource resource = getResourceFromFriendlyName(resourceId);
			if (resource == null)
				return "";

			printInventory(resource);

		} catch (Exception e) {
			printError("Error getting inventory for resource " + resourceId);
			printError(e);
			printEndCommand();
			return "";
		}
		printEndCommand();
		return null;
	}

	private void printInventory(IResource resource) {

		ProteusOpticalSwitch model = (ProteusOpticalSwitch) resource.getModel();

		printInfo("Proteus Optical Switch " + model.getName() + ":");
		printInfo("Number of connections: " + model.getFiberConnections().size());
		printInfo("Number of cards: " + model.getLogicalDevices().size());
		for (LogicalDevice card : model.getLogicalDevices()) {
			if (card instanceof ProteusOpticalSwitchCard) {

				String cardType = ((ProteusOpticalSwitchCard) card).getCardType().toString();
				int chassis = ((ProteusOpticalSwitchCard) card).getChasis();
				int slot = ((ProteusOpticalSwitchCard) card).getModuleNumber();

				printInfo("\t" + cardType + " card in chassis " + chassis + " and slot " + slot);

				// ports
				printInfo("\t\tNumber of ports: " + ((ProteusOpticalSwitchCard) card).getModulePorts().size());
				for (NetworkPort port : ((ProteusOpticalSwitchCard) card).getModulePorts()) {
					printInfo("\t\tPort " + port.getPortNumber() + " used in " + port.getPortsOnDevice().size() + " connections.");
				}
			}
		}

	}

}
