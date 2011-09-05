package net.i2cat.luminis.commandsets.wonesys.commands.psroadm;

import net.i2cat.luminis.commandsets.wonesys.WonesysCommand;
import net.i2cat.luminis.commandsets.wonesys.WonesysResponse;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.opticalSwitch.FiberChannelPlan;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import net.i2cat.mantychore.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GetChannelInfo extends WonesysCommand {

	private static final String	COMMAND_ID	= "0b01";
	private static final String	DATA_LENGTH	= "0200";

	Log							log			= LogFactory.getLog(WonesysCommand.class);

	private int					chassis;
	private int					slot;
	private int					channelNum;

	private String				chassisHexStr;
	private String				slotHexStr;
	private String				channelHexStr;											// 2B

	public GetChannelInfo(int chassis, int slot, int channelNum) {
		super();
		this.chassis = chassis;
		this.slot = slot;
		this.channelNum = channelNum;

		this.chassisHexStr = toByteHexString(chassis, 1);
		this.slotHexStr = toByteHexString(slot, 1);
		this.channelHexStr = toByteHexString(channelNum, 2);
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

		String responseData = commandResponse.getInformation();

		int port = Integer.parseInt(responseData, 16);

		if (port != 0) {
			log.info("Channel " + channelNum + " mapped to port " + port);

			NetworkPort portInModel = relatedCard.getPort(port);
			if (portInModel == null) {
				log.error("Mapped port is not in model. Skipping this mapping although channel is in use");
				return;
			}
		} else {
			// if (port == 0) --> channel is not associated with any port
			// log.trace("Channel " + dwdmChannel + " not in use");
		}
		SetChannel.setChannelInModel(relatedCard, channelNum, port, (ProteusOpticalSwitch) model);
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
		return channelHexStr;
	}

}
