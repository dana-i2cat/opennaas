package org.opennaas.extensions.power.capabilities.shell;

/*
 * #%L
 * OpenNaaS :: Power :: Capabilities
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
import org.opennaas.extensions.power.capabilities.IPowerManagementCapability;

@Command(scope = "power", name = "getPowerStatus", description = "Reads power status of a specified consumer")
public class GetPowerStatusCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("getPowerStatus of resource: " + resourceId);

		IResource resource = getResourceFromFriendlyName(resourceId);
		boolean status = ((IPowerManagementCapability) resource.getCapabilityByInterface(IPowerManagementCapability.class))
				.getPowerStatus();

		printStatus(status);

		printEndCommand();
		return null;

	}

	private void printStatus(boolean status) {
		if (status)
			printSymbol("Powered On");
		else
			printSymbol("Powered Off");
	}

}
