package org.opennaas.extensions.bod.repository;

import org.opennaas.core.resources.IResourceBootstrapper;
import org.opennaas.core.resources.IResourceBootstrapperFactory;

public class BoDBootstrapperFactory  implements IResourceBootstrapperFactory {

	@Override
	public IResourceBootstrapper createResourceBootstrapper() {

		return new BoDBootstrapper();
	}

}
