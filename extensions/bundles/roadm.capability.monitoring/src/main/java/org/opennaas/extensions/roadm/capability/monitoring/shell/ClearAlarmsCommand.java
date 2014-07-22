package org.opennaas.extensions.roadm.capability.monitoring.shell;

/*
 * #%L
 * OpenNaaS :: ROADM :: Monitoring Capability
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
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.roadm.capability.monitoring.IMonitoringCapability;

/**
 * Lists alarms per resource
 * 
 * @author Carlos Baez
 */
@Command(scope = "alarms", name = "clear", description = "Clear all alarms fromgiven resources")
public class ClearAlarmsCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "A space delimited list of resource type and name.", required = true, multiValued = true)
	private List<String>	resourceIDs;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Clear alarms");
		try {
			ResourceManager manager = (ResourceManager) getResourceManager();
			for (String friendlyId : resourceIDs) {
				IResourceIdentifier resourceIdentifier = getResourceIdentifier(friendlyId, manager);
				IResource resource = manager.getResource(resourceIdentifier);
				IMonitoringCapability monitoringCapability = (IMonitoringCapability) resource.getCapabilityByInterface(IMonitoringCapability.class);
				monitoringCapability.clearAlarms();
				printInfo("Cleared alarms for resource: " + friendlyId);
			}
		} catch (Exception e) {
			printError("Error clearing alarms.");
		}
		printEndCommand();
		return null;

	}

	private IResourceIdentifier getResourceIdentifier(String friendlyName, IResourceManager resourceManager) throws Exception {
		String[] argsRouterName = new String[2];
		argsRouterName = splitResourceName(friendlyName);
		IResourceIdentifier identifier = resourceManager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
		return identifier;
	}
}
