package org.opennaas.extensions.abno.capability.linkprovisioning;

/*
 * #%L
 * OpenNaaS :: XIFI ABNO
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
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.abno.Activator;
import org.opennaas.extensions.abno.capability.linkprovisioning.api.ProvisionLinkRequest;

/**
 * ABNO {@link ILinkProvisioningCapability} implementation
 * 
 * @author Julio Carlos Barrera
 *
 */
public class LinkProvisioningCapability extends AbstractCapability implements ILinkProvisioningCapability {

	private static final Log	log			= LogFactory.getLog(LinkProvisioningCapability.class);

	private String				resourceId	= "";

	public LinkProvisioningCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Link Provisioning Capability");
	}

	@Override
	public void provisionLink(ProvisionLinkRequest provisionLinkRequest) throws CapabilityException {
		IAction action = createActionAndCheckParams(LinkProvisioningActionSet.PROVISION_LINK, provisionLinkRequest);

		ActionResponse response = executeAction(action);

		if (!response.getStatus().equals(ActionResponse.STATUS.OK)) {
			throw new ActionException(response.toString());
		}
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), ILinkProvisioningCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		super.deactivate();
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
		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getActionSetService(ILinkProvisioningCapability.CAPABILITY_TYPE, name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	private ActionResponse executeAction(IAction action) throws ActionException {
		log.debug("Executing action...");

		try {
			IProtocolManager protocolManager = getProtocolManagerService();
			IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(this.resourceId);

			// skip the queue and execute directly
			return action.execute(protocolSessionManager);
		} catch (ActivatorException e) {
			throw new ActionException("Error getting Protocol Manager service.", e);
		} catch (ProtocolException e) {
			throw new ActionException("Error getting Protocol Session Manager.", e);
		}
	}

	private IProtocolManager getProtocolManagerService() throws ActivatorException {
		return Activator.getProtocolManagerService();
	}

}
