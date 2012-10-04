package org.opennaas.extensions.vcpe.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

public class VCPENetBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {
		return new VCPENetBootstrapper();
	}

}
