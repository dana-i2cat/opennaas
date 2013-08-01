package org.opennaas.extensions.quantum.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class QuantumResourceBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {
		return new QuantumResourceBootstrapper();
	}

}
