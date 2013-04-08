package org.opennaas.extensions.vnmapper.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class VNMapperResourceBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {

		return new VNMapperResourceBootstrapper();
	}

}
