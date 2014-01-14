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
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.ospf.IOSPFCapability;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;

/**
 * @author Isart Canyameres
 */
@Command(scope = "ospf", name = "enableInterface", description = "Enable OSPF in given interfaces")
public class EnableInterfaceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to apply this command on", required = true, multiValued = false)
	private String			resourceId;

	@Argument(index = 1, name = "interfaceName", description = "Name of the interface(s) to enable OSPF on", required = true, multiValued = true)
	private List<String>	interfaceNames;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Enable OSPF on interface(s) ");
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

			IOSPFCapability ospfCapability = (IOSPFCapability) router.getCapabilityByInterface(IOSPFCapability.class);
			ospfCapability.enableOSPFInterfaces(ospfPeps);

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error enabling OSPF in interface(s)");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}