package org.opennaas.core.resources.shell;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IncorrectLifecycleStateException;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceManager;

/**
 * Start one or more resources
 * 
 * @author Scott Campbell (CRC)
 * 
 */
@Command(scope = "resource", name = "start", description = "Start one or more resources")
public class StartResourceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "A space delimited list of resource type:name to be started", required = true, multiValued = true)
	private List<String>	resourceIDs;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("resource start");
		int counter = 0;

		try {
			ResourceManager manager = (ResourceManager) getResourceManager();
			for (String id : resourceIDs) {

				String[] argsRouterName = new String[2];
				try {
					argsRouterName = splitResourceName(id);
				} catch (Exception e) {
					printError(e.getMessage());
					printEndCommand();
					return -1;
				}

				IResourceIdentifier identifier = null;
				try {
					identifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
					if (identifier != null) {
						// printInfo("Starting Resource: "+ argsRouterName[1]);
						manager.startResource(identifier);
						counter++;
						printInfo("Resource " + id + " started.");
					} else {
						printError("Resource " + id +
								" not found on repository.");
					}
				} catch (ResourceException e) {
					if (e.getCause() instanceof IncorrectLifecycleStateException)
						printError("Cannot start resource " + id + " from state: " + ((IncorrectLifecycleStateException) e.getCause())
								.getResourceState());
					else
						printError("Cannot start resource " + id + ": ", e);
				}
				// printSymbol(horizontalSeparator);
			}
			printInfo("Started " + counter + " resource/s of " + resourceIDs.size());

		} catch (Exception e) {
			printError("An error occurred starting the resource.");
			printError(e);
		}
		printEndCommand();
		return null;
	}
}
