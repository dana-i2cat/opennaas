package org.opennaas.extensions.router.model.opticalSwitch;

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

import org.opennaas.extensions.router.model.NetworkPort;

public interface IOpticalSwitchCard {

	/**
	 * Connects srcChannel in srcPort with dstChannel in dstPort.
	 * 
	 * @param srcChannel
	 * @param srcPort
	 * @param dstChannel
	 * @param dstPort
	 * @return
	 */
	public boolean addSwitchingRule(FiberChannel srcChannel, NetworkPort srcPort, FiberChannel dstChannel, NetworkPort dstPort);

	public boolean removeSwitchingRule(FiberChannel srcChannel, NetworkPort srcPort, FiberChannel dstChannel, NetworkPort dstPort);

	public List getSwitchingRules();

	public List<NetworkPort> getPorts();

	/**
	 * Returns channels ready to be configured on given port. Notice it is not the same as free channels on given port, as this card may introduce
	 * further restrictions. Assumes given port is in this card
	 * 
	 * @param port
	 * @return
	 */
	public List<FiberChannel> getAvailableChannels(NetworkPort port);

	public boolean isPassive();

}
