package org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards;

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

import java.util.List;

import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberChannel;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.IDWDMTranceiverCard;

public class WonesysTransceiverCard extends ProteusOpticalSwitchCard implements IDWDMTranceiverCard {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3406989632621913235L;

	public WonesysTransceiverCard(int chasis, int slot, int type, int subtype) {
		super(chasis, slot, type, subtype);
		// TODO Auto-generated constructor stub
	}

	public boolean addSwitchingRule(FiberChannel srcChannel, NetworkPort srcPort, FiberChannel dstChannel, NetworkPort dstPort) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeSwitchingRule(FiberChannel srcChannel, NetworkPort srcPort, FiberChannel dstChannel, NetworkPort dstPort) {
		// TODO Auto-generated method stub
		return false;
	}

	public List getSwitchingRules() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<NetworkPort> getPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	public void configureElectricToOptic(NetworkPort srcPort, FCPort dstPort, DWDMChannel dstChannel) {
		// TODO Auto-generated method stub

	}

	public void configureOpticToElectric(FCPort srcPort, DWDMChannel srcChannel, NetworkPort dstPort) {
		// TODO Auto-generated method stub

	}

	public List<FiberChannel> getAvailableChannels(NetworkPort port) {
		// TODO Auto-generated method stub
		return null;
	}

}
