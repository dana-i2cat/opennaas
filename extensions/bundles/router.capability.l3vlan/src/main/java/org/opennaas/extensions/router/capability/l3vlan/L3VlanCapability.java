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
import org.opennaas.extensions.router.capabilities.api.helper.VLANBridgeApiHelper;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomains;
import org.opennaas.extensions.router.model.System;
import org.opennaas.extensions.router.model.utils.ModelHelper;

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
		log.info("Start of addIpAddressToBridgedDomain call");
		String[] params = new String[2];
		params[0] = domainName;
		params[1] = ipAddress;

		IAction action = createActionAndCheckParams(L3VlanActionset.L3VLAN_ADD_IP_TO_DOMAIN, params);
		queueAction(action);
		log.info("End of addIpAddressToBridgedDomain call");
	}

	@Override
	public void removeIpAddressfromBridgedDomain(String domainName, String ipAddress) throws CapabilityException {
		// FIXME pending to resolve or workaround Junos bug:
		// http://forums.juniper.net/t5/Junos/quot-syntax-error-expecting-lt-rpc-gt-or-lt-rpc-gt-quot-trying/m-p/235936

		throw new CapabilityException(new UnsupportedOperationException("Not implemented due to bug in Junos"));

		// FIXME org.opennaas.extensions.router.junos.actionssets.actions.l3vlan.RemoveIPAction from
		// org.opennaas.extensions.router.actionsets.junos bundle producing error above
		// uncomment when solved

		// log.info("Start of removeIpAddressfromBridgedDomain call");
		// String[] params = new String[2];
		// params[0] = domainName;
		// params[1] = ipAddress;
		//
		// IAction action = createActionAndCheckParams(L3VlanActionset.L3VLAN_REMOVE_IP_FROM_DOMAIN, params);
		// queueAction(action);
		// log.info("End of removeIpAddressfromBridgedDomain call");
	}

	@Override
	public BridgeDomains getL3Vlans() throws CapabilityException {
		return VLANBridgeApiHelper.buildApiBridgeDomains(ModelHelper.getL3BridgeDomains((System) resource.getModel()));
	}
}
