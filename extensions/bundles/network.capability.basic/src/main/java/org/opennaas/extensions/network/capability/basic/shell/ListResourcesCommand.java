package org.opennaas.extensions.network.capability.basic.shell;

/*
 * #%L
 * OpenNaaS :: Network :: Basic capability
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
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.NetworkElement;

@Command(scope = "net", name = "listResources", description = "List resource of the network")
public class ListResourcesCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The network where to add a resource", required = true, multiValued = false)
	private String	networkId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("list resources in network");

		// load network
		IResource network;
		try {
			network = getResourceFromFriendlyName(networkId);
		} catch (Exception e) {
			printError("Failed to get required resources: " + e.getLocalizedMessage());
			printEndCommand();
			return null;
		}

		// get and print network elements
		NetworkModel networkModel = (NetworkModel) network.getModel();
		for (NetworkElement netElem : NetworkModelHelper.getNetworkElementsExceptTransportElements(networkModel)) {
			printNetworkElement(netElem);
		}

		printEndCommand();
		return null;
	}

	private void printNetworkElement(NetworkElement netElem) {
		printSymbol(netElem.getName() + "\n");
	}
}
