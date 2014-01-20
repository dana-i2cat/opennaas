package org.opennaas.extensions.router.model.utils;

/*
 * #%L
 * OpenNaaS :: CIM Model
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
