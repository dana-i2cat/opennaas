package org.opennaas.extensions.vcpe.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;
import org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilder;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

public class VCPENetBootstrapper implements IResourceBootstrapper {

	@Override
	public void bootstrap(Resource resource) throws ResourceException {

		VCPENetworkModel model = loadModelFromDescriptor(
				(VCPENetworkDescriptor) resource.getResourceDescriptor());
		resource.setModel(model);

		VCPENetworkModel currentModel = buildDesiredScenario(resource);
		resource.setModel(currentModel);
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		unbuildScenario(resource);
		resetModel(resource);
	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {
		resource.setModel(new VCPENetworkModel());
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
		try {
			descriptor.setvCPEModel(ObjectSerializer.toXml(model));
			return descriptor;
		} catch (SerializationException e) {
			throw new ResourceException(e);
		}

	}

	private VCPENetworkModel buildDesiredScenario(Resource resource) throws ResourceException {
		IVCPENetworkBuilder capab = (IVCPENetworkBuilder) resource.getCapabilityByInterface(IVCPENetworkBuilder.class);
		return capab.buildVCPENetwork((VCPENetworkModel) resource.getModel());
	}

	private void unbuildScenario(Resource resource) throws ResourceException {
		IVCPENetworkBuilder capab = (IVCPENetworkBuilder) resource.getCapabilityByInterface(IVCPENetworkBuilder.class);
		capab.destroyVCPENetwork();
	}

}
