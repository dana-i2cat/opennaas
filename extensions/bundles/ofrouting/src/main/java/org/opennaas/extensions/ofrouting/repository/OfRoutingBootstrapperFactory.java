package org.opennaas.extensions.ofrouting.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

/**
 * 
 * @author josep
 * 
 */
public class OfRoutingBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {
		return new OfRoutingBootstrapper();
	}

}
