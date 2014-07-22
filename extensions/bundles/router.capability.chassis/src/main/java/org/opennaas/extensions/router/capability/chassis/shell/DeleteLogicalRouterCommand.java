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

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;

@Command(scope = "chassis", name = "deleteLogicalRouter", description = "Delete a logical router on a given resource.")
public class DeleteLogicalRouterCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource owning the Logical Router to delete.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "logicalRouterId", description = "The logical router to be deleted.", required = true, multiValued = false)
	private String	logicalRouterName;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("delete logical router");

		try {
			IResource resource = getResourceFromFriendlyName(resourceId);

			IChassisCapability chassisCapability = (IChassisCapability) resource.getCapabilityByInterface(IChassisCapability.class);
			chassisCapability.deleteLogicalRouter(logicalRouterName);

		} catch (Exception e) {
			printError("Error deleting logical router.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

}
