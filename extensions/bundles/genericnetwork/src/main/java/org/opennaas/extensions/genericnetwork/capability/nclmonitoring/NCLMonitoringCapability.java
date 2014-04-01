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

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.genericnetwork.Activator;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.NCLProvisionerCapability;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class NCLMonitoringCapability extends AbstractCapability implements INCLMonitoringCapability {

	public static final String	CAPABILITY_TYPE	= "nclmonitoring";

	private Log					log				= LogFactory.getLog(NCLProvisionerCapability.class);

	private String				resourceId		= "";
	private IEventManager		eventManager;

	private NCLMonitoring		nclMonitoring;

	public NCLMonitoringCapability(CapabilityDescriptor descriptor, String resourceId) {
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
		throw new UnsupportedOperationException("Not Implemented. This capability is not using the queue.");
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		throw new UnsupportedOperationException("This capability does not contain actionset.");
	}

	@Override
	public void activate() throws CapabilityException {
		initNCLMonitoring();
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(),
				INCLMonitoringCapability.class.getName());
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

	/**
	 * Register the capability like an OSGi service but NOT as a web service through DOSGi.
	 * 
	 * The fact this capability has no methods to export as WS in INCLMonitoringCapability causes an error if registered with DOSGi.
	 * 
	 * @param name
	 * @param resourceId
	 * @return
	 * @throws CapabilityException
	 */
	protected ServiceRegistration registerService(BundleContext bundleContext, String capabilityName, String resourceType, String resourceName,
			String ifaceName) throws CapabilityException {
		Dictionary<String, String> props = new Hashtable<String, String>();
		return registration = bundleContext.registerService(ifaceName, this, props);
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

}
