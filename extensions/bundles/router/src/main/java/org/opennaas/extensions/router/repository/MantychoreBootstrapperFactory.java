package org.opennaas.extensions.router.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

public class MantychoreBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {
		// TODO Auto-generated method stub
		return new MantychoreBootstrapper();
	}

}
