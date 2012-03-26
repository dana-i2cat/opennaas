package org.opennaas.extensions.roadm.capability.connections.shell;

import java.util.List;

import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberChannel;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.WDMFCPort;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "connections", name = "getChannels", description = "Shows channels of given port.")
public class GetChannelsCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id where given port is.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "port", description = "Port to get channels from", required = true, multiValued = false)
	private String	portId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("get channels of port : " + portId + " in resource: " + resourceId);

		try {

			IResource resource = getResourceFromFriendlyName(resourceId);
			if (resource == null)
				return "";

			printChannels(resource);

		} catch (Exception e) {
			printError("Error getting resource " + resourceId);
			printError(e);
			printEndCommand();
			return "";
		}
		printEndCommand();
		return null;
	}

	private void printChannels(IResource resource) {

		ProteusOpticalSwitch model = (ProteusOpticalSwitch) resource.getModel();

		String[] portIdParts = portId.split("-");

		ProteusOpticalSwitchCard card = model.getCard(Integer.parseInt(portIdParts[0]), Integer.parseInt(portIdParts[1]));

		NetworkPort port = card.getPort(Integer.parseInt(portIdParts[2]));
		if (port instanceof FCPort) {

			List<FiberChannel> allChannels = card.getChannelPlan().getAllChannels();

			printInfo("Port " + portId + " supports " + allChannels.size() + " channels");

			String[][] channelsInfo = new String[allChannels.size()][3];

			for (int i = 0; i < allChannels.size(); i++) {

				FiberChannel channel = allChannels.get(i);
				boolean inUse = false;

				// look for current channel in port
				for (int j = 0; j < ((FCPort) port).getPortsOnDevice().size() && !inUse; j++) {

					LogicalPort subPort = ((FCPort) port).getPortsOnDevice().get(j);
					if (subPort instanceof WDMFCPort) {
						DWDMChannel subPortChannel = ((WDMFCPort) subPort).getDWDMChannel();
						if (channel.getNumChannel() == subPortChannel.getNumChannel()) {
							inUse = true;
						}
					}
				}

				// set channel info
				String lambda = "-";
				if (channel instanceof DWDMChannel) {
					lambda = Double.toString(((DWDMChannel) channel).getLambda());
				}
				String inUseS = "-";
				if (inUse) {
					inUseS = "X";
				}

				channelsInfo[i][0] = Integer.toString(channel.getNumChannel());
				channelsInfo[i][1] = lambda;
				channelsInfo[i][2] = inUseS;
			}

			String[] titles = new String[] { "ChannelNumber", "Lambda", "InUse" };
			printTable(titles, channelsInfo, -1);

		} else {
			printInfo("Port does not support channels");
		}
	}

}
