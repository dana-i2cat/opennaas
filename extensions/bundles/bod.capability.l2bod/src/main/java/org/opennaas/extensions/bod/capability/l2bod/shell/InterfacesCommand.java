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

import static com.google.common.collect.Iterables.filter;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.topology.Interface;

@Command(scope = "l2bod", name = "interfaces", description = "Show interfaces of BoD resource.")
public class InterfacesCommand extends GenericKarafCommand
{
	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception
	{
		printInitCommand("l2bod:interfaces");

		try {
			IResource resource = getResourceFromFriendlyName(resourceId);

			NetworkModel model = (NetworkModel) resource.getModel();
			for (Interface i : filter(model.getNetworkElements(), Interface.class)) {
				printInfo(i.getName() + " (" + i + ")");
			}
		} catch (Exception e) {
			printError("Error listing interfaces of  " + resourceId);
			printError(e);
		} finally {
			printEndCommand();
		}
		return null;
	}

}