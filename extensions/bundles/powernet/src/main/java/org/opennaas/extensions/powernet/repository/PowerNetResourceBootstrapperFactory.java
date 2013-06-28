package org.opennaas.extensions.powernet.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class PowerNetResourceBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {
		return new PowerNetResourceBootstrapper();
	}

}
