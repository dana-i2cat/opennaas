package org.opennaas.extensions.roadm.wonesys.actionsets.actions;

import java.io.IOException;
import java.util.List;

import org.opennaas.extensions.roadm.wonesys.actionsets.ActionConstants;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysResponse;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.GetInventoryCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.psroadm.GetChannelPlan;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.psroadm.GetChannels;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.psroadm.SetChannel;
import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.opticalSwitch.FiberChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;
import org.opennaas.extensions.router.model.opticalSwitch.ITUGrid;
import org.opennaas.extensions.router.model.opticalSwitch.WDMChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.WDMFCPort;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard.CardType;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.WonesysDropCard;
import org.opennaas.extensions.router.model.utils.OpticalSwitchCardFactory;
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

public class RefreshModelConnectionsAction extends WonesysAction {

	Log	log	= LogFactory.getLog(RefreshModelConnectionsAction.class);

	int	chassisNum;
	int	slotNum;

	public RefreshModelConnectionsAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.REFRESHCONNECTIONS);
	}

	@Override
	public boolean checkParams(Object arg0) throws ActionException {
		log.warn("Given params are ignored");
		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		ActionResponse actionResponse = new ActionResponse();

		actionResponse.setActionID(actionID);
		try {
			/* get protocol */
			IProtocolSession protocol = protocolSessionManager.obtainSessionByProtocol("wonesys", false);

			// getInventory
			WonesysCommand c = new GetInventoryCommand();
			c.initialize();
			String response = (String) protocol.sendReceive(c.message());
			Response resp = checkResponse(c.message(), response);
			actionResponse.addResponse(resp);
			parseResponseGetInventoryCommand(resp, modelToUpdate);

			if (modelToUpdate instanceof ProteusOpticalSwitch) {

				ProteusOpticalSwitch opticalSwitch1 = (ProteusOpticalSwitch) modelToUpdate;
				// obtain ROADM drop card
				WonesysDropCard dropCard = null;
				for (LogicalDevice dev : opticalSwitch1.getLogicalDevices()) {
					if (dev instanceof ProteusOpticalSwitchCard) {
						ProteusOpticalSwitchCard card = (ProteusOpticalSwitchCard) dev;

						if (card.getCardType().equals(CardType.ROADM_DROP)) {
							dropCard = (WonesysDropCard) card;

							chassisNum = dropCard.getChasis();
							slotNum = dropCard.getModuleNumber();

							// getChannelPlan
							c = new GetChannelPlan(chassisNum, slotNum);
							c.initialize();
							response = (String) protocol.sendReceive(c.message());
							resp = checkResponse(c.message(), response);
							actionResponse.addResponse(resp);
							parseResponseGetChannelPlan(resp, modelToUpdate);

							// getChannels
							c = new GetChannels(chassisNum, slotNum);
							c.initialize();
							response = (String) protocol.sendReceive(c.message());
							resp = checkResponse(c.message(), response);
							actionResponse.addResponse(resp);
							parseResponseGetChannelsCommand(resp, modelToUpdate);

							// updateFiberConnections
							FCPort srcPort = dropCard.getCommonPort();
							List<LogicalPort> srcSubPorts = srcPort.getPortsOnDevice();
							for (LogicalPort srcSubPort : srcSubPorts) {
								if (srcSubPort instanceof FCPort) {
									// find dstSubPort
									FCPort dstSubPort = null;
									FCPort subPort = (FCPort) srcSubPort.getOutgoingDeviceConnections().get(0);
									while (!subPort.getOutgoingDeviceConnections().isEmpty()) {
										subPort = (FCPort) subPort.getOutgoingDeviceConnections().get(0);
									}
									dstSubPort = subPort;

									// create FiberConnection
									FiberConnection connection = new FiberConnection();
									connection.setSrcCard(dropCard);
									connection.setSrcPort(srcPort);
									connection.setSrcFiberChannel(((WDMFCPort) srcSubPort).getDWDMChannel());
									FCPort dstPort = (FCPort) dstSubPort.getDevices().get(0);
									connection.setDstCard((ProteusOpticalSwitchCard) dstPort.getModule());
									connection.setDstPort(dstPort);
									connection.setDstFiberChannel(((WDMFCPort) dstSubPort).getDWDMChannel());

									opticalSwitch1.getFiberConnections().add(connection);
								}
							}

						}
					}
				}
				actionResponse.setStatus(STATUS.OK);
			} else {
				throw new ActionException("Refresh model Action: error in model");
			}
		} catch (ProtocolException e) {
			throw new ActionException(e);
		}
		return actionResponse;
	}

	// TODO copy pasted from the command check all is ok

	public void parseResponseGetInventoryCommand(Object response, Object model) throws CommandException {

		WonesysResponse commandResponse = (WonesysResponse) response;

		if (commandResponse.getStatus().equals(Response.Status.ERROR)) {
			if (commandResponse.getErrors().size() > 0)
				throw new CommandException(commandResponse.getErrors().get(0));
			else
				throw new CommandException("Command Failed");
		}

		//remove old model
		List<LogicalDevice> oldLogicalDevices = ((ProteusOpticalSwitch) model).getLogicalDevices();
		for (int i= oldLogicalDevices.size() -1; i>=0; i--){
			if (oldLogicalDevices.get(i) instanceof ProteusOpticalSwitchCard){
				((ProteusOpticalSwitch) model).removeLogicalDevice(oldLogicalDevices.get(i));
			}
		}
		((ProteusOpticalSwitch) model).getFiberConnections().clear();

		OpticalSwitchCardFactory factory;
		try {
			factory = new OpticalSwitchCardFactory();
		} catch (IOException e) {
			throw new CommandException("Failed to load received data into model. Error loading card profiles file:", e);
		}

		String responseData = commandResponse.getInformation();

		// ID (2B) tipo (1B) subtipo (1B) = 4B per result, 2chars x Byte
		int resultLength = 4 * 2;
		int resultsCount = responseData.length() / resultLength;
		String[] results = new String[resultsCount];

		for (int i = 0; i < resultsCount; i++) {
			results[i] = responseData.substring(i * resultLength, (i + 1) * resultLength);

			String schasis = results[i].substring(0, 2);
			String sslot = results[i].substring(2, 4);
			String stype = results[i].substring(4, 6);
			String ssubtype = results[i].substring(6, 8);

			int chasis = Integer.parseInt(schasis, 16);
			int slot = Integer.parseInt(sslot, 16);
			int type = Integer.parseInt(stype, 16);
			int subtype = Integer.parseInt(ssubtype, 16);

			log.info("Chasis:" + chasis + " Slot:" + slot + " Type:" + type + " SubType:" + subtype);

			ProteusOpticalSwitchCard card = factory.createCard(chasis, slot, type, subtype);
			((ProteusOpticalSwitch) model).addLogicalDevice(card);
		}

		factory.addHardcodedCardConnections((ProteusOpticalSwitch) model);

	}

	public void parseResponseGetChannelsCommand(Object response, Object model) throws CommandException {

		if (!(model instanceof ProteusOpticalSwitch)) {
			throw new IllegalArgumentException("Given model is not a ProteusOpticalSwitchCard. It is of type: " + model.getClass());
		}

		WonesysResponse commandResponse = (WonesysResponse) response;

		if (commandResponse.getStatus().equals(Response.Status.ERROR)) {
			if (commandResponse.getErrors().size() > 0)
				throw new CommandException(commandResponse.getErrors().get(0));
			else
				throw new CommandException("Command Failed");
		}

		ProteusOpticalSwitchCard relatedCard = ((ProteusOpticalSwitch) model).getCard(chassisNum, slotNum);
		FiberChannelPlan channelPlan = relatedCard.getChannelPlan();

		String responseData = commandResponse.getInformation();

		int channelCount = responseData.length() / 2;
		log.info("Port supports " + channelCount + " channels");
		for (int i = 0; i < channelCount; i++) {
			String sport = responseData.substring(i * 2, (i * 2) + 2);
			int port = Integer.parseInt(sport, 16);

			int dwdmChannel = channelPlan.getFirstChannel() + (channelPlan.getChannelGap() * i);

			if (port != 0) {
				log.info("Channel " + dwdmChannel + " mapped to port " + port);
				if (relatedCard.getPort(port) == null) {
					log.error("Mapped port is not in model. Skipping this mapping although channel is in use");
					continue;
				}
			} else {
				// if (port == 0) --> channel is not associated with any port
				// log.trace("Channel " + dwdmChannel + " not in use");
			}
			SetChannel.setChannelInModel(relatedCard, dwdmChannel, port, (ProteusOpticalSwitch) model);
		}
	}

	public void parseResponseGetChannelPlan(Object response, Object model) throws CommandException {

		if (!(model instanceof ProteusOpticalSwitch)) {
			throw new IllegalArgumentException("Given model is not a ProteusOpticalSwitchCard. It is of type: " + model.getClass());
		}

		WonesysResponse commandResponse = (WonesysResponse) response;

		if (commandResponse.getStatus().equals(Response.Status.ERROR)) {
			if (commandResponse.getErrors().size() > 0)
				throw new CommandException(commandResponse.getErrors().get(0));
			else
				throw new CommandException("Command Failed");
		}

		String responseData = commandResponse.getInformation();

		// * Canal DWDM: 0-824 (Ver ITU_Grid_Planning.ods)
		// ** Espaciado: 0x00: 100 GHz, 0x01: 50 GHz, 0x02: 25 GHz, 0x03: 12,5 GHz
		String sfirstChannel = responseData.substring(0, 4);
		sfirstChannel = convertLittleBigEndian(sfirstChannel);
		String slastChannel = responseData.substring(4, 8);
		slastChannel = convertLittleBigEndian(slastChannel);
		String schannelGap = responseData.substring(8, 10);

		int firstChannel = Integer.parseInt(sfirstChannel, 16);
		int lastChannel = Integer.parseInt(slastChannel, 16);
		double channelGap = Math.pow(2, Integer.parseInt(schannelGap, 16));
		double guardInterval = (100 / (channelGap * 1000)); // THz

		ITUGrid ituGrid = new ITUGrid();
		double cardMaxFreq = ituGrid.getFrequencyFromChannelNum(firstChannel);
		double cardMinFreq = ituGrid.getFrequencyFromChannelNum(lastChannel);

		WDMChannelPlan cardChannelPlan = new WDMChannelPlan(cardMaxFreq, cardMinFreq, guardInterval, ituGrid);

		ProteusOpticalSwitchCard card = ((ProteusOpticalSwitch) model).getCard(chassisNum, slotNum);

		card.setChannelPlan(cardChannelPlan);

		log.info("ChannelPlan loaded: Card supports " + cardChannelPlan.getAllChannels().size() + " channels\n" +
				"First channel=" + cardChannelPlan.getFirstChannel() + " LastChannel=" + cardChannelPlan.getLastChannel() + " ChannelGap=" + cardChannelPlan
						.getChannelGap());
	}
}
