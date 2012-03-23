package org.opennaas.extensions.roadm.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

public class ROADMBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {
		// TODO Auto-generated method stub
		return new ROADMBootstrapper();
	}

}
