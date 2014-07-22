package org.opennaas.extensions.router.capability.bgp.shell;

/*
 * #%L
 * OpenNaaS :: Router :: BGP Capability
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

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.bgp.BGPModelFactory;
import org.opennaas.extensions.router.capability.bgp.IBGPCapability;
import org.opennaas.extensions.router.model.ComputerSystem;

@Command(scope = "bgp", name = "configureBGP", description = "Configure BGP in a router")
public class ConfigureBGPCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:ParentResourceName", description = "Parent resource id, source of the transference", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "filePath", description = "Absolut path to properties file to load BGP from", required = true, multiValued = false)
	private String	filePath;

	@Override
	protected Object doExecute() throws Exception {

		try {

			BGPModelFactory factory = new BGPModelFactory(filePath);
			ComputerSystem model = factory.createRouterWithBGP();

			IResource sourceResource = getResourceFromFriendlyName(resourceId);

			IBGPCapability chassisCapability = (IBGPCapability) sourceResource.getCapabilityByInterface(IBGPCapability.class);
			chassisCapability.configureBGP(model);

		} catch (Exception e) {
			printError(e);
			printEndCommand();
			return -1;
		}

		return null;
	}

}
