package org.opennaas.extensions.router.capability.topologydiscovery.shell;

/*
 * #%L
 * OpenNaaS :: Router :: Topology Discovery capability
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
import org.opennaas.extensions.router.capability.topologydiscovery.ITopologyDiscoveryCapability;
import org.opennaas.extensions.router.capability.topologydiscovery.model.LocalInformation;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@Command(scope = "topologydiscover", name = "getLogicalDeviceId", description = "Gets the local device id.")
public class GetLocalInformationCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to apply this command on", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Get Logical Device Id ");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);
			ITopologyDiscoveryCapability topologyCapab = (ITopologyDiscoveryCapability) router
					.getCapabilityByInterface(ITopologyDiscoveryCapability.class);

			LocalInformation localInformation = topologyCapab.getLocalInformation();

			printInfo("Device Id: \t" + localInformation.getDeviceId());

			for (String portId : localInformation.getInterfacesMap().keySet()) {
				StringBuilder sb = new StringBuilder();
				sb.append("\t PortId: \t").append(portId);
				sb.append("\t Interface Name: \t").append(localInformation.getInterfacesMap().get(portId));
				printSymbol(sb.toString());
			}

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		}

		return null;
	}
}
