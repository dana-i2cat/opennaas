package net.i2cat.luminis.capability.connections.shell;

import java.util.List;

import net.i2cat.luminis.actionsets.wonesys.ActionConstants;
import net.i2cat.luminis.capability.connections.Activator;
import net.i2cat.luminis.capability.connections.ConnectionsCapability;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.opticalSwitch.FiberConnection;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.WDMFCPort;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.core.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

@Command(scope = "connections", name = "getInventory", description = "Shows given resource cards and connections information.")
public class GetInventoryCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id to get inventory.", required = true, multiValued = false)
	private String	resourceId;

	@Option(name = "--refresh", aliases = { "-r" }, description = "Refresh model prior to show information")
	boolean			shouldRefresh;

	@Override
	protected Object doExecute() throws Exception {
		initcommand("get inventory of resource :" + resourceId);

		try {

			IResource resource = getResourceFromFriendlyName(resourceId);
			if (resource == null)
				return "";

			if (shouldRefresh) {
				printInfo("Refreshing model...");

				ICapability capability = getCapability(resource.getCapabilities(), ConnectionsCapability.CONNECTIONS);
				if (capability == null) {
					printError("Error getting the capability");
					endcommand();
					return "";
				}
				Response response = (Response) capability.sendMessage(ActionConstants.REFRESHCONNECTIONS, null);
				if (!response.getErrors().isEmpty()) {
					printError("Errors executing refresh:");
					for (String errorMsg : response.getErrors()) {
						printError(errorMsg);
					}
					endcommand();
					return "";
				}

				// FIXME: should execute refresh but not all queue!!!
				QueueResponse queueResponse = executeQueue(resource);
				if (!queueResponse.isOk()) {
					printError("Errors executing queue!");
					endcommand();
					return "";
				}

				printInfo("Refresh done.");
			}

			printInventory(resource);

		} catch (Exception e) {
			printError("Error refresing!");
			printError(e);
			endcommand();
			return "";
		}
		endcommand();
		return null;
	}

	private QueueResponse executeQueue(IResource resource) throws Exception {

		printInfo("Executing queue...");
		IQueueManagerService queue = Activator.getQueueManagerService(resource.getResourceIdentifier().getId());
		printInfo("Sending the message...");
		QueueResponse response = null;
		try {
			response = queue.execute();
		} catch (CapabilityException e) {
			queue.empty();
			throw e;
		}
		return response;
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
