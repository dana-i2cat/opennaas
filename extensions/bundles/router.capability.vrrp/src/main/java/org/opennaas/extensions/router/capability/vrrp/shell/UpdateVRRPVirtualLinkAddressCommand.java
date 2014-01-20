package org.opennaas.extensions.router.capability.vrrp.shell;

/*
 * #%L
 * OpenNaaS :: Router :: VRRP Capability
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
import org.opennaas.extensions.router.capability.vrrp.IVRRPCapability;
import org.opennaas.extensions.router.model.VRRPGroup;

/**
 * 
 * @author Adrian Rosello
 * 
 */
@Command(scope = "vrrp", name = "updateVirtualLinkAddress", description = "Update VRRP group virtual link address")
public class UpdateVRRPVirtualLinkAddressCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to update the VRRP virtual IP address", required = true, multiValued =
			false)
	private String	resourceId;

	@Argument(index = 1, name = "vrrpGroupId", description = "The VRRP group ID", required = true, multiValued = false)
	private int		vrrpGroupId;

	@Argument(index = 2, name = "virtualLinkAddress", description = "The new VRRP virtual link address", required = true, multiValued = false)
	private String	virtualLinkAddress;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Update VRRP virtual link address");

		try {

			IResource router = getResourceFromFriendlyName(resourceId);

			IVRRPCapability vrrpCapability = (IVRRPCapability) router.getCapabilityByInterface(IVRRPCapability.class);

			VRRPGroup vrrpGroup = new VRRPGroup();
			vrrpGroup.setVrrpName(vrrpGroupId);
			vrrpGroup.setVirtualLinkAddress(virtualLinkAddress);

			vrrpCapability.updateVRRPVirtualLinkAddress(vrrpGroup);

			printEndCommand();
			return null;

		} catch (Exception e) {
			printError("Error updating VRRP virtual Link address.");
			printError(e);
			printEndCommand();
			return -1;
		}
	}

}
