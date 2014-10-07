package org.opennaas.extensions.openflowswitch.driver.floodlight.monitoring.actionssets.actions;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Floodlight driver v0.90
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

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.PortStatistics;
import org.opennaas.extensions.openflowswitch.driver.floodlight.offorwarding.actionssets.FloodlightAction;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient.IFloodlightPortsStatisticsClient;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient.wrappers.SwitchStatisticsMap;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class GetPortStatisticsAction extends FloodlightAction {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		try {
			// obtain the switch ID from the protocol session
			String switchId = getSwitchIdFromSession(protocolSessionManager);

			// call Floodlight client
			IFloodlightPortsStatisticsClient client = getFloodlightProtocolSession(protocolSessionManager).getFloodlightPortsStatisticsClientForUse();
			SwitchStatisticsMap statistics = client.getPortsStatistics(switchId);

			// transform objects
			Map<String, Map<Integer, PortStatistics>> switchStatisticsMap = statistics.getSwitchStatisticsMap();
			Map<Integer, PortStatistics> portStatistics = switchStatisticsMap.get(switchId);
			Map<String, PortStatistics> realPortStatistics = new HashMap<String, PortStatistics>();
			for (Integer i : portStatistics.keySet()) {
				// discard ports with negative id
				// they have a special meaning in floodlight, but not in opennaas.
				if (i >= 0)
					realPortStatistics.put(i.toString(), portStatistics.get(i));
			}

			// create action response with an object response
			ActionResponse response = new ActionResponse();
			response.setStatus(ActionResponse.STATUS.OK);
			response.setResult(realPortStatistics);

			return response;
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params != null) {
			throw new ActionException("Invalid parameters for action " + this.actionID);
		}
		return true;
	}

}
