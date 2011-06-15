package net.i2cat.mantychore.repository;

import net.i2cat.nexus.resources.IResourceBootstrapper;
import net.i2cat.nexus.resources.IResourceBootstrapperFactory;

public class MantychoreBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {
		// TODO Auto-generated method stub
		return new MantychoreBootstrapper();
	}

}
