package org.opennaas.extensions.roadm.wonesys.commandsets.commands.psroadm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysCommand;
import org.opennaas.extensions.roadm.wonesys.commandsets.WonesysResponse;
import org.opennaas.extensions.router.model.opticalSwitch.ITUGrid;
import org.opennaas.extensions.router.model.opticalSwitch.WDMChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;

public class GetChannelPlan extends WonesysCommand {

	private static final String	COMMAND_ID	= "0b03";
	private static final String	DATA_LENGTH	= "0000";

	private int					chassis;
	private int					slot;

	private String				chassisHexStr;
	private String				slotHexStr;

	Log							log			= LogFactory.getLog(GetChannelPlan.class);

	public GetChannelPlan(int chassisNum, int slotNum) {
		super();

		this.chassis = chassisNum;
		this.slot = slotNum;

		this.chassisHexStr = toByteHexString(chassisNum, 1);
		this.slotHexStr = toByteHexString(slotNum, 1);
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

		// * Canal DWDM: 0-824 (Ver ITU_Grid_Planning.ods)
		// ** Espaciado: 0x00: 100 GHz, 0x01: 50 GHz, 0x02: 25 GHz, 0x03: 12,5 GHz
		String sfirstChannel = responseData.substring(0, 4);
		sfirstChannel = convertLittleBigEndian(sfirstChannel);
		String slastChannel = responseData.substring(4, 8);
		slastChannel = convertLittleBigEndian(slastChannel);
		String schannelGap = responseData.substring(8, 10);

		int firstChannel = Integer.parseInt(sfirstChannel, 16);
		int lastChannel = Integer.parseInt(slastChannel, 16);
		double guardIntervalInv = Math.pow(2, Integer.parseInt(schannelGap, 16));
		double guardInterval = (100 / (guardIntervalInv * 1000)); // THz

		ITUGrid ituGrid = new ITUGrid();
		double cardMaxFreq = ituGrid.getFrequencyFromChannelNum(firstChannel);
		double cardMinFreq = ituGrid.getFrequencyFromChannelNum(lastChannel);

		WDMChannelPlan cardChannelPlan = new WDMChannelPlan(cardMaxFreq, cardMinFreq, guardInterval, ituGrid);

		ProteusOpticalSwitchCard card = ((ProteusOpticalSwitch) model).getCard(chassis, slot);

		card.setChannelPlan(cardChannelPlan);

		log.info("ChannelPlan loaded: Card supports " + cardChannelPlan.getAllChannels().size() + " channels\n" +
				"First channel=" + cardChannelPlan.getFirstChannel() + " LastChannel=" + cardChannelPlan.getLastChannel() + " ChannelGap=" + cardChannelPlan
						.getChannelGap());
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
		return "";
	}

}
