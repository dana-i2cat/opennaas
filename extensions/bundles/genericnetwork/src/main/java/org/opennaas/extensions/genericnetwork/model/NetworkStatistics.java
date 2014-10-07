package org.opennaas.extensions.genericnetwork.model;

/*
 * #%L
 * OpenNaaS :: OF Network
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

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.openflowswitch.capability.portstatistics.SwitchPortStatistics;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class NetworkStatistics {

	/**
	 * Key: SwitchId, Value: SwitchPortStatistics for that switch
	 */
	private Map<String, SwitchPortStatistics>	switchStatistics;

	public NetworkStatistics() {
		switchStatistics = new HashMap<String, SwitchPortStatistics>();
	}

	public SwitchPortStatistics getSwitchPortStatistic(String switchId) {
		return switchStatistics.get(switchId);
	}

	public void addPortSwitchStatistic(String switchName, SwitchPortStatistics switchPortStatistics) {
		switchStatistics.put(switchName, switchPortStatistics);
	}

	public void removePortSwitchStatistic(String switchId) {
		switchStatistics.remove(switchId);
	}

	public Map<String, SwitchPortStatistics> getSwitchStatistics() {
		return switchStatistics;
	}

	public void setSwitchStatistics(Map<String, SwitchPortStatistics> switchStatistics) {
		this.switchStatistics = switchStatistics;
	}

}
