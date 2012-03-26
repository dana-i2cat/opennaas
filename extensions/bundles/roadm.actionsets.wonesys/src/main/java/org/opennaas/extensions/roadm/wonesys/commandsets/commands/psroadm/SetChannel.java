package org.opennaas.extensions.roadm.wonesys.commandsets.commands.psroadm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysResponse;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.WDMChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.WonesysDropCard;

public class SetChannel extends WonesysCommand {

	private static final String	COMMAND_ID	= "0b02";
	private static final String	DATA_LENGTH	= "0300";

	private static Log			log			= LogFactory.getLog(SetChannel.class);

	private int					chassis;
	private int					slot;
	private int					channelNum;
	private int					portNum;

	private String				chassisHexStr;
	private String				slotHexStr;
	private String				channelHexStr;										// 2B
	private String				portHexStr;

	public SetChannel(int chassis, int slot, int channelNum, int portNum) {
		this.chassis = chassis;
		this.slot = slot;
		this.channelNum = channelNum;
		this.portNum = portNum;

		log.debug("Creating SetChannel command: Chassis=" + chassis + " Slot=" + slot + " ChannelNum=" + channelNum + " PortNum=" + portNum);

		chassisHexStr = toByteHexString(chassis, 1);
		slotHexStr = toByteHexString(slot, 1);
		channelHexStr = toByteHexString(channelNum, 2);
		portHexStr = toByteHexString(portNum, 1);
	}

	@Override
	public void parseResponse(Object response, Object model) throws CommandException {

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

		if (!responseData.equals(WonesysCommand.SET_OK_STATUS)) {
			throw new CommandException();
		}

		ProteusOpticalSwitchCard card = ((ProteusOpticalSwitch) model).getCard(chassis, slot);

		if (portNum != 0) {
			NetworkPort port = card.getPort(portNum);
			if (port == null) {
				throw new CommandException("Port " + portNum + " is not in model.");
			}
		}

		SetChannel.setChannelInModel(card, channelNum, portNum, (ProteusOpticalSwitch) model);
	}

	public static void setChannelInModel(ProteusOpticalSwitchCard card, int channelNum, int portNum, ProteusOpticalSwitch model) {
		if (card instanceof WonesysDropCard) {

			// get channel
			DWDMChannel channel = (DWDMChannel) ((WDMChannelPlan) card.getChannelPlan()).getChannel(channelNum);

			// remove channel from src port
			SetChannel.freeChannel(card, channel, ((WonesysDropCard) card).getCommonPort());

			// remove channel from dst port
			// unnecessary in WonesysDropCards as they have a single entry point.
			// if channel was allocated in dstPort previous call to freeChannel has released it.
			// SetChannel.freeChannel(card, (FCPort) card.getPort(portNum), channel);

			// SetChannel.removeFCPortsAndConnectionsOnDrop((WonesysDropCard) card, channelNum, portNum, model);
			if (portNum != 0) {
				SetChannel.allocateChannel((WonesysDropCard) card, channel, ((WonesysDropCard) card).getCommonPort(), (FCPort) card.getPort(portNum));
			}
		}
		// ROADM_ADD are passive. They can not be configured. They return no channels.
		// Connections to ROADM_ADD are hardcoded, they are created in GetInventoryCommand.

		// Other types are ignored as they doesn't allow GetChannels command
	}

	// public static void removeFCPortsAndConnectionsOnDrop(WonesysDropCard card, int dwdmChannel, int portNum,
	// ProteusOpticalSwitch opticalSwitch) {
	//
	// FCPort dstPort = (FCPort) card.getPort(portNum);
	// if (dstPort != null) {
	//
	// // FIXME get channel instead of creating a new one!!!
	// DWDMChannel channel = new DWDMChannel();
	// channel.setChannelNumber(dwdmChannel);
	// channel.setLambda(((WDMChannelPlan) card.getChannelPlan()).getLambda(dwdmChannel));
	//
	// if (dstPort.equals(card.getExpressPort())) {
	// // should unlink subports using channel (if any)
	// FCPort outPort = card.getSubPort(dstPort, channel);
	// if (outPort != null) {
	// FCPort inPort = (FCPort) outPort.getOutgoingDeviceConnections().get(0);
	// if (inPort != null) {
	// outPort.removeDeviceConnection(inPort);
	//
	// // if inPort card is WonesysPassiveAddCard
	// ProteusOpticalSwitchCard inPortCard = (ProteusOpticalSwitchCard) ((FCPort) inPort.getDevices().get(0)).getModule();
	// if (inPortCard instanceof WonesysPassiveAddCard) {
	// // should remove rule in passive card
	// inPortCard.removeSwitchingRule(channel, (FCPort) inPort.getDevices().get(0), channel,
	// ((WonesysPassiveAddCard) inPortCard).getCommonPort());
	// } else {
	// // Other types of card should be configured with an other command
	// }
	// }
	// }
	// }
	// card.removeSwitchingRule(channel, card.getCommonPort(), channel, dstPort);
	// } else {
	// // TODO notify invalid portNum given
	// }
	//
	// //
	// //
	// // // port using dwdmChannel
	// // FCPort old_port = null;
	// //
	// // List<NetworkPort> ports = card.getModulePorts();
	// // for (NetworkPort port : ports) {
	// // if (port instanceof FCPort) {
	// // if (port.getPortNumber() == dwdmChannel) {
	// // old_port = (FCPort) port;
	// // break;
	// // }
	// // }
	// // }
	// //
	// // if (old_port == null)
	// // // no previous FCPorts nor connections using this dwdnChannel
	// // return;
	// //
	// // List<LogicalDevice> linkedPorts = old_port.getOutgoingDeviceConnections();
	// // for (int i = linkedPorts.size() - 1; i >= 0; i--) {
	// // LogicalDevice linkedPort = linkedPorts.get(i);
	// // if (linkedPort instanceof NetworkPort) {
	// // if ((linkedPort instanceof FCPort) && (((NetworkPort) linkedPort).getPortNumber() == dwdmChannel)) {
	// // // dwdmChannel dedicated FCPort
	// // // remove dedicated port
	// // OpticalSwitchCard linkedCard = (OpticalSwitchCard) ((NetworkPort) linkedPort).getModule();
	// // linkedCard.removeModulePort((NetworkPort) linkedPort);
	// // }
	// // // remove connection
	// // old_port.removeDeviceConnection(linkedPort);
	// // }
	// // }
	// // card.removeModulePort(old_port);
	// }

	// public static void removeChannelOnDrop(WonesysDropCard srcCard, int dwdmChannel) {
	//
	// // FIXME get channel instead of creating a new one!!!
	// DWDMChannel channel = new DWDMChannel();
	// channel.setChannelNumber(dwdmChannel);
	// channel.setLambda(((WDMChannelPlan) srcCard.getChannelPlan()).getLambda(dwdmChannel));
	//
	// // remove previous redirection of given channel (if any)
	// FCPort srcSubPort = srcCard.getSubPort(srcCard.getCommonPort(), channel);
	// if (srcSubPort != null) {
	// if (!srcSubPort.getOutgoingDeviceConnections().isEmpty()) {
	// NetworkPort oldDstPort = (NetworkPort) ((FCPort) srcSubPort.getOutgoingDeviceConnections().get(0));
	// if (((FCPort) oldDstPort.getDevices().get(0)).equals(srcCard.getExpressPort())) {
	//
	// // should unlink subports using channel (if any)
	// FCPort inPort = (FCPort) oldDstPort.getOutgoingDeviceConnections().get(0);
	// if (inPort != null) {
	// oldDstPort.removeDeviceConnection(inPort);
	//
	// // if inPort card is WonesysPassiveAddCard
	// ProteusOpticalSwitchCard inPortCard = (ProteusOpticalSwitchCard) ((FCPort) inPort.getDevices().get(0)).getModule();
	// if (inPortCard instanceof WonesysPassiveAddCard) {
	// // should remove rule in passive card
	// inPortCard.removeSwitchingRule(channel, (FCPort) inPort.getDevices().get(0), channel,
	// ((WonesysPassiveAddCard) inPortCard).getCommonPort());
	// } else {
	// // Other types of card should be configured with an other command
	// }
	// }
	// }
	// srcCard.removeSwitchingRule(channel, srcCard.getCommonPort(), channel, oldDstPort);
	// }
	// }
	// }

	/**
	 * Create connections from ALL ports owned by srcCard to port portNum. If portNum is an internal port, then connections to ALL ports connected to
	 * it are created.
	 *
	 * @param srcCard
	 * @param dwdmChannel
	 * @param portNum
	 * @param opticalSwitch
	 */
	public static void allocateChannel(ProteusOpticalSwitchCard srcCard, DWDMChannel channel, FCPort srcPort, FCPort dstPort) {

		srcCard.addSwitchingRule(channel, srcPort, channel, dstPort);

		if (dstPort == null) {
			// get actual dstPort
			FCPort srcSubPort = srcCard.getSubPort(srcPort, channel);
			FCPort dstSubPort = (FCPort) srcSubPort.getOutgoingDeviceConnections().get(0);
			dstPort = (FCPort) dstSubPort.getDevices().get(0);
		}

		if (isLocallyConnectedOnly(dstPort)) {
			FCPort dstSubPort = srcCard.getSubPort(dstPort, channel);
			createPassthrough(dstSubPort, channel);
		}
	}

	public static void freeChannel(ProteusOpticalSwitchCard card, DWDMChannel channel, FCPort srcPort) {

		FCPort srcInSubPort = card.getSubPort(srcPort, channel);
		if (srcInSubPort != null) {
			if (!srcInSubPort.getOutgoingDeviceConnections().isEmpty()) {
				FCPort srcOutSubPort = (FCPort) ((FCPort) srcInSubPort.getOutgoingDeviceConnections().get(0));
				NetworkPort srcOutSubPortParent = (NetworkPort) srcOutSubPort.getDevices().get(0);
				if (isLocallyConnectedOnly(srcOutSubPortParent)) {

					removePassthrough(srcOutSubPort, channel);
				}
				card.removeSwitchingRule(channel, srcPort, channel, srcOutSubPortParent);
			}
		}
	}

	private static void createPassthrough(FCPort srcOutSubPort, DWDMChannel channel) {

		FCPort srcOutParentPort = (FCPort) srcOutSubPort.getDevices().get(0);
		if (!srcOutParentPort.getOutgoingDeviceConnections().isEmpty()) {

			// obtain where the port is connected to
			FCPort dstCardPort = (FCPort) srcOutParentPort.getOutgoingDeviceConnections().get(0);
			ProteusOpticalSwitchCard dstCard = (ProteusOpticalSwitchCard) dstCardPort.getModule();

			DWDMChannel dstChannel = (DWDMChannel) ((WDMChannelPlan) dstCard.getChannelPlan()).getChannel(
					((WDMChannelPlan) dstCard.getChannelPlan()).getChannelNumberFromLambda(channel.getLambda()));
			// DWDMChannel dstChannel = new DWDMChannel();
			// dstChannel.setLambda(channel.getLambda());
			// // given lambda may match to a different channelNumber in dstChannelPlan
			// dstChannel.setChannelNumber(((WDMChannelPlan) dstCard.getChannelPlan()).getChannelNumber(
			// ((WDMChannelPlan) dstCard.getChannelPlan()).getFrequency(channel.getLambda())));

			if (dstCard.isPassive()) {
				// should create connection in a passive card
				allocateChannel(dstCard, dstChannel, dstCardPort, null);
			}

			FCPort dstInSubPort = dstCard.getSubPort(dstCardPort, dstChannel);
			srcOutSubPort.addDeviceConnection(dstInSubPort);

		} else {
			// no connections from given port
			// TODO decide if should fail (throw exception) or just warn
			log.warn("Creating connection to a port without connections!!!");
		}
	}

	private static void removePassthrough(FCPort srcOutSubPort, DWDMChannel channel) {

		// unlink subports using channel (if any)
		if (!srcOutSubPort.getOutgoingDeviceConnections().isEmpty()) {
			FCPort inPort = (FCPort) srcOutSubPort.getOutgoingDeviceConnections().get(0);

			if (inPort != null) {
				srcOutSubPort.removeDeviceConnection(inPort);

				// if inPort card is WonesysPassiveAddCard
				FCPort inPortParent = (FCPort) inPort.getDevices().get(0);
				ProteusOpticalSwitchCard inPortCard = (ProteusOpticalSwitchCard) inPortParent.getModule();
				if (inPortCard.isPassive()) {
					// should remove the channel in a passive card
					freeChannel(inPortCard, channel, inPortParent);
				}
			}
		}
	}

	/**
	 *
	 * @param srcPort
	 * @return true if given port has connections and all of them are connected to ports in the same System than given port, or false otherwise.
	 */
	private static boolean isLocallyConnectedOnly(NetworkPort srcPort) {

		org.opennaas.extensions.router.model.System srcPortSystem = srcPort.getModule().getSystems().get(0);

		if (srcPort.getOutgoingDeviceConnections().isEmpty())
			return false;

		for (LogicalDevice otherPort : srcPort.getOutgoingDeviceConnections()) {
			if (otherPort instanceof NetworkPort) {
				org.opennaas.extensions.router.model.System otherPortSystem = ((NetworkPort) otherPort).getModule().getSystems().get(0);
				if (!srcPortSystem.equals(otherPortSystem))
					return false;
			}
		}

		return true;
	}

	@Override
	protected String getWonesysCommandDeviceId() {
		return chassisHexStr + slotHexStr;
	}

	@Override
	protected String getWonesysCommandId() {
		return COMMAND_ID;
	}

	@Override
	protected String getWonesysCommandRequiredDataLength() {
		return DATA_LENGTH;
	}

	@Override
	protected String getWonesysCommandData() {
		return channelHexStr + portHexStr;
	}

}
