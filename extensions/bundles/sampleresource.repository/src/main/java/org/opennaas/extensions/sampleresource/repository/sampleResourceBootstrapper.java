package org.opennaas.extensions.sampleresource.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.domain.NetworkDomain;

public class sampleResourceBootstrapper implements IResourceBootstrapper {

	Log				log	= LogFactory.getLog(sampleResourceBootstrapper.class);

	private IModel	oldModel;

	public void bootstrap(IResource resource) throws ResourceException {

		log.info("Loading bootstrap to start resource...");

		// Add here all the necessary methods to populate resource model
		//
	}

	@Override
	public void resetModel(IResource resource) throws ResourceException {

		NetworkDomain networkDomain = new NetworkDomain();
		ResourceDescriptor resourceDescriptor = resource.getResourceDescriptor();
		Information information = resourceDescriptor.getInformation();

		networkDomain.setName(information.getName());

		NetworkModel networkModel = new NetworkModel();
		networkModel.getNetworkElements().add(networkDomain);

		resource.setModel(new NetworkModel());
	}

	@Override
	public void revertBootstrap(IResource resource) throws ResourceException {

		resource.setModel(oldModel);
	}

}
