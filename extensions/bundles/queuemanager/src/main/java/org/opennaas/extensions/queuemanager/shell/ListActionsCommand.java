package org.opennaas.extensions.queuemanager.shell;

/*
 * #%L
 * OpenNaaS :: Queue Manager
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
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.queuemanager.QueueManager;

@Command(scope = "queue", name = "listActions", description = "List all actions included in the queue.")
public class ListActionsCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the resource owning the queue", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("List actions in the queue");

		try {
			IResource resource = getResourceFromFriendlyName(resourceId);

			IQueueManagerCapability queue = (IQueueManagerCapability) getCapability(resource.getCapabilities(), QueueManager.QUEUE);
			if (queue == null) {
				printError("Could not found capability " + QueueManager.QUEUE + " in resource " + resourceId);
				return -1;
			}

			List<IAction> listActions = queue.getActions();

			String[] titles = { "num", "actionID" };
			String[][] matrix = new String[listActions.size()][2];
			int num = 0;
			for (IAction action : listActions) {
				String actionID = action.getActionID();
				String params[] = { Integer.toString(num), actionID };
				matrix[num] = params;
				num++;
			}
			printSymbol("Actions in queue of resource " + resourceId + ":");
			printTable(titles, matrix, -1);

		} catch (Exception e) {
			printError("Error getting queue.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}
