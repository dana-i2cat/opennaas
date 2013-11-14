package org.opennaas.extensions.vrf.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

/**
 * 
 * @author Josep Batallé (josep.batalle@i2cat.net)
 * 
 */
public class VRFBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {
		return new VRFBootstrapper();
	}

}
