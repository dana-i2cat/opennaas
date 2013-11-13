package org.opennaas.extensions.rfv.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

/**
 * 
 * @author Josep Batallé (josep.batalle@i2cat.net)
 * 
 */
public class RFVBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {
		return new RFVBootstrapper();
	}

}
