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

import java.util.HashMap;
import java.util.List;

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
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimePeriod;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedPortStatistics;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedStatistics;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedSwitchPortStatistics;

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

	private TimedSwitchPortStatistics allStatistics;

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
	public TimedSwitchPortStatistics getPortStatistics(TimePeriod period) {

		// TODO filter by period

		// defensive copy
		return new TimedSwitchPortStatistics(allStatistics);
	}

	@Override
	public TimedPortStatistics getPortStatistics(TimePeriod period,
			String switchId) {

		// TODO filter by period
		
		TimedPortStatistics stats = new TimedPortStatistics();
		stats.setStatisticsMap(new HashMap<String, List<TimedStatistics>>(allStatistics.getStatisticsMap().get(switchId)));
		return stats;
	}

}
