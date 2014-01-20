package org.opennaas.extensions.network.capability.ospf.shell.completers;

/*
 * #%L
 * OpenNaaS :: Network :: OSPF Capability
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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.StringsCompleter;
import org.opennaas.core.resources.Activator;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;

public class ResourceNameCompleter implements Completer {

	Log	log	= LogFactory.getLog(ResourceNameCompleter.class);

	@Override
	public int complete(String arg0, int arg1, List<String> arg2) {

		IResourceManager resourceManager = null;
		List<IResource> list = null;
		StringsCompleter delegate = new StringsCompleter();

		try {
			resourceManager = Activator.getResourceManagerService();
			list = resourceManager.listResourcesByType("network");

			for (IResource resource : list) {
				String value = resource.getResourceDescriptor().getInformation().getType() + ":" + resource.getResourceDescriptor().getInformation()
						.getName();
				delegate.getStrings().add(value);
			}

		} catch (Exception e) {
			log.error(e);
		}

		return delegate.complete(arg0, arg1, arg2);
	}
}
