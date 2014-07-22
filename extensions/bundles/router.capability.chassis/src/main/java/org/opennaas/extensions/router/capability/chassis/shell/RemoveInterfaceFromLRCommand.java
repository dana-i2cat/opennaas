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

@Command(scope = "chassis", name = "removeInterfaceFromLR", description = "Remove an exitent subinterface from a logical router.")
public class RemoveInterfaceFromLRCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:ParentResourceName", description = "Parent resource id, target of the transference.", required = true, multiValued = false)
	private String			physicalResourceId;

	@Argument(index = 1, name = "resourceType:ChildResourceName", description = "Child resource id, source of the transference.", required = true, multiValued = false)
	private String			logicalResourceId;

	@Argument(index = 2, name = "interfaces", description = "The names list of the interfaces to be removed.", required = true, multiValued = true)
	private List<String>	subinterfaces;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Remove an interface from a child logical router");

		try {
			IResource sourceResource = getResourceFromFriendlyName(physicalResourceId);

			InterfacesNamesList interfacesNamesList = null;
			if (subinterfaces != null && !subinterfaces.isEmpty()) {
				interfacesNamesList = new InterfacesNamesList();
				interfacesNamesList.setInterfaces(subinterfaces);
			}

			IChassisCapability chassisCapability = (IChassisCapability) sourceResource.getCapabilityByInterface(IChassisCapability.class);
			chassisCapability.removeInterfacesFromLogicalRouter(splitResourceName(logicalResourceId)[1], interfacesNamesList);

		} catch (Exception e) {
			printError(e);
			printEndCommand();
			return -1;
		}

		printEndCommand();
		return null;
	}

}
