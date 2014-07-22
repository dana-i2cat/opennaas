package org.opennaas.extensions.bod.repository;

/*
 * #%L
 * OpenNaaS :: BoD :: Repository
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
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.domain.NetworkDomain;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;

public class BoDBootstrapper implements IResourceBootstrapper {

	Log				log	= LogFactory.getLog(BoDBootstrapper.class);

	private IModel	oldModel;

	public void bootstrap(Resource resource) throws ResourceException {

		log.info("Loading bootstrap to start resource...");
		oldModel = resource.getModel();
		resetModel(resource);

		/* start resource capabilities */
		for (ICapability capab : resource.getCapabilities()) {
			log.debug("Found a capability in the resource");
			/* abstract capabilities have to be initialized */
			if (capab instanceof AbstractCapability) {
				log.debug("Executing capabilities startup...");

				try {
					((AbstractCapability) capab).sendRefreshActions();
				} catch (CapabilityException e) {
					throw new ResourceException(
							"Failed to send refresActions of " + capab.getCapabilityInformation().getType(), e);
				}
			}
		}

		IQueueManagerCapability queueCapab = null;
		try {
			queueCapab = (IQueueManagerCapability) resource.getCapabilityByInterface(IQueueManagerCapability.class);
		} catch (ResourceException e) {
			// ignored
		}
		if (queueCapab != null) {
			try {
				QueueResponse response = queueCapab.execute();
				if (!response.isOk()) {
					throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.");
				}
			} catch (ProtocolException e) {
				throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.", e);
			} catch (ActionException e) {
				throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.", e);
			} catch (CapabilityException e) {
				throw new ResourceException("Error during capabilities startup. Failed to execute startUp actions.", e);
			}
		}

		if (resource.hasProfile()) {
			log.debug("Executing initModel from profile...");
			resource.getProfile().initModel(resource.getModel());
		}

	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {

		NetworkDomain networkDomain = new NetworkDomain();
		ResourceDescriptor resourceDescriptor = resource.getResourceDescriptor();
		Information information = resourceDescriptor.getInformation();

		networkDomain.setName(information.getName());

		NetworkModel networkModel = new NetworkModel();
		networkModel.getNetworkElements().add(networkDomain);

		resource.setModel(new NetworkModel());
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		resource.setModel(oldModel);
	}

}
