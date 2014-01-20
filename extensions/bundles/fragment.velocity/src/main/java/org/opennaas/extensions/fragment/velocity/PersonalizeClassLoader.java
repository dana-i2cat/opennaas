package org.opennaas.extensions.fragment.velocity;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet :: Velocity Fragment
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
