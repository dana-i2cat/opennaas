package net.i2cat.luminis.actionsets.wonesys.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.i2cat.luminis.actionsets.wonesys.ActionConstants;
import net.i2cat.luminis.commandsets.wonesys.WonesysCommand;
import net.i2cat.luminis.commandsets.wonesys.commands.psroadm.SetChannel;
import net.i2cat.luminis.protocols.wonesys.WonesysProtocolSession;
import net.i2cat.mantychore.model.FCPort;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.opticalSwitch.DWDMChannel;
import net.i2cat.mantychore.model.opticalSwitch.FiberChannel;
import net.i2cat.mantychore.model.opticalSwitch.FiberConnection;
import net.i2cat.mantychore.model.opticalSwitch.WDMChannelPlan;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.WDMFCPort;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;

public class MakeConnectionAction extends Action {

	static Log	log	= LogFactory.getLog(MakeConnectionAction.class);

	public MakeConnectionAction() {
		super();
		initialize();

	}

	protected void initialize() {
		this.setActionID(ActionConstants.MAKECONNECTION);
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		try {
			/* get protocol */
			WonesysProtocolSession protocol = (WonesysProtocolSession) protocolSessionManager.obtainSessionByProtocol("wonesys", false);

			/* get params */
			FiberConnection connection = loadParams((FiberConnection) params, (ProteusOpticalSwitch) modelToUpdate);

			double srcLambda = connection.getSrcFiberChannel().getLambda();
			double dstLambda = connection.getDstFiberChannel().getLambda();

			// check there is no connection using given channel
			// return error if there is
			if (channelAlreadyInUse(connection, (ProteusOpticalSwitch) modelToUpdate))
				return ActionResponse.errorResponse("Could not make connection. Desired channel is already in use.");

			ActionResponse actionResponse;
			if (srcLambda == dstLambda) {
				actionResponse = makeConnectionWithSameLambda(connection, protocol);
			} else {
				actionResponse = makeConnectionWithDifferentLambda(connection, protocol);
			}

			if (actionResponse.getStatus().equals(STATUS.OK)) {
				((ProteusOpticalSwitch) modelToUpdate).getFiberConnections().add(connection);
			}

			return actionResponse;

		} catch (ProtocolException e) {
			throw new ActionException(e);
		}
	}

	private ActionResponse makeConnectionWithDifferentLambda(FiberConnection connection, WonesysProtocolSession protocol) {
		// TODO NOT SUPPORTED YET
		return ActionResponse.errorResponse("Could not make connection. Connections with different lambdas are not supported.");
	}

	private ActionResponse makeConnectionWithSameLambda(FiberConnection connection, WonesysProtocolSession protocol) throws ActionException {

		ActionResponse response = ActionResponse.okResponse(getActionID());

		FCPort srcPort = connection.getSrcPort();
		FCPort dstPort = connection.getDstPort();

		double lambda = connection.getSrcFiberChannel().getLambda();

		/* find paths */
		NetworkPort[] path = findInternPath(srcPort, dstPort);

		// foreach hop in path concerning a single card
		for (int i = 0; i < path.length - 1; i++) {
			if (path[i].getModule().equals(path[i + 1].getModule())) {
				Response partialResponse = makeConnectionInCard((FCPort) path[i], (FCPort) path[i + 1], lambda, protocol);
				response.addResponse(partialResponse);
			}
		}
		// foreach hop in path concerning different cards
		for (int i = 0; i < path.length - 1; i++) {
			if (!path[i].getModule().equals(path[i + 1].getModule())) {
				// create passthrough between subports
				FCPort subPort1 = getSubportWithLambda((FCPort) path[i], lambda);
				FCPort subPort2 = getSubportWithLambda((FCPort) path[i + 1], lambda);
				// subports have been created in previous "for" so they cannot be null
				subPort1.addDeviceConnection(subPort2);
			}
		}

		return response;
	}

	private Response makeConnectionInCard(FCPort srcPort, FCPort dstPort, double lambda, WonesysProtocolSession protocol)
			throws ActionException {

		try {
			ProteusOpticalSwitchCard card = (ProteusOpticalSwitchCard) srcPort.getModule();

			int channelNum = ((WDMChannelPlan) card.getChannelPlan()).getChannelNumberFromLambda(lambda);
			FiberChannel channel = ((WDMChannelPlan) card.getChannelPlan()).getChannel(channelNum);

			Response response = Response.okResponse("none");

			if (!card.isPassive()) {

				int chassis = card.getChasis();
				int slot = card.getModuleNumber();
				int portNum = dstPort.getPortNumber();

				// send command to the card
				WonesysCommand command = new SetChannel(chassis, slot, channelNum, portNum);
				command.initialize();
				response = command.checkResponse(protocol.sendReceive(command.message()));
				validateResponse(response);
			}
			// create subports and their connection in model
			makeConnectionInCardUpdateModel(card, srcPort, dstPort, channel, channel);
			// setChannelInModel(card, channelNum, portNum, (ProteusOpticalSwitch) modelToUpdate);

			return response;

		} catch (ProtocolException e) {
			throw new ActionException(e);
		} catch (CommandException e) {
			throw new ActionException(e);
		}
	}

	private void validateResponse(Response response) throws CommandException {

		if (response.getStatus().equals(Response.Status.ERROR)) {
			if (response.getErrors().size() > 0)
				throw new CommandException(response.getErrors().get(0));
			else
				throw new CommandException("Command Failed");
		}
	}

	/**
	 * Finds a route from srcPort to dstPort following connections inside the switch <br/>
	 * FIXME: NOTE: Right now it only tries paths with less than two hops of card.
	 * 
	 * @param srcPort
	 * @param dstPort
	 * @return
	 * @throws ActionException
	 */
	private NetworkPort[] findInternPath(NetworkPort srcPort, NetworkPort dstPort) throws ActionException {

		/* simple case */
		if (dstPort.getModule().equals(srcPort.getModule())) {
			ProteusOpticalSwitchCard srcCard = (ProteusOpticalSwitchCard) srcPort.getModule();
			if (srcCard.isInternallyConnected(srcPort, dstPort)) {
				return new NetworkPort[] { srcPort, dstPort };
			} else {
				throw new ActionException("Could not internally connect given ports");
			}
			/* we search a route */
		} else {
			for (NetworkPort p1 : srcPort.getModule().getModulePorts()) {
				ProteusOpticalSwitchCard srcCard = (ProteusOpticalSwitchCard) srcPort.getModule();
				/* srcPort is internally connected with the p1 */
				if (srcCard.isInternallyConnected(srcPort, p1)) {
					/* search ports connected to p1 */
					for (LogicalDevice p2 : p1.getOutgoingDeviceConnections()) {
						// TODO assure only local ports are checked
						/* it is a correct port definition */
						if (p2 instanceof NetworkPort) {
							ProteusOpticalSwitchCard dstCard = (ProteusOpticalSwitchCard) ((NetworkPort) p2).getModule();
							/* assure p2 is internally connected to dstPort */
							if (dstCard.isInternallyConnected((NetworkPort) p2, dstPort)) {
								return new NetworkPort[] { srcPort, p1, (NetworkPort) p2, dstPort };
							}
						}
					}
				}
			}
			throw new ActionException("Could not internally connect given ports");
		}
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (!(params instanceof FiberConnection)) {
			return false;
		}
		return true;
	}

	private FiberConnection loadParams(FiberConnection connectionRequest, ProteusOpticalSwitch opticalSwitch) throws ActionException {

		if (!checkParams(connectionRequest))
			throw new ActionException("The Action " + getActionID() + " doesn't accept given params");

		return loadFiberConnectionFromRequest(connectionRequest, opticalSwitch);
	}

	/**
	 * Uses given request to create a FiberConnection request with information from opticalSwitch model. Checks all params are correct.
	 * 
	 * @param connectionRequest
	 *            request to select which data must be loaded
	 * @param opticalSwitch
	 *            model to get data from
	 * @return
	 * @throws ActionException
	 *             if param checking fails
	 */
	public static FiberConnection loadFiberConnectionFromRequest(FiberConnection connectionRequest, ProteusOpticalSwitch opticalSwitch)
			throws ActionException {

		log.info("Loading request...");

		// connection src
		ProteusOpticalSwitchCard srcCard = opticalSwitch.getCard(connectionRequest.getSrcCard().getChasis(), connectionRequest.getSrcCard()
				.getSlot());
		if (srcCard == null)
			throw new ActionException(
					"There is no such card in model : Chassis: " + connectionRequest.getSrcCard().getChasis() + " Slot: " + connectionRequest
							.getSrcCard().getSlot());

		FCPort srcPort = (FCPort) srcCard.getPort(connectionRequest.getSrcPort().getPortNumber());
		if (srcPort == null)
			throw new ActionException(
					"There is no such port in model: Chassis: " + connectionRequest.getSrcCard().getChasis() + " Slot: " + connectionRequest
							.getSrcCard().getSlot() + " PortNumber : " + connectionRequest.getSrcPort().getPortNumber());

		DWDMChannel srcFiberChannel = (DWDMChannel) connectionRequest.getSrcFiberChannel();
		// it is equals in the src and in the destination channel
		double srcLambda = srcFiberChannel.getLambda();
		if (srcLambda == 0.0)
			throw new ActionException("Invalid lambda: " + srcLambda);

		// FIXME what if it's not a valid lambda????
		int srcChannelNum = ((WDMChannelPlan) srcCard.getChannelPlan()).getChannelNumberFromLambda(srcLambda);
		srcFiberChannel = (DWDMChannel) ((WDMChannelPlan) srcCard.getChannelPlan()).getChannel(srcChannelNum);

		// connection dst
		ProteusOpticalSwitchCard dstCard = opticalSwitch.getCard(connectionRequest.getDstCard().getChasis(), connectionRequest.getDstCard()
				.getSlot());
		if (dstCard == null)
			throw new ActionException(
					"There is no such card in model : Chassis: " + connectionRequest.getDstCard().getChasis() + " Slot: " + connectionRequest
							.getDstCard().getSlot());

		FCPort dstPort = (FCPort) dstCard.getPort(connectionRequest.getDstPort().getPortNumber());
		if (dstPort == null)
			throw new ActionException(
					"There is no such port in model: Chassis: " + connectionRequest.getDstCard().getChasis() + " Slot: " + connectionRequest
							.getDstCard().getSlot() + " PortNumber : " + connectionRequest.getDstPort().getPortNumber());

		DWDMChannel dstFiberChannel = (DWDMChannel) connectionRequest.getDstFiberChannel();
		double dstLambda = dstFiberChannel.getLambda();
		if (dstLambda == 0.0)
			throw new ActionException("Invalid lambda: " + dstLambda);

		// FIXME what if it's not a valid lambda????
		int dstChannelNum = ((WDMChannelPlan) dstCard.getChannelPlan()).getChannelNumberFromLambda(dstLambda);
		dstFiberChannel = (DWDMChannel) ((WDMChannelPlan) dstCard.getChannelPlan()).getChannel(dstChannelNum);

		FiberConnection connection = new FiberConnection();
		connection.setSrcCard(srcCard);
		connection.setDstCard(dstCard);
		connection.setSrcPort(srcPort);
		connection.setDstPort(dstPort);
		connection.setSrcFiberChannel(srcFiberChannel);
		connection.setDstFiberChannel(dstFiberChannel);

		log.info("Connection request loaded. \n" +
				"Connection source: " + connection.getSrcCard().getChasis() + "-" + connection.getSrcCard().getModuleNumber() + "-" + connection
						.getSrcPort().getPortNumber() + "-" + connection.getSrcFiberChannel().getNumChannel() + "\n" +
				"Connection destination: " + connection.getDstCard().getChasis() + "-" + connection.getDstCard().getModuleNumber() + "-" + connection
						.getDstPort().getPortNumber() + "-" + connection.getDstFiberChannel().getNumChannel());

		return connection;
	}

	/**********************
	 * CONTROLLER METHODS *
	 **********************/

	public static void makeConnectionInCardUpdateModel(ProteusOpticalSwitchCard card, FCPort srcPort, FCPort dstPort, FiberChannel srcChannel,
			FiberChannel dstChannel) {
		card.addSwitchingRule(srcChannel, srcPort, dstChannel, dstPort);
	}

	public static WDMFCPort getSubportWithLambda(FCPort parentPort, double lambda) {
		for (LogicalPort subport : parentPort.getPortsOnDevice()) {
			if (subport instanceof WDMFCPort) {
				if (((WDMFCPort) subport).getDWDMChannel().getLambda() == lambda) {
					return (WDMFCPort) subport;
				}
			}
		}
		return null;
	}

	public static boolean channelAlreadyInUse(FiberConnection connection, ProteusOpticalSwitch model) {

		boolean channelInUse = false;

		for (FiberConnection existentConnection : model.getFiberConnections()) {

			if (existentConnection.getSrcCard().getChasis() == connection.getSrcCard().getChasis() &&
					existentConnection.getSrcCard().getSlot() == connection.getSrcCard().getSlot()) {
				if (existentConnection.getSrcFiberChannel().getLambda() == connection.getSrcFiberChannel().getLambda()) {
					channelInUse = true;
					break;
				}
			}

			if (existentConnection.getDstCard().getChasis() == connection.getDstCard().getChasis() &&
					existentConnection.getDstCard().getSlot() == connection.getDstCard().getSlot()) {
				if (existentConnection.getDstFiberChannel().getLambda() == connection.getDstFiberChannel().getLambda()) {
					channelInUse = true;
					break;
				}
			}
		}

		return channelInUse;
	}

}
