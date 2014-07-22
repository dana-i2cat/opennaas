package org.opennaas.extensions.router.capability.staticroute.shell;

/*
 * #%L
 * OpenNaaS :: Router :: Static route capability
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
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.staticroute.IStaticRouteCapability;

//
/**
 * @author Isart Canyameres
 */
@Command(scope = "staticroute", name = "delete", description = "Delete a static route in given device")
public class DeleteStaticRouteCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router this operation applies to", required = true, multiValued =
			false)
	private String	resourceId;

	@Argument(index = 1, name = "netIdIpAdress", description = "The net id ip address", required = true, multiValued =
			false)
	private String	netIdIpAdress;

	@Argument(index = 2, name = "nextHopIpAddress", description = "The next hop ip address", required = true, multiValued =
			false)
	private String	nextHopIpAddress;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Delete Static Route");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);

			IStaticRouteCapability staticRouteCapability = (IStaticRouteCapability) router.getCapabilityByInterface(IStaticRouteCapability.class);
			staticRouteCapability.deleteStaticRoute(netIdIpAdress, nextHopIpAddress);

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error deleting Static route.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;

	}
}