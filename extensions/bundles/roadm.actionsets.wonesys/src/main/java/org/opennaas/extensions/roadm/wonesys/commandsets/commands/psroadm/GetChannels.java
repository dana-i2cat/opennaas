package org.opennaas.extensions.roadm.wonesys.commandsets.commands.psroadm;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysResponse;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.FiberChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GetChannels extends WonesysCommand {

	Log							log			= LogFactory.getLog(WonesysCommand.class);

	private static final String	COMMAND_ID	= "0b04";
	private static final String	DATA_LENGTH	= "0000";

	private int					chassis;
	private int					slot;

	private String				chassisHexStr;
	private String				slotHexStr;

	public GetChannels(int chassis, int slot) {

		this.chassis = chassis;
		this.slot = slot;

		this.chassisHexStr = toByteHexString(chassis, 1);
		this.slotHexStr = toByteHexString(slot, 1);
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

		ProteusOpticalSwitchCard relatedCard = ((ProteusOpticalSwitch) model).getCard(chassis, slot);
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

				NetworkPort portInModel = relatedCard.getPort(port);
				if (portInModel == null) {
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

	// private void createFCPortsAndConnectionsOnDrop(OpticalSwitchCard srcCard, int dwdmChannel, int portNum, ComputerSystem opticalSwitch) {
	//
	// // ROADM_DROP is attached to only one port, the one where the fiber is connected.
	// // But this doesn't matter
	// List<NetworkPort> srcPorts = new ArrayList<NetworkPort>();
	// srcPorts.addAll(srcCard.getModulePorts());
	//
	// List<NetworkPort> dstPorts = new ArrayList<NetworkPort>();
	//
	// if (srcCard.isInternalPort(portNum)) {
	// // should obtain where the internal port is connected to
	// List<LogicalDevice> connectedDevices = srcCard.getInternalPort(portNum).getOutgoingDeviceConnections();
	//
	// for (LogicalDevice device : connectedDevices) {
	// if (device instanceof NetworkPort) {
	// dstPorts.add((NetworkPort) device);
	// }
	// }
	// } else {
	// dstPorts.add(OpticalSwitchController.getPort(opticalSwitch, portNum));
	// }
	//
	// OpticalSwitchController.createPortConnections(dwdmChannel, srcPorts, dstPorts);
	// }

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
		return "";
	}

}
