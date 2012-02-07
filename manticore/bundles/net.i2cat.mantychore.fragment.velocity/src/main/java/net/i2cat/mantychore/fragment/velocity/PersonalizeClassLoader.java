package net.i2cat.mantychore.fragment.velocity;

import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

public class PersonalizeClassLoader extends ResourceLoader {

	@Override
	public long getLastModified(Resource arg0) {
		return 0;
	}

	@Override
	public InputStream getResourceStream(String template)
			throws ResourceNotFoundException {
		return getClass().getResourceAsStream(template);
	}

	@Override
	public void init(ExtendedProperties arg0) {
		if (log.isTraceEnabled()) {
			log.trace("PersonalizeClassLoader : initialization complete.");
		}

	}

	@Override
	public boolean isSourceModified(Resource arg0) {
		return false;
	}

}
