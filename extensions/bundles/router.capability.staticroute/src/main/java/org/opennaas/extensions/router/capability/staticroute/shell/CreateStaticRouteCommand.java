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
import org.opennaas.extensions.router.capability.staticroute.StaticRouteCapability;

//
/**
 * @author Jordi Puig
 */
@Command(scope = "staticroute", name = "create", description = "Create a static route in given device")
public class CreateStaticRouteCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to create the static route", required = true, multiValued =
			false)
	private String	resourceId;

	@Argument(index = 1, name = "netIdIpAdress", description = "The net id ip address", required = true, multiValued =
			false)
	private String	netIdIpAdress;

	@Argument(index = 2, name = "nextHopIpAddress", description = "The next hop ip address", required = false, multiValued =
			false)
	private String	nextHopIpAddress;

	@Argument(index = 3, name = "isRejected", description = "Choose if is discard", required = false, multiValued = false)
	private String	isDiscard;

	@Argument(index = 4, name = "preference", description = "Routing option preference.", required = false, multiValued = false)
	private int		preference	= StaticRouteCapability.PREFERENCE_DEFAULT_VALUE;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Create Static Route");

		// FIXME check either nextHopIpAddress or isRejected are set
		try {
			IResource router = getResourceFromFriendlyName(resourceId);

			IStaticRouteCapability staticRouteCapability = (IStaticRouteCapability) router.getCapabilityByInterface(IStaticRouteCapability.class);
			staticRouteCapability.createStaticRoute(netIdIpAdress, nextHopIpAddress, isDiscard, preference);

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error creating Static route.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;

	}

}