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
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.ip.IIPCapability;

@Command(scope = "ip", name = "setIP", description = "Set an IP address in a given interface of a resource")
public class SetIPCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id, owning the interface", required = true, multiValued = false)
	private String	resourceId;
	@Argument(index = 1, name = "interface", description = "The name of the interface to be setted.", required = true, multiValued = false)
	private String	interfaceName;
	@Argument(index = 2, name = "ip", description = "A valid IPv4 or IPv6 address : x.x.x.x/x or x:x:x:x:x:x:x:x/x", required = true, multiValued = false)
	private String	ipAddress;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("set IP address");

		try {
			IResource resource = getResourceFromFriendlyName(resourceId);

			IIPCapability ipCapability = (IIPCapability) resource.getCapabilityByInterface(IIPCapability.class);
			ipCapability.setIP(interfaceName, ipAddress);

		} catch (CapabilityException ce) {
			printError(ce);
			printError("Remember that IP format must match one of the following constraints: ");
			printError("Ipv4: IPv4 Address + Net mask. Example : 144.156.12.1/24");
			printError("Ipv6: IPv6 Address + Prefix Length. Example: A::43A:B41/64");
			printEndCommand();
			return null;
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return null;
		} catch (Exception e) {
			printError("Error setting ip address in an interface.");
			printError(e);
			printEndCommand();
			return null;
		}
		printEndCommand();
		return null;
	}
}
