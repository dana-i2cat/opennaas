package org.opennaas.extensions.roadm.wonesys.actionsets.actions;

import org.opennaas.extensions.roadm.wonesys.actionsets.ActionConstants;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.psroadm.SetChannel;
import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;
import org.opennaas.extensions.router.model.opticalSwitch.WDMChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.WDMFCPort;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
			IProtocolSession protocol = protocolSessionManager.obtainSessionByProtocol("wonesys", false);

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

	private ActionResponse makeConnectionWithDifferentLambda(FiberConnection connection, IProtocolSession protocol) {
		// TODO NOT SUPPORTED YET
		return ActionResponse.errorResponse("Could not make connection. Connections with different lambdas are not supported.");
	}

	private ActionResponse makeConnectionWithSameLambda(FiberConnection connection, IProtocolSession protocol) throws ActionException {

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

	private Response makeConnectionInCard(FCPort srcPort, FCPort dstPort, double lambda, IProtocolSession protocol)
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

		DWDMChannel srcFiberChannel = loadCompleteDWDMChannel((DWDMChannel) connectionRequest.getSrcFiberChannel(), srcCard);


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

		DWDMChannel dstFiberChannel = loadCompleteDWDMChannel((DWDMChannel) connectionRequest.getDstFiberChannel(), dstCard);

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

	/**
	 * Loads existent DWDMChannel from request.
	 * Tries to load it from request lambda. If lambda is not set, loads it from request channelNum.
	 *
	 * @param request
	 * @param card
	 * @return
	 * @throws ActionException
	 */
	private static DWDMChannel loadCompleteDWDMChannel(DWDMChannel request, ProteusOpticalSwitchCard card) throws ActionException {

		int channelNum = request.getNumChannel();

		if (request.getLambda() != 0.0) {
			//load from lambda

			// FIXME what if it's not a valid lambda????
			channelNum = ((WDMChannelPlan) card.getChannelPlan()).getChannelNumberFromLambda(request.getLambda());
		}

		//check channelNum is a valid one
		int[] allChannelsNum = ((WDMChannelPlan) card.getChannelPlan()).getAllChannelsNum();
		boolean found = false;
		int position = -1;
		for (int i = 0; i< allChannelsNum.length; i++){
			if (allChannelsNum[i] == channelNum){
				position = i;
				found = true;
				break;
			} else {
				if (allChannelsNum[i] > channelNum){
					position = i;
					//allChannelsNum is an ordered list (allChannelsNum[i] < allChannelsNum[i+1])
					break;
				}
			}
		}

		if (!found){
			log.debug("Could not find specified channel. Looking for channel " + channelNum + ". Found " + allChannelsNum[position] + " and gap is " +  ((WDMChannelPlan) card.getChannelPlan()).getChannelGap());
			throw new ActionException("Invalid connectionRequest: Could not find specified channel");
		}

		DWDMChannel completeChannel = (DWDMChannel) ((WDMChannelPlan) card.getChannelPlan()).getChannel(channelNum);

		if (completeChannel == null) {
			throw new ActionException("Invalid connectionRequest: Could not find specified channel");
		}

		return completeChannel;
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
