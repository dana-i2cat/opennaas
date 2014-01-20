package org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus;

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

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards.ProteusOpticalSwitchCard;

public class ProteusOpticalSwitch extends org.opennaas.extensions.router.model.System {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 6737244447225380204L;
	private List<FiberConnection>	fiberConnections	= new ArrayList<FiberConnection>();

	public List<FiberConnection> getFiberConnections() {
		return fiberConnections;
	}

	public void setFiberConnections(List<FiberConnection> fiberConnections) {
		this.fiberConnections = fiberConnections;
	}

	/**
	 * Returns ProteusOpticalSwitchCard identifyied with given chasis and slot, or null if there is no such card.
	 * 
	 * @param chasis
	 * @param slot
	 * @return
	 */
	public ProteusOpticalSwitchCard getCard(int chasis, int slot) {

		for (LogicalDevice dev : getLogicalDevices()) {
			if (dev instanceof ProteusOpticalSwitchCard) {
				if (((ProteusOpticalSwitchCard) dev).getChasis() == chasis &&
						((ProteusOpticalSwitchCard) dev).getSlot() == slot) {
					return (ProteusOpticalSwitchCard) dev;
				}
			}
		}
		return null;
	}

}
