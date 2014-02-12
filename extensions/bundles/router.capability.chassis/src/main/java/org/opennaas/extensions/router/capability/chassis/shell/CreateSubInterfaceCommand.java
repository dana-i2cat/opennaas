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
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capabilities.api.model.chassis.InterfaceInfo;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;

@Command(scope = "chassis", name = "createSubInterface", description = "Create a subinterface on a given resource.")
public class CreateSubInterfaceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "subInterface", description = "The interface to be created.", required = true, multiValued = false)
	private String	subinterface;

	@Option(name = "--description", aliases = { "-d" }, description = "interface description .")
	private String	description	= "";

	@Option(name = "--vlanid", aliases = { "-v" }, description = "specify vlan id to use vlan-tagging. IMPORTANT, ethernet interfaces need to include vlans")
	private int		vlanid		= -1;

	@Option(name = "--peerunit", aliases = { "-pu" }, description = "specify peer unit for lt interfaces.")
	private int		peerunit	= -1;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("create subInterface");

		try {
			IResource resource = getResourceFromFriendlyName(resourceId);
			IChassisCapability chassisCapability = (IChassisCapability) resource.getCapabilityByInterface(IChassisCapability.class);

			InterfaceInfo interfaceInfo = new InterfaceInfo();
			interfaceInfo.setName(subinterface);
			interfaceInfo.setDescription(description);
			interfaceInfo.setVlan(String.valueOf(vlanid));
			interfaceInfo.setPeerUnit(String.valueOf(peerunit));

			chassisCapability.createSubInterface(interfaceInfo);

		} catch (Exception e) {
			printError("Error configuring interfaces.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

}
