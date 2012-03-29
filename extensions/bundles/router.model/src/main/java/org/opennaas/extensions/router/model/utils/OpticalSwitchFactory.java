package org.opennaas.extensions.router.model.utils;

import java.io.IOException;

import org.opennaas.extensions.router.model.opticalSwitch.ITUGrid;
import org.opennaas.extensions.router.model.opticalSwitch.WDMChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;

public class OpticalSwitchFactory {

	public ProteusOpticalSwitch newPedrosaProteusOpticalSwitch() throws Exception {
		ProteusOpticalSwitch opticalSwitch = new ProteusOpticalSwitch();
		opticalSwitch.setName("pedrosa");

		/* create two cards */

		OpticalSwitchCardFactory factory;
		try {
			factory = new OpticalSwitchCardFactory();
		} catch (IOException e) {
			throw new Exception("Failed to load received data into model. Error loading card profiles file:", e);
		}

		// PSROADM DROP card
		int dropChasis = 0;
		int dropSlot = 1;
		int dropType = 11;
		int dropSubtype = 1;
		ProteusOpticalSwitchCard dropCard = factory.createCard(dropChasis, dropSlot, dropType, dropSubtype);
		opticalSwitch.addLogicalDevice(dropCard);

		// PSROADM ADD card
		int addChasis = 0;
		int addSlot = 17;
		int addType = 11;
		int addSubtype = 32;
		ProteusOpticalSwitchCard addCard = factory.createCard(addChasis, addSlot, addType, addSubtype);
		opticalSwitch.addLogicalDevice(addCard);

		factory.addHardcodedCardConnections(opticalSwitch);

		ITUGrid ituGrid = new ITUGrid();
		double maxFreq = ituGrid.getFrequencyFromChannelNum(32);
		double minFreq = ituGrid.getFrequencyFromChannelNum(392);
		double guardInterval = 0.1;
		WDMChannelPlan channelPlan = new WDMChannelPlan(maxFreq, minFreq, guardInterval, ituGrid);

		dropCard.setChannelPlan(channelPlan);
		addCard.setChannelPlan(channelPlan);

		return opticalSwitch;
	}

}
