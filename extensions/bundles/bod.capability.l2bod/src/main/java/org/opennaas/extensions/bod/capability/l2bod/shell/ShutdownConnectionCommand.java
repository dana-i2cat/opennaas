package org.opennaas.extensions.bod.capability.l2bod.shell;

/*
 * #%L
 * OpenNaaS :: BoD :: L2BoD capability
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

import java.util.NoSuchElementException;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.bod.capability.l2bod.BoDLink;
import org.opennaas.extensions.bod.capability.l2bod.IL2BoDCapability;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;

@Command(scope = "l2bod", name = "shutdownConnection", description = "Shutdown L2 connectivity between specified interfaces.")
public class ShutdownConnectionCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id to shutdown the connectivity.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "connectionName", description = "The name of the link to be connection", required = true, multiValued = false)
	private String	connectionName;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("shutdown connectivity of resource: " + resourceId + " and link: " + connectionName);

		try {
			IResource resource = getResourceFromFriendlyName(resourceId);
			IL2BoDCapability ipCapability = (IL2BoDCapability) resource.getCapabilityByInterface(IL2BoDCapability.class);
			ipCapability.shutDownConnection(getLink((NetworkModel) resource.getModel()));
		} catch (Exception e) {
			printError("Error shutting down connection in resource: " + resourceId);
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;

	}

	/**
	 * Get the link from the model. Uses connectionName argument to find it.
	 * 
	 * @param model
	 * @return Link representing the connection with given name in given model.
	 * @throws NoSuchElementException
	 *             if there is no Link in model named <code>connectionName</code>.
	 * 
	 */
	private BoDLink getLink(NetworkModel model) {
		int linkIndex = NetworkModelHelper.getNetworkElementByName(connectionName, model.getNetworkElements());
		if (linkIndex == -1) {
			throw new NoSuchElementException("No such link " + connectionName);
		}

		if (!(model.getNetworkElements().get(linkIndex) instanceof BoDLink)) {
			throw new NoSuchElementException("No such link " + connectionName);
		}

		return (BoDLink) model.getNetworkElements().get(linkIndex);
	}
}