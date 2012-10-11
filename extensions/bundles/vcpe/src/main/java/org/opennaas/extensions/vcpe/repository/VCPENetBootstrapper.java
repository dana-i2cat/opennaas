package org.opennaas.extensions.vcpe.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;
import org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilder;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class VCPENetBootstrapper implements IResourceBootstrapper {

	@Override
	public void bootstrap(Resource resource) throws ResourceException {

		// load model from the one persisted in the descriptor
		VCPENetworkModel model = loadModelFromDescriptor(
				(VCPENetworkDescriptor) resource.getResourceDescriptor());
		resource.setModel(model);

		// FIXME scenario should be created upon request (by calling capability)
		// not during bootstrap when resource is starting
		VCPENetworkModel currentModel = buildDesiredScenario(resource);
		resource.setModel(currentModel);
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		// FIXME scenario should be destroyed upon request (by calling capability)
		// not when resource is stopping
		unbuildScenario(resource);
		resetModel(resource);

		// persist model into descriptor
		storeModelIntoDescriptor((VCPENetworkModel) resource.getModel(), (VCPENetworkDescriptor) resource.getResourceDescriptor());
	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {
		resource.setModel(new VCPENetworkModel());
	}

	private VCPENetworkModel loadModelFromDescriptor(VCPENetworkDescriptor descriptor)
			throws ResourceException {

		if (descriptor.getvCPEModel() == null)
			return null;

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

	// TODO REMOVE
	private VCPENetworkModel buildDesiredScenario(Resource resource) throws ResourceException {

		// simulating vCPENetwork factory with a HARDCODED model
		VCPENetworkModel desiredScenario = VCPENetworkModelHelper.generateSampleModel();

		IVCPENetworkBuilder capab = (IVCPENetworkBuilder) resource.getCapabilityByInterface(IVCPENetworkBuilder.class);
		return capab.buildVCPENetwork(desiredScenario);
	}

	// TODO REMOVE
	private void unbuildScenario(Resource resource) throws ResourceException {
		IVCPENetworkBuilder capab = (IVCPENetworkBuilder) resource.getCapabilityByInterface(IVCPENetworkBuilder.class);
		capab.destroyVCPENetwork();
	}

}
