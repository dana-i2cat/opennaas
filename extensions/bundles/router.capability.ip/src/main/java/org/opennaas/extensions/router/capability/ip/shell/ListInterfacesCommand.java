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

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ModelElementNotFoundException;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.ip.IIPCapability;

@Command(scope = "ip", name = "list", description = "List all the interfaces of a given resource.")
public class ListInterfacesCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name to show the interfaces.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {
		try {
			IResource resource = getResourceFromFriendlyName(resourceId);
			validateResource(resource);

			IIPCapability ipCapability = (IIPCapability) resource.getCapabilityByInterface(IIPCapability.class);
			List<String> interfacesNames = ipCapability.getInterfacesNames().getInterfaces();

			// print ifaces & its ip address
			for (String ifaceName : interfacesNames) {
				List<String> ipAddresses = null;

				try {
					ipAddresses = ipCapability.getIPs(ifaceName).getIpAddresses();
				} catch (ModelElementNotFoundException e) {
					// ignore, not all interfaces can have IPProtocolEndpoints associated, like GRE
					continue;
				}

				printSymbolWithoutDoubleLine("[" + ifaceName + "]  ");

				if (ipAddresses != null && !ipAddresses.isEmpty()) {
					String description = ipCapability.getDescription(ifaceName);
					if (description != null && !description.isEmpty()) {
						printSymbolWithoutDoubleLine(doubleTab + "description: " + description);
					}

					printSymbol("");

					for (String ipAddress : ipAddresses) {
						printSymbol(doubleTab + "IP/MASK: " + ipAddress);
					}
				} else {
					printSymbol("");
				}

			}

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error listing interfaces.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}
