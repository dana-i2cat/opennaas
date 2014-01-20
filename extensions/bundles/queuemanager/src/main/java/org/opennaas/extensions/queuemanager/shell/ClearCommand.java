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

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.queuemanager.QueueManager;

@Command(scope = "queue", name = "clear", description = "Removes of queued actions of the queue.")
public class ClearCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the resource owning the queue", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("Removing all actions of the queue");

		try {

			IResource resource = getResourceFromFriendlyName(resourceId);
			validateResource(resource);

			QueueManager queue = (QueueManager) getCapability(resource.getCapabilities(), QueueManager.QUEUE);
			queue.clear();

			printInfo("Removed all actions from the queue");

		} catch (Exception e) {
			printError(e.getMessage());
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;

	}
}