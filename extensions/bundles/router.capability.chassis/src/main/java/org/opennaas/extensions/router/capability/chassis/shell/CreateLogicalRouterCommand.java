package org.opennaas.extensions.router.capability.chassis.shell;

/*
 * #%L
 * OpenNaaS :: Router :: Chassis Capability
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
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfacesNamesList;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;

@Command(scope = "chassis", name = "createLogicalRouter", description = "Create a logical router on a given resource.")
public class CreateLogicalRouterCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Host resource name.", required = true, multiValued = false)
	private String			resourceId;

	@Argument(index = 1, name = "logicalRouter", description = "Name of the logical router to be created.", required = true, multiValued = false)
	private String			lrNname;

	@Argument(index = 2, name = "subinterfaces", description = "Optional list of subinterfaces to transfere to new logical router", required = false, multiValued = true)
	private List<String>	subinterfaces;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("create Logical Router");

		try {
			IResource resource = getResourceFromFriendlyName(resourceId);

			IChassisCapability chassisCapability = (IChassisCapability) resource.getCapabilityByInterface(IChassisCapability.class);

			InterfacesNamesList interfacesNamesList = null;
			if (subinterfaces != null && !subinterfaces.isEmpty()) {
				interfacesNamesList = new InterfacesNamesList();
				interfacesNamesList.setInterfaces(subinterfaces);
			}

			chassisCapability.createLogicalRouter(lrNname, interfacesNamesList);

		} catch (Exception e) {
			printError("Error creating Logical router.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

}
