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
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.VLANEndpoint;
import org.opennaas.extensions.router.model.utils.ModelHelper;

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

			ComputerSystem model = (ComputerSystem) resource.getModel();

			List<NetworkPort> interfaces = ModelHelper.getInterfaces(model);
			printInterfaces(interfaces);

			List<ProtocolEndpoint> grePEPs = ModelHelper.getGREProtocolEndpoints(model);
			printGREPEPsAsInterfaces(grePEPs);

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

	private void printInterfaces(List<NetworkPort> interfaces) {

		for (NetworkPort netPort : interfaces) {

			int portNumber = netPort.getPortNumber();
			String ifaceName = netPort.getName() + "." + String.valueOf(portNumber);

			if (ifaceName.length() < 15) {
				int dif = 15 - ifaceName.length();
				for (int i = 0; i < dif; i++)
					ifaceName += " ";
			}
			printSymbolWithoutDoubleLine("INTERFACE: " + ifaceName);

			if (netPort instanceof LogicalTunnelPort)
				printSymbolWithoutDoubleLine(doubleTab + "Peer-Unit: " + ((LogicalTunnelPort) netPort).getPeer_unit());

			if (netPort.getProtocolEndpoint() != null) {

				for (ProtocolEndpoint pE : netPort.getProtocolEndpoint()) {
					if (pE instanceof VLANEndpoint) {
						printSymbolWithoutDoubleLine(doubleTab + "VLAN id: " + Integer
								.toString(((VLANEndpoint) pE).getVlanID()));
					}
				}
				printSymbolWithoutDoubleLine(doubleTab + "STATE: " + netPort.getOperationalStatus());

			}

			if (netPort.getDescription() != null && !netPort.getDescription().equals("")) {
				printSymbolWithoutDoubleLine(doubleTab + "description: " + netPort.getDescription());
			}

			printSymbol("");
		}

	}

	private void printGREPEPsAsInterfaces(List<ProtocolEndpoint> grePEPs) {
		for (ProtocolEndpoint pE : grePEPs) {

			printSymbolWithoutDoubleLine("GRE INTERFACE: " + pE.getName());

			printSymbolWithoutDoubleLine(doubleTab + "STATE: " + pE.getOperationalStatus());

			if (pE.getDescription() != null && !pE.getDescription().equals(""))
				printSymbolWithoutDoubleLine(doubleTab + "description: " + pE.getDescription());
			printSymbol("");
		}
	}
}
