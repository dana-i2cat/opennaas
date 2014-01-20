package org.opennaas.extensions.network.capability.basic.shell;

/*
 * #%L
 * OpenNaaS :: Network :: Basic capability
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
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.network.capability.basic.INetworkBasicCapability;

@Command(scope = "net", name = "removeResource", description = "Remove a resource from the network")
public class RemoveResourceFromNetworkCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "network:networkName", description = "The network where to remove the resource from", required = true, multiValued = false)
	private String	networkId;

	@Argument(index = 1, name = "resourceType:resourceName", description = "The resource to be removed", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("add resource from network");

		// load resources
		IResource network;
		IResource resource;
		try {
			network = getResourceFromFriendlyName(networkId);
			resource = getResourceFromFriendlyName(resourceId);
		} catch (Exception e) {
			printError("Failed to get required resources: " + e.getLocalizedMessage());
			printEndCommand();
			return null;
		}

		INetworkBasicCapability networkCapability =
				(INetworkBasicCapability) network.getCapabilityByInterface(INetworkBasicCapability.class);

		try {
			networkCapability.removeResource(resource);
		} catch (CapabilityException e) {
			printError("Error deleting resource.");
			printError(e);
			printEndCommand();
			return null;
		}

		printInfo("Resource " + resourceId + " removed from network " + networkId);
		printEndCommand();
		return null;
	}

}