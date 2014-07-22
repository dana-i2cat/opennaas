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

import java.util.HashMap;
import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.descriptor.Information;

/**
 * Show the information of one or more Resources
 * 
 * @author Evelyn Torras
 * @author Julio Carlos Barrera
 * 
 */
@Command(scope = "resource", name = "info", description = "Provides extended information about one or more resources.")
public class InfoResourcesCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "A space delimited list of resource type and name.", required = true, multiValued = true)
	private List<String>	resourceIDs;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("print resource information");

		try {

			for (String id : resourceIDs) {
				IResource resource = null;
				try {
					resource = getResourceFromFriendlyName(id);
				} catch (ResourceException e) {
					printError("Error getting respurce with name: " + id, e);
					continue;
				}

				Information information = resource.getResourceDescriptor().getInformation();
				printInfo("Resource Id: " + resource.getResourceIdentifier().getId());
				printInfo("Resource Name: " + information.getName());
				printInfo("State: " + resource.getState());
				printSymbol(horizontalSeparator);
				printInfo("Resource descriptor ");
				printInfo(doubleTab + "Description: " + information.getDescription());
				printInfo(doubleTab + "Type: " + information.getType());

				if (resource.getResourceDescriptor().getProfileId() != null && !resource.getResourceDescriptor().getProfileId().isEmpty()) {
					printInfo("Profile ID: " + resource.getResourceDescriptor().getProfileId());
				}
				if (resource.getResourceDescriptor().getProfileId() != null && !resource.getResourceDescriptor().getProperties().isEmpty()) {
					HashMap<String, String> properties = (HashMap<String, String>) resource.getResourceDescriptor().getProperties();
					if (!properties.isEmpty()) {
						printInfo("Properties: ");
						for (String key : properties.keySet()) {
							printInfo(doubleTab + key + ":" + properties.get(key));
						}
					}
				}
				// printSymbol(horizontalSeparator);
				printInfo("Instantiated capabilities:");
				for (ICapability capability : resource.getCapabilities()) {
					// show capabilities instantiated in this resource
					printInfo(indexArrowRigth + simpleTab + "Name: " + capability.getCapabilityInformation().getName());
					printInfo(doubleTab + "Description: " + capability.getCapabilityInformation().getDescription());
					printInfo(doubleTab + "Type: " + capability.getCapabilityInformation().getType());
				}
			}
		} catch (Exception e) {
			printError(e);
			printError(e.getMessage());
			printError(e.getLocalizedMessage());
			printError("Error showing information of resource.");
		}
		printEndCommand();
		return null;
	}
}
