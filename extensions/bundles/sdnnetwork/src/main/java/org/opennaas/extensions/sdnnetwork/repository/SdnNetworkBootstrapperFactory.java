package org.opennaas.extensions.sdnnetwork.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class SdnNetworkBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {
		return new SdnNetworkBootstrapper();
	}

}
