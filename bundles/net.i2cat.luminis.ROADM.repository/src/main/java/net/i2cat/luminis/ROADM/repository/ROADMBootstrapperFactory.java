package net.i2cat.luminis.ROADM.repository;

import net.i2cat.nexus.resources.IResourceBootstrapper;
import net.i2cat.nexus.resources.IResourceBootstrapperFactory;

public class ROADMBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {
		// TODO Auto-generated method stub
		return new ROADMBootstrapper();
	}

}
