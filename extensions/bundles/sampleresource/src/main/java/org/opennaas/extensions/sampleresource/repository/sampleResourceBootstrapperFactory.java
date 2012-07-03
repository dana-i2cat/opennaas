package org.opennaas.extensions.sampleresource.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class sampleResourceBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {

		return new sampleResourceBootstrapper();
	}

}
