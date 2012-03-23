package org.opennaas.extensions.network.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

public class NetworkBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {
		// TODO Auto-generated method stub
		return new NetworkBootstrapper();
	}

}
