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

import java.util.List;

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
import org.opennaas.extensions.genericnetwork.model.NetworkStatistics;
import org.opennaas.extensions.openflowswitch.capability.monitoring.IMonitoringCapability;
import org.opennaas.extensions.openflowswitch.capability.monitoring.SwitchPortStatistics;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class GetNetworkStatisticsAction extends Action {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		ActionResponse response = new ActionResponse();

		try {
			List<IResource> resources = getNetworkSwitches();
			NetworkStatistics netStats = getSwitchesStatistics(resources);
			response.setResult(netStats);
			response.setStatus(ActionResponse.STATUS.OK);

		} catch (ActivatorException ae) {
			throw new ActionException(ae);
		}

		return response;
	}

	private NetworkStatistics getSwitchesStatistics(List<IResource> resources) {
		NetworkStatistics netStats = new NetworkStatistics();
		for (IResource resource : resources) {
			IMonitoringCapability monitorCapab = null;
			try {
				monitorCapab = (IMonitoringCapability) resource.getCapabilityByInterface(IMonitoringCapability.class);
			} catch (ResourceException e) {
				log.debug("Switch with resource with name = '" + resource.getResourceDescriptor().getInformation().getName() + "' and ID = " + resource
						.getResourceDescriptor().getId() + " has not Monitoring Capability. Skipping it.");
				// continue with next switch
				continue;
			}
			try {
				SwitchPortStatistics switchStatistics = monitorCapab.getPortStatistics();
				netStats.addPortSwitchStatistic(resource.getResourceDescriptor().getInformation().getName(), switchStatistics);
			} catch (CapabilityException e) {
				log.error(
						"Error getting port statistics from switch with name = '" + resource.getResourceDescriptor().getInformation().getName() + "' and ID  = " + resource
								.getResourceDescriptor().getId(), e);
			}
		}

		return netStats;

	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (params != null)
			log.debug("Params ignored for action " + this.getActionID());

		return true;
	}

	/**
	 * TODO to be removed when network is aware of its topology
	 * 
	 * @return
	 * @throws ActivatorException
	 */
	private List<IResource> getNetworkSwitches() throws ActivatorException {

		IResourceManager rm = Activator.getResourceManagerService();
		List<IResource> ofSwitches = rm.listResourcesByType("openflowswitch");

		return ofSwitches;
	}

}
