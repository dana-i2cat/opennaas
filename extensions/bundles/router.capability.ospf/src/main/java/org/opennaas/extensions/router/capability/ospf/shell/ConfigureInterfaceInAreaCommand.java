package org.opennaas.extensions.router.capability.ospf.shell;

/*
 * #%L
 * OpenNaaS :: Router :: OSPF capability
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

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfacesNamesList;
import org.opennaas.extensions.router.capability.ospf.IOSPFCapability;

/**
 * @author Isart Canyameres
 */
@Command(scope = "ospf", name = "configureInterfaceInArea", description = "Configure interface in OSPF area")
public class ConfigureInterfaceInAreaCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to apply this command on", required = true, multiValued = false)
	private String			resourceId;

	@Argument(index = 1, name = "areaId", description = "OSPF area where to configure given interfaces.", required = true, multiValued = false)
	private String			areaId;

	@Argument(index = 2, name = "interfaceNames", description = "Interfaces to configure.", required = true, multiValued = true)
	private List<String>	interfaceNames;

	@Option(name = "--delete", aliases = { "-d" }, description = "Delete interfaces from given area, instead of adding them.")
	boolean					delete;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Configure interfaces in OSPF area ");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);

			// FIXME Cannot read model to get interfaces.
			// model may not be updated :S

			InterfacesNamesList ifaces = getInterfaces();

			IOSPFCapability ospfCapability = (IOSPFCapability) router.getCapabilityByInterface(IOSPFCapability.class);

			if (delete)
				ospfCapability.removeInterfacesInOSPFArea(areaId, ifaces);
			else
				ospfCapability.addInterfacesInOSPFArea(areaId, ifaces);

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error configuring interfaces in OSPF area");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

	private InterfacesNamesList getInterfaces() {
		InterfacesNamesList ifaces = new InterfacesNamesList();
		List<String> interfaces = new ArrayList<String>();
		interfaces.addAll(interfaceNames);
		ifaces.setInterfaces(interfaces);

		return ifaces;
	}

}
