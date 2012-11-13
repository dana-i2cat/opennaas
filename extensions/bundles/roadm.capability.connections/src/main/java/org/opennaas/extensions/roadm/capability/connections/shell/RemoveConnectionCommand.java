package org.opennaas.extensions.roadm.capability.connections.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.roadm.capability.connections.ConnectionsCapability;
import org.opennaas.extensions.roadm.capability.connections.IConnectionsCapability;
import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;

@Command(scope = "connections", name = "removeConnection", description = "Removes a connection between given ports of given resource")
public class RemoveConnectionCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id to make connection.", required = true, multiValued = false)
	private String	resourceId;
	@Argument(index = 1, name = "sourceport", description = "Connection source port (input)", required = true, multiValued = false)
	private String	portSource;
	@Argument(index = 2, name = "inputlambda", description = "Input lambda (wavelength in nm)", required = true, multiValued = false)
	private String	lambdaSource;

	@Argument(index = 3, name = "porttarget", description = "Connection target port (output)", required = true, multiValued = false)
	private String	portTarget;

	@Argument(index = 4, name = "outputlambda", description = "Output lambda (wavelength in nm)", required = true, multiValued = false)
	private String	lambdaTarget;

	@Option(name = "--useChannelNumbers", aliases = { "-n" }, description = "Tells command to read inputLambda and outputLambda as integers representing the channelNumber, instead of their original meaning")
	private boolean	useChannelNum	= false;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("remove connection between: (" + portSource + ",l=" + lambdaSource + "),(" + portTarget + ",l=" + lambdaTarget + ")");

		try {

			IResource resource = getResourceFromFriendlyName(resourceId);
			if (resource == null)
				return "";

			IConnectionsCapability capability = (IConnectionsCapability) getCapability(resource.getCapabilities(), ConnectionsCapability.CONNECTIONS);
			if (capability == null) {
				printError("Error getting the capability");
				printEndCommand();
				return "";
			}

			FiberConnection connectionRequest = buildConnectionRequest();

			IConnectionsCapability connectionsCapability = (IConnectionsCapability) resource.getCapabilityByInterface(IConnectionsCapability.class);
			connectionsCapability.removeConnection(connectionRequest);

		} catch (Exception e) {
			printError("Error in remove connection");
			printError(e);
			printEndCommand();
			return "";
		}
		printEndCommand();
		return null;
	}

	private FiberConnection buildConnectionRequest() {

		FiberConnection connection = new FiberConnection();

		String[] srcPortId = portSource.split("-");
		String[] dstPortId = portTarget.split("-");

		ProteusOpticalSwitchCard srcCard = new ProteusOpticalSwitchCard();
		srcCard.setChasis(Integer.parseInt(srcPortId[0]));
		srcCard.setModuleNumber(Integer.parseInt(srcPortId[1]));

		ProteusOpticalSwitchCard dstCard = new ProteusOpticalSwitchCard();
		dstCard.setChasis(Integer.parseInt(dstPortId[0]));
		dstCard.setModuleNumber(Integer.parseInt(dstPortId[1]));

		FCPort srcPort = new FCPort();
		srcPort.setPortNumber(Integer.parseInt(srcPortId[2]));

		FCPort dstPort = new FCPort();
		dstPort.setPortNumber(Integer.parseInt(dstPortId[2]));

		DWDMChannel srcFiberChannel = new DWDMChannel();
		DWDMChannel dstFiberChannel = new DWDMChannel();

		if (!useChannelNum) {
			srcFiberChannel.setLambda(Double.parseDouble(lambdaSource));
			dstFiberChannel.setLambda(Double.parseDouble(lambdaTarget));
		} else {
			srcFiberChannel.setNumChannel(Integer.parseInt(lambdaSource));
			dstFiberChannel.setNumChannel(Integer.parseInt(lambdaTarget));
		}

		connection.setSrcCard(srcCard);
		connection.setDstCard(dstCard);

		connection.setSrcPort(srcPort);
		connection.setDstPort(dstPort);

		connection.setSrcFiberChannel(srcFiberChannel);
		connection.setDstFiberChannel(dstFiberChannel);

		return connection;

	}

}
