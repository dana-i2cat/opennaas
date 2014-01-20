package org.opennaas.extensions.macbridge.ios.resource.repository;

/*
 * #%L
 * OpenNaaS :: MAC Bridge :: IOS Resource
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
import org.opennaas.core.resources.Activator;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceRepository;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;

public class MACBridgeIOSRepository extends ResourceRepository {

	Log	log	= LogFactory.getLog(MACBridgeIOSRepository.class);

	@Override
	protected void checkResourceCanBeStarted(IResource resource)
			throws ResourceException {
		checkResourceHasAnAssociatedContext(resource);
		super.checkResourceCanBeStarted(resource);
	}

	private void checkResourceHasAnAssociatedContext(IResource resource) throws ResourceException {
		IProtocolSessionManager sessionManager;
		try {
			sessionManager = getProtocolSessionManager(resource.getResourceDescriptor().getId());
			if (sessionManager.getRegisteredContexts().isEmpty()) {
				String name = resource.getResourceDescriptor().getInformation().getName();
				String type = resource.getResourceDescriptor().getInformation().getType();
				String resourceId = type + ":" + name;
				throw new ResourceException(
						"There is no session context for resource " + resourceId + ". A session context is needed for the resource to start.");
			}
		} catch (ResourceException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException("Error loading session manager: ", e);
		}
	}

	public MACBridgeIOSRepository(String resourceType) {
		super(resourceType);
	}

	public void capabilityFactoryAdded(ICapabilityFactory capabilityFactory) {
		log.info("Adding factory: " + capabilityFactory.getType());
		this.capabilityFactories.put(capabilityFactory.getType(), capabilityFactory);
	}

	public void capabilityFactoryDeleted(ICapabilityFactory capabilityFactory) {
		if (capabilityFactory != null) {
			log.info("Deleting factory: " + capabilityFactory.getType());
			this.capabilityFactories.remove(capabilityFactory.getType());
		}
	}

	private IProtocolSessionManager getProtocolSessionManager(String resourceId) throws Exception {
		IProtocolManager protocolManager = Activator.getProtocolManagerService();
		return protocolManager.getProtocolSessionManager(resourceId);
	}

}
