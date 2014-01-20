package org.opennaas.extensions.vcpe.repository;

/*
 * #%L
 * OpenNaaS :: vCPENetwork
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

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

public class VCPENetBootstrapper implements IResourceBootstrapper {

	@Override
	public void bootstrap(Resource resource) throws ResourceException {

		VCPENetworkModel model = createEmptyModel(resource);

		if (((VCPENetworkDescriptor) resource.getResourceDescriptor()).getvCPEModel()
				!= null) {
			// load model from the one persisted in the descriptor
			model = loadModelFromDescriptor(
					(VCPENetworkDescriptor) resource.getResourceDescriptor());
		}
		resource.setModel(model);
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {
		// persist model into descriptor
		storeModelIntoDescriptor((VCPENetworkModel) resource.getModel(), (VCPENetworkDescriptor) resource.getResourceDescriptor());

		// reset the model
		resetModel(resource);
	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {
		resource.setModel(createEmptyModel(resource));
	}

	private VCPENetworkModel createEmptyModel(Resource resource) {
		VCPENetworkModel model = new VCPENetworkModel();
		model.setId(resource.getResourceIdentifier().getId());
		model.setName(resource.getResourceDescriptor().getInformation().getName());
		model.setCreated(false);
		return model;
	}

	private VCPENetworkModel loadModelFromDescriptor(VCPENetworkDescriptor descriptor)
			throws ResourceException {
		try {
			VCPENetworkModel model = (VCPENetworkModel) ObjectSerializer.fromXml(
					descriptor.getvCPEModel(), VCPENetworkModel.class);
			return model;
		} catch (SerializationException e) {
			throw new ResourceException(e);
		}
	}

	private VCPENetworkDescriptor storeModelIntoDescriptor(VCPENetworkModel model,
			VCPENetworkDescriptor descriptor) throws ResourceException {

		if (model == null) {
			descriptor.setvCPEModel(null);
		} else {
			try {
				descriptor.setvCPEModel(ObjectSerializer.toXml(model));
			} catch (SerializationException e) {
				throw new ResourceException(e);
			}
		}
		return descriptor;
	}

}
