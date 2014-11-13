package org.opennaas.extensions.genericnetwork.actionsets.internal.statistics.actions;

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

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.genericnetwork.Activator;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.NetworkStatistics;
import org.opennaas.extensions.genericnetwork.model.driver.DevicePortId;
import org.opennaas.extensions.genericnetwork.model.helpers.TopologyHelper;
import org.opennaas.extensions.genericnetwork.model.topology.NetworkElement;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.IPortStatisticsCapability;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.PortStatistics;
import org.opennaas.extensions.openflowswitch.capability.portstatistics.SwitchPortStatistics;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class GetNetworkStatisticsAction extends Action {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		GenericNetworkModel networkModel = (GenericNetworkModel) getModelToUpdate();

		NetworkStatistics netStats = new NetworkStatistics();
		if (networkModel.getTopology() == null) {
			log.warn("Failed to obtain network statistics. Topology is not loaded yet.");
		} else {
			for (NetworkElement ne : networkModel.getTopology().getNetworkElements()) {
				try {
					SwitchPortStatistics switchPortStatistics = getSwitchPortStatisticsForNetworkElement(ne, networkModel);
					netStats.addPortSwitchStatistic(ne.getId(), switchPortStatistics);
				} catch (Exception e) {
					log.warn("Failed to obtain port statistics for network element" + ne.getId() + ". Skipping it.", e);
				}
			}
		}

		ActionResponse response = ActionResponse.okResponse(getActionID());
		response.setResult(netStats);
		return response;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (params != null)
			log.debug("Params ignored for action " + this.getActionID());

		return true;
	}

	private IResourceManager getResourceManager() throws ActivatorException {
		return Activator.getResourceManagerService();
	}

	private SwitchPortStatistics getSwitchPortStatisticsForNetworkElement(NetworkElement networkElement, GenericNetworkModel networkModel)
			throws CapabilityException, Exception {

		IResource resource;
		try {
			resource = TopologyHelper.getResourceFromNetworkElementId(networkElement.getId(), getResourceManager());
		} catch (Exception e) {
			throw new Exception("Failed to obtain mapping resource for network element " + networkElement.getId(), e);
		}

		IPortStatisticsCapability monitorCapab;
		try {
			monitorCapab = (IPortStatisticsCapability) resource.getCapabilityByInterface(IPortStatisticsCapability.class);
		} catch (ResourceException e) {
			throw new Exception("Failed to obtain IMonitorCapability of mapping resource for network element " + networkElement.getId(), e);
		}

		SwitchPortStatistics switchStatistics = monitorCapab.getPortStatistics();
		SwitchPortStatistics netSwitchStatistics = portIdsToNetPortIds(networkElement, switchStatistics, networkModel);

		return netSwitchStatistics;
	}

	private SwitchPortStatistics portIdsToNetPortIds(NetworkElement networkElement, SwitchPortStatistics switchStatistics,
			GenericNetworkModel networkModel) {
		SwitchPortStatistics netSwitchStatistics = new SwitchPortStatistics();
		netSwitchStatistics.setStatistics(new HashMap<String, PortStatistics>());

		// Assuming networkElement.getId() is the device id.
		// String deviceId = networkModel.getDeviceResourceMap().get(networkElement.getId());
		String deviceId = networkElement.getId();

		for (String portId : switchStatistics.getStatistics().keySet()) {

			DevicePortId devicePortId = new DevicePortId();
			devicePortId.setDeviceId(deviceId);
			devicePortId.setDevicePortId(portId.toString());
			String netPortId = networkModel.getTopology().getNetworkDevicePortIdsMap().inverse().get(devicePortId);
			if (netPortId == null) {
				log.warn("Cannot find mapping network port for device " + deviceId + " and port " + portId + ". Skipping it.");
			} else {
				netSwitchStatistics.getStatistics().put(netPortId, switchStatistics.getStatistics().get(portId));
			}
		}

		return netSwitchStatistics;
	}
}
