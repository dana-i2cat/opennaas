package org.opennaas.extensions.vcpe.repository;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.Resource;
import org.opennaas.core.resources.ResourceException;

public class VCPENetBootstrapper implements IResourceBootstrapper {

	@Override
	public void bootstrap(Resource resource) throws ResourceException {

		IModel desiredModel = loadModelFromDescriptor();
		IModel currentModel = createRealVCPENetFromModel(desiredModel);
		resource.setModel(currentModel);
	}

	@Override
	public void revertBootstrap(Resource resource) throws ResourceException {

		destroyVCPENetFromModel(resource.getModel());
		resetModel(resource);
	}

	@Override
	public void resetModel(Resource resource) throws ResourceException {
		// TODO Auto-generated method stub

	}

	private IModel createRealVCPENetFromModel(IModel model) {
		// TODO Auto-generated method stub
		return null;
	}

	private void destroyVCPENetFromModel(IModel model) {
		// TODO Auto-generated method stub

	}

	private IModel loadModelFromDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

}
