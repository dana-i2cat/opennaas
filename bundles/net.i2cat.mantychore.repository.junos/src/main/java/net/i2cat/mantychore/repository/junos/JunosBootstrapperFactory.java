package net.i2cat.mantychore.repository.junos;

import com.iaasframework.resources.core.IResourceBootstrapper;
import com.iaasframework.resources.core.IResourceBootstrapperFactory;

public class JunosBootstrapperFactory implements IResourceBootstrapperFactory {

	public IResourceBootstrapper createResourceBootstrapper() {
		return new JunosBootstrapper();
	}

}
