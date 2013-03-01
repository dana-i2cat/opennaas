package org.opennaas.extensions.vcpe.repository;

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
