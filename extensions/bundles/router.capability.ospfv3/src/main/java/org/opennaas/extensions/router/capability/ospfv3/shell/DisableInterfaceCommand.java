package org.opennaas.extensions.router.capability.ospfv3.shell;

/*
 * #%L
 * OpenNaaS :: Router :: OSPFv3 Capability
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
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.ospfv3.IOSPFv3Capability;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;

@Command(scope = "ospfv3", name = "disableInterface", description = "Disable OSPF in given interfaces")
public class DisableInterfaceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to apply this command on", required = true, multiValued = false)
	private String			resourceId;

	@Argument(index = 1, name = "interfaceName", description = "Name of the interface(s) to disable OSPFv3 on", required = true, multiValued = true)
	private List<String>	interfaceNames;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Disable OSPFv3 on interface(s) ");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);

			// FIXME Cannot read model to get OSPFProtocolEndpoints and their OSPFArea.
			// model may not be updated :S

			List<OSPFProtocolEndpoint> ospfPeps = new ArrayList<OSPFProtocolEndpoint>(interfaceNames.size());
			OSPFProtocolEndpoint pep;
			for (String ifaceName : interfaceNames) {
				pep = new OSPFProtocolEndpoint();
				pep.setName(ifaceName);
				ospfPeps.add(pep);
			}

			IOSPFv3Capability ospfCapability = (IOSPFv3Capability) router.getCapabilityByInterface(IOSPFv3Capability.class);
			ospfCapability.disableOSPFv3Interfaces(ospfPeps);
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error disabling OSPFv3 in interfaces(s)");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}
