package org.opennaas.extensions.genericnetwork.actionsets.internal.portstatistics.actions;

/*
 * #%L
 * OpenNaaS :: Generic Network
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
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.genericnetwork.capability.statistics.INetworkStatisticsCapability;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.NetworkStatistics;
import org.opennaas.extensions.genericnetwork.model.driver.DevicePortId;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.PortStatistics;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.SwitchPortStatistics;

import com.google.common.collect.BiMap;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class GetPortStatisticsAction extends Action {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		try {
			GenericNetworkModel model = (GenericNetworkModel) this.getModelToUpdate();

			// obtain INetworkStatisticsCapability capability of own resource
			IResource resource = (IResource) params;
			INetworkStatisticsCapability networkStatisticsCapability;
			try {
				networkStatisticsCapability = (INetworkStatisticsCapability) resource.getCapabilityByInterface(INetworkStatisticsCapability.class);
			} catch (ResourceException e) {
				throw new ActionException(
						"Failed to obtain INetworkStatisticsCapability! It is mandatory having INetworkStatisticsCapability to use IPortStatisticsCapability in generic networks!",
						e);
			}

			// obtain NetworkStatistics using capability
			NetworkStatistics networkStatistics = networkStatisticsCapability.getNetworkStatistics();

			// generate own SwitchPortStatistics from underlying network elements information
			SwitchPortStatistics ownPortStatistics = generateSwitchPortStatistics(networkStatistics.getSwitchStatistics(), model.getTopology()
					.getNetworkDevicePortIdsMap());

			// create action response with an object response
			ActionResponse response = new ActionResponse();
			response.setStatus(ActionResponse.STATUS.OK);
			response.setResult(ownPortStatistics);

			return response;
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (!(params instanceof IResource)) {
			throw new ActionException("Invalid parameters for action " + this.actionID);
		}
		return true;
	}

	protected static SwitchPortStatistics generateSwitchPortStatistics(Map<String, SwitchPortStatistics> switchesStatistics,
			BiMap<String, DevicePortId> networkDevicePortIdsMap) throws Exception {

		Map<String, PortStatistics> stats = new HashMap<String, PortStatistics>();

		// extract own network port IDs and associate obtained PortStatistics
		for (Entry<String, DevicePortId> entry : networkDevicePortIdsMap.entrySet()) {
			// get underlying network element and port IDs
			String networkElementId = entry.getValue().getDeviceId();
			String networkElementPortId = entry.getValue().getDevicePortId();

			// check existence of statistics for element and port
			if (!switchesStatistics.containsKey(networkElementId) || switchesStatistics.get(networkElementId).getStatistics() == null || !switchesStatistics
					.get(networkElementId).getStatistics().containsKey(networkElementId.split(Pattern.quote(":"))[1] + "." + networkElementPortId)) {
				throw new Exception(
						"Could not find statistics for network element with ID = " + networkElementId + " and port ID = " + networkElementPortId + "!");
			}

			// get PortStatistics and associate it to own network port ID
			PortStatistics portStatistics = switchesStatistics.get(networkElementId).getStatistics()
					.get(networkElementId.split(Pattern.quote(":"))[1] + "." + networkElementPortId);
			stats.put(entry.getKey(), portStatistics);
		}

		// generate SwitchPortStatistics from stats
		SwitchPortStatistics switchPortStatistics = new SwitchPortStatistics();
		switchPortStatistics.setStatistics(stats);

		return switchPortStatistics;
	}
}
