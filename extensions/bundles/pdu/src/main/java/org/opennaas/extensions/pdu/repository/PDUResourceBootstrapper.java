package org.opennaas.extensions.pdu.repository;

/*
 * #%L
 * OpenNaaS :: PDU Resource
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
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.pdu.capability.AbstractNotQueueingCapability;
import org.opennaas.extensions.pdu.model.PDUModel;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class PDUResourceBootstrapper implements IResourceBootstrapper {

	Log				log	= LogFactory.getLog(PDUResourceBootstrapper.class);

	private IModel	oldModel;

	@Override
	public void bootstrap(Resource resource) throws ResourceException {
		log.info("Loading bootstrap to start resource...");
		oldModel = resource.getModel();
		resetModel(resource);
		// Add here all the necessary methods to populate resource model

		PDU pdu = new PDU();
		pdu.setName(resource.getResourceDescriptor().getInformation().getName());
		((PDUModel) resource.getModel()).setPdu(pdu);

		try {
			for (ICapability capability : resource.getCapabilities()) {
				if (capability instanceof AbstractNotQueueingCapability) {
					((AbstractNotQueueingCapability) capability).resyncModel();
				}
			}
		} catch (Exception e) {
			throw new ResourceException("Error during resource startup. Failed to execute capabilities resyncModel.", e);
		}

	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {
		resource.setModel(new PDUModel());
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		// remove pdu from GIModel
		// PDU pdu = ((PDUModel) resource.getModel()).getPdu();
		// getGIModel().getDeliveries().remove(pdu);

		((PDUModel) resource.getModel()).setPdu(null);
		resource.setModel(oldModel);
	}

}
