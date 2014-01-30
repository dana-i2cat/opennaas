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
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.wrappers.InterfaceInfo;
import org.opennaas.extensions.router.model.wrappers.InterfaceInfoList;

@Command(scope = "chassis", name = "showInterfaces", description = "List all interfaces of a given resource.")
public class ShowInterfacesCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name to show the interfaces.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("show interfaces information");

		try {

			IResource resource = getResourceFromFriendlyName(resourceId);
			validateResource(resource);

			IChassisCapability chassisCapability = (IChassisCapability) resource.getCapabilityByInterface(IChassisCapability.class);
			InterfaceInfoList interfacesInfoList = chassisCapability.getInterfacesInfo();

			printInterfacesInfo(interfacesInfoList);

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

	private void printInterfacesInfo(InterfaceInfoList interfaceInfoList) {

		for (InterfaceInfo interfaceInfo : interfaceInfoList.getInterfaceInfos()) {

			String ifaceName = interfaceInfo.getName();
			if (ifaceName.length() < 15) {
				int dif = 15 - ifaceName.length();
				for (int i = 0; i < dif; i++)
					ifaceName += " ";
			}
			printSymbolWithoutDoubleLine("INTERFACE: " + ifaceName);

			String peerUnit = interfaceInfo.getPeerUnit();
			if (peerUnit != null)
				printSymbolWithoutDoubleLine(doubleTab + "Peer-Unit: " + peerUnit);

			String vlan = interfaceInfo.getVlan();
			if (vlan != null) {
				printSymbolWithoutDoubleLine(doubleTab + "VLAN id: " + vlan);
			}

			String state = interfaceInfo.getState();
			if (state != null) {
				printSymbolWithoutDoubleLine(doubleTab + "STATE: " + state);

			}

			String description = interfaceInfo.getDescription();
			if (description != null && !description.isEmpty()) {
				printSymbolWithoutDoubleLine(doubleTab + "description: " + description);
			}

			printSymbol("");
		}

	}
}
