package org.opennaas.extensions.network.capability.ospf.shell;

/*
 * #%L
 * OpenNaaS :: Network :: OSPF Capability
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
import org.opennaas.extensions.network.capability.ospf.INetOSPFCapability;

@Command(scope = "netospf", name = "deactivate", description = "It will dactivate OSPF in all routers of the network resource.")
public class DeactivateCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "network:networkName", description = "The network where OSPF will be deactivated.", required = true, multiValued = false)
	private String	networkId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Deactivate OSPF ");
		try {
			IResource network = getResourceFromFriendlyName(networkId);
			INetOSPFCapability netOSPFCapability = (INetOSPFCapability) network.getCapabilityByInterface(INetOSPFCapability.class);
			netOSPFCapability.deactivateOSPF();
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error deactivating OSPF");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}
