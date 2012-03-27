package org.opennaas.extensions.roadm.capability.connections.shell;

import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "connections", name = "listConnections", description = "Shows given resource connections.")
public class ListConnectionsCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id to show connections.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("list connections of resource :" + resourceId);

		try {

			IResource resource = getResourceFromFriendlyName(resourceId);
			if (resource == null)
				return "";

			printConnections(resource);

		} catch (Exception e) {
			printError("Error listing connections for resource " + resourceId);
			printError(e);
			printEndCommand();
			return "";
		}
		printEndCommand();
		return null;

	}

	private void printConnections(IResource resource) {
		ProteusOpticalSwitch model = (ProteusOpticalSwitch) resource.getModel();

		printInfo("Connections of Proteus Optical Switch " + model.getName() + ":");

		for (FiberConnection connection : model.getFiberConnections()) {

			String srcCardType = connection.getSrcCard().getCardType().toString();
			String srcPortId = connection.getSrcCard().getChasis() + "-" + connection.getSrcCard().getModuleNumber() + "-" + connection.getSrcPort()
					.getPortNumber();

			String dstCardType = connection.getDstCard().getCardType().toString();
			String dstPortId = connection.getDstCard().getChasis() + "-" + connection.getDstCard().getModuleNumber() + "-" + connection.getDstPort()
					.getPortNumber();

			printInfo("Port " + srcPortId + " in " + srcCardType + " card using channel " + connection.getSrcFiberChannel().getNumChannel() + " --> " +
					"Port " + dstPortId + " in " + dstCardType + " card using channel " + connection.getDstFiberChannel().getNumChannel());

		}

		printInfo("Total connections: " + model.getFiberConnections().size());
	}

}