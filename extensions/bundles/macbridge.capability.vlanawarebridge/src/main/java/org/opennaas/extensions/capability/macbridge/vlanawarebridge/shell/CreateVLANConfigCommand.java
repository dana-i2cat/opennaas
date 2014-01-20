package org.opennaas.extensions.capability.macbridge.vlanawarebridge.shell;

/*
 * #%L
 * OpenNaaS :: MAC Bridge :: VLAN-Aware Bridge capability
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
import org.opennaas.extensions.capability.macbridge.model.VLANConfiguration;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.IVLANAwareBridgeCapability;

/**
 * @author Eduard Grasa
 */
@Command(scope = "vlanawarebridge", name = "createvlanconfig", description = "Create a VLAN Configuration")
public class CreateVLANConfigCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the MAC bridge to create the VLAN configuration on", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "name", description = "Name that describes the VLAN", required = true, multiValued = false)
	private String	name;

	@Argument(index = 2, name = "vlanID", description = "ID of the VLAN", required = true, multiValued = false)
	private int		vlanID;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Create VLAN Configuration ");
		try {
			IResource macBridge = getResourceFromFriendlyName(resourceId);
			IVLANAwareBridgeCapability vlanAwareBridgeCapability =
					(IVLANAwareBridgeCapability) macBridge.getCapabilityByInterface(IVLANAwareBridgeCapability.class);
			vlanAwareBridgeCapability.createVLANConfiguration(new VLANConfiguration(name, vlanID));
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error creating VLAN Configuration");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}