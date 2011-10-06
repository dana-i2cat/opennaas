package net.i2cat.luminis.capability.connections.shell;

import net.i2cat.luminis.actionsets.wonesys.ActionConstants;
import net.i2cat.luminis.capability.connections.ConnectionsCapability;
import net.i2cat.mantychore.model.FCPort;
import net.i2cat.mantychore.model.opticalSwitch.DWDMChannel;
import net.i2cat.mantychore.model.opticalSwitch.FiberConnection;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "connections", name = "makeConnection", description = "Makes a connection between given ports af given resource, and configures which lambda enters and which gets out of the connection.")
public class MakeConnectionCommand extends GenericKarafCommand {

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

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("make connection between: (" + portSource + ",l=" + lambdaSource + "),(" + portTarget + ",l=" + lambdaTarget + ")");

		try {

			IResource resource = getResourceFromFriendlyName(resourceId);
			if (resource == null)
				return "";

			ICapability capability = getCapability(resource.getCapabilities(), ConnectionsCapability.CONNECTIONS);
			if (capability == null) {
				printError("Error getting the capability");
				printEndCommand();
				return "";
			}

			FiberConnection connectionRequest = buildConnectionRequest();

			Response response = (Response) capability.sendMessage(ActionConstants.MAKECONNECTION, connectionRequest);
			if (!response.getErrors().isEmpty()) {
				printError("Errors executing make connection:");
				for (String errorMsg : response.getErrors()) {
					printError(errorMsg);
				}
				printEndCommand();
				return "";
			}

		} catch (Exception e) {
			printError("Error in make connection");
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
		srcFiberChannel.setLambda(Double.parseDouble(lambdaSource));

		DWDMChannel dstFiberChannel = new DWDMChannel();
		dstFiberChannel.setLambda(Double.parseDouble(lambdaTarget));

		connection.setSrcCard(srcCard);
		connection.setDstCard(dstCard);

		connection.setSrcPort(srcPort);
		connection.setDstPort(dstPort);

		connection.setSrcFiberChannel(srcFiberChannel);
		connection.setDstFiberChannel(dstFiberChannel);

		return connection;

	}

}
