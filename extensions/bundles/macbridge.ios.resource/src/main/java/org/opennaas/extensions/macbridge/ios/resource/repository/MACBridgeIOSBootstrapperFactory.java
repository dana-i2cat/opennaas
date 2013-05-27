package org.opennaas.extensions.macbridge.ios.resource.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

public class MACBridgeIOSBootstrapperFactory implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {
		return new MACBridgeIOSBootstrapper();
	}

}
