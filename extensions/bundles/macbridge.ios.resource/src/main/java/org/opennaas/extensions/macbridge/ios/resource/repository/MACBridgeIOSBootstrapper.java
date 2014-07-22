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
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;

public class MACBridgeIOSBootstrapper implements IResourceBootstrapper {
	Log		log	= LogFactory.getLog(MACBridgeIOSBootstrapper.class);
	IModel	oldModel;

	@Override
	public void resetModel(Resource resource) throws ResourceException {
		// Do nothing, don't want to reset the model (each refresh action updates)
		// the model whenever it is executed
	}

	@Override
	public void bootstrap(Resource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");
		if (resource.getModel() == null) {
			resource.setModel(new MACBridge());
		}

		/* start its capabilities */
		for (ICapability capab : resource.getCapabilities()) {
			/* abstract capabilities have to be initialized */
			log.debug("Found a capability in the resource.");
			/* abstract capabilities have to be initialized */
			if (capab instanceof AbstractCapability) {
				log.debug("Executing capabilities startup...");
				((AbstractCapability) capab).sendRefreshActions();
			}
		}

		IQueueManagerCapability queueCapab = (IQueueManagerCapability) resource.getCapabilityByInterface(IQueueManagerCapability.class);
		QueueResponse response;
		try {
			response = queueCapab.execute();
			if (!response.isOk()) {
				// TODO IMPROVE ERROR REPORTING
				throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.");
			}
		} catch (ProtocolException e) {
			throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.", e);
		} catch (ActionException e) {
			throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.", e);
		} catch (CapabilityException e) {
			throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.", e);
		}

		if (resource.getProfile() != null) {
			log.debug("Executing initModel from profile...");
			resource.getProfile().initModel(resource.getModel());
		}
	}

	public void createResource(MACBridgeIOSRepository repository, ResourceDescriptor descriptor) {
		/* Profile info is not cloned */
		// descriptor.setProfileId(profileName);
		IResource resource = null;
		try {
			log.info("Creating Resource ...... ");
			resource = repository.createResource(descriptor);
		} catch (ResourceException e) {
			log.error(e.getMessage());
		}
		log.info("Resource of type " + resource.getResourceDescriptor().getInformation().getType() + " created with name: "
				+ resource.getResourceDescriptor().getInformation().getName());
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {
		resource.setModel(oldModel);
	}
}
