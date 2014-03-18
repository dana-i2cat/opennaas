package org.opennaas.extensions.router.capability.l3vlan;

/*
 * #%L
 * OpenNaaS :: Router :: L3 VLAN Capability
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomains;

public class L3VlanCapability extends AbstractCapability implements IL3VlanCapability {

	public static final String	CAPABILITY_TYPE	= "l3vlan";

	private String				resourceId		= "";

	Log							log				= LogFactory.getLog(L3VlanCapability.class);

	public L3VlanCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new L3Vlan Capability");

	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);
		try {
			return Activator.getL3VlanActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		getQueueManager(resourceId).queueAction(action);

	}

	/**
	 * 
	 * @return QueuemanagerService this capability is associated to.
	 * @throws CapabilityException
	 *             if desired queueManagerService could not be retrieved.
	 */
	private IQueueManagerCapability getQueueManager(String resourceId) throws CapabilityException {
		try {
			return Activator.getQueueManagerService(resourceId);
		} catch (ActivatorException e) {
			throw new CapabilityException("Failed to get QueueManagerService for resource " + resourceId, e);
		}
	}

	@Override
	public void activate() throws CapabilityException {

		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IL3VlanCapability.class.getName());
		super.activate();

	}

	@Override
	public void deactivate() throws CapabilityException {

		registration.unregister();
		super.deactivate();

	}

	@Override
	public void addIpAddressToBridgedDomain(String domainName, String ipAddress) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeIpAddressfromBridgedDomain(String domainName, String ipAddress) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public BridgeDomains getL3Vlans() throws CapabilityException {
		// FIXME replace by real implementation

		BridgeDomains domains = new BridgeDomains();

		List<String> domainNames = new ArrayList<String>();
		domainNames.add("fe-0/1/0.0");
		domainNames.add("fe-0/2/1.3");

		domains.setDomainNames(domainNames);

		return domains;
	}
}
