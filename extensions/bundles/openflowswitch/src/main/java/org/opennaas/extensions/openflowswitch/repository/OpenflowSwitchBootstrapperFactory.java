package org.opennaas.extensions.openflowswitch.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class OpenflowSwitchBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {
		return new OpenflowSwitchBootstrapper();
	}

}
