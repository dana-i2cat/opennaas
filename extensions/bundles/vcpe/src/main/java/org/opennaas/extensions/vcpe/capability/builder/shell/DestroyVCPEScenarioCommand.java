package org.opennaas.extensions.vcpe.capability.builder.shell;

/*
 * #%L
 * OpenNaaS :: vCPENetwork
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
import org.opennaas.extensions.vcpe.capability.builder.IVCPENetworkBuilderCapability;

@Command(scope = "vcpenet", name = "destroy", description = "Descroy a vCPE network scenario")
public class DestroyVCPEScenarioCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "vCPENetwork resource friendly name.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		IResource resource = getResourceFromFriendlyName(resourceId);

		IVCPENetworkBuilderCapability capability = (IVCPENetworkBuilderCapability) resource
				.getCapabilityByInterface(IVCPENetworkBuilderCapability.class);
		capability.destroyVCPENetwork();

		return null;
	}

}
