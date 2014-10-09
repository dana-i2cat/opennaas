package org.opennaas.extensions.genericnetwork.capability.nclmonitoring;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.genericnetwork.Activator;
import org.opennaas.extensions.genericnetwork.capability.nclmonitoring.portstatistics.IPortStatisticsMonitoringCapability;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.NCLProvisionerCapability;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimePeriod;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedPortStatistics;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedStatistics;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class NCLMonitoringCapability extends AbstractCapability implements
		IPortStatisticsMonitoringCapability {

	public static final String CAPABILITY_TYPE = "nclmonitoring";

	private Log log = LogFactory.getLog(NCLProvisionerCapability.class);

	private String resourceId = "";
	private IEventManager eventManager;

	private NCLMonitoring nclMonitoring;

	public NCLMonitoringCapability(CapabilityDescriptor descriptor,
			String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new NCLMonitoring Capability");
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		throw new UnsupportedOperationException(
				"Not Implemented. This capability is not using the queue.");
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException(
				"This capability does not contain actionset.");
	}

	@Override
	public void activate() throws CapabilityException {
		initNCLMonitoring();
		registerService(Activator.getContext(), CAPABILITY_TYPE,
				getResourceType(), getResourceName(),
				IPortStatisticsMonitoringCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		stopNCLMonitoring();
		super.deactivate();
	}

	public IEventManager getEventManager() {
		return eventManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	// ///////////////////////////////
	// NCLMonitoring Implementation //
	// ///////////////////////////////

	private void initNCLMonitoring() {
		nclMonitoring = new NCLMonitoring();
		nclMonitoring.setEventManager(eventManager);
		nclMonitoring.setResource(resource);
		nclMonitoring.init();
	}

	private void stopNCLMonitoring() {
		nclMonitoring.stop();
		nclMonitoring = null;
	}

	// //////////////////////////////////////////////////
	// IPortStatisticsMonitoringCapability implementation
	// //////////////////////////////////////////////////

	@Override
	public TimedPortStatistics getPortStatistics(TimePeriod period) {

		// filtered map with keys from period.getInit() to period.getEnd() both inclusive (achieved by adding 1 to period.getEnd() :P)
		SortedMap<Long, Map<String, List<TimedStatistics>>> filteredMap = ((GenericNetworkModel)resource.getModel()).getTimedSwitchPortStatistics().getStatisticsMap().
				subMap(Long.valueOf(period.getInit()), Long.valueOf(period.getEnd()+1));
		
		// build a list with all values
		List<TimedStatistics> stats = new ArrayList<TimedStatistics>();
		for (Long timestamp : filteredMap.keySet()) {
			for (String switchId : filteredMap.get(timestamp).keySet()) {
				stats.addAll(filteredMap.get(timestamp).get(switchId));
			}
		}
		
		TimedPortStatistics result = new TimedPortStatistics();
		result.setStatistics(stats);
		return result;
	}

	@Override
	public TimedPortStatistics getPortStatistics(TimePeriod period,
			String switchId) {

		// filtered map with keys from period.getInit() to period.getEnd() both inclusive (achieved by adding 1 to period.getEnd() :P)
		SortedMap<Long, Map<String, List<TimedStatistics>>> filteredMap = ((GenericNetworkModel)resource.getModel()).getTimedSwitchPortStatistics().getStatisticsMap().
				subMap(Long.valueOf(period.getInit()), Long.valueOf(period.getEnd()+1));
		
		// build a list with all values
		List<TimedStatistics> switchStats = new ArrayList<TimedStatistics>();
		for (Long timestamp : filteredMap.keySet()) {
			switchStats.addAll(filteredMap.get(timestamp).get(switchId));
		}
		
		TimedPortStatistics stats = new TimedPortStatistics();
		stats.setStatistics(switchStats);
		return stats;
	}

}
