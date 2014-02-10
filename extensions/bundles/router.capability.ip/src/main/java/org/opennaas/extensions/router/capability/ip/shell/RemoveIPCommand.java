package org.opennaas.extensions.router.capability.ip.shell;

/*
 * #%L
 * OpenNaaS :: Router :: IP Capability
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
import org.opennaas.extensions.router.capability.ip.IIPCapability;

@Command(scope = "ip", name = "removeIP", description = "Removes an IP address from a given interface of a resource")
public class RemoveIPCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id, owning the interface", required = true, multiValued = false)
	private String	resourceId;
	@Argument(index = 1, name = "interface", description = "The name of the interface to be setted.", required = true, multiValued = false)
	private String	interfaceName;
	@Argument(index = 2, name = "ip", description = "A valid IPv4 or IPv6 address to remove : x.x.x.x/x or x:x:x:x:x:x:x:x/x", required = true, multiValued = false)
	private String	ipAddress;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Remove IP address");

		try {
			IResource resource = getResourceFromFriendlyName(resourceId);

			IIPCapability ipCapability = (IIPCapability) resource.getCapabilityByInterface(IIPCapability.class);
			ipCapability.removeIP(interfaceName, ipAddress);

		} catch (Exception e) {
			printError("Error removing ip address from an interface.");
			printError(e);
			printEndCommand();
			return null;
		}

		printEndCommand();

		return null;
	}
}
