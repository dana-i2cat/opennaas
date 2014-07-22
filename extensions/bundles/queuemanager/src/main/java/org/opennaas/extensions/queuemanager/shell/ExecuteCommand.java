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
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.queuemanager.QueueManager;

@Command(scope = "queue", name = "execute", description = "Execute all actions in queue")
public class ExecuteCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the resource owning the queue", required = true, multiValued = false)
	private String	resourceId;

	@Option(name = "--debug", aliases = { "-d" }, description = "Print execution data verbosely.")
	private boolean	debug;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("Execute all actions in queue");

		IQueueManagerCapability queue;
		try {
			IResource resource = getResourceFromFriendlyName(resourceId);

			queue = (IQueueManagerCapability) getCapability(resource.getCapabilities(), QueueManager.QUEUE);
			if (queue == null) {
				printError("Could not found capability " + QueueManager.QUEUE + " in resource " + resourceId);
				return -1;
			}

		} catch (Exception e) {
			printError("Error getting queue.");
			printError(e);
			printEndCommand();
			return -1;
		}

		try {
			printSymbolWithoutDoubleLine("Executing queue... ");
			QueueResponse queueResponse = queue.execute();
			if (queueResponse.isOk())
				printSymbol("OK");
			else
				printSymbol("ERROR");
			printSymbol("Elapsed time " + queueResponse.getTotalTime() + " ms");
			printSymbol("Queue Report:");
			newLine();
			if (debug) {
				printDebug(queueResponse);
			} else {
				printOverview(queueResponse);
			}
			newLine();

		} catch (Exception e) {
			printSymbol(""); // to start a new line
			printError("Error executing queue.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

	private void printDebug(QueueResponse queueResponse) {
		if (queueResponse.getRestoreResponse().getStatus() != ActionResponse.STATUS.PENDING) {
			printSymbol("WARNING IT WAS NECESARY TO RESTORE THE CONFIGURATION!!");
			printActionResponseExtended(queueResponse.restoreResponse);
		}
		// newLine();
		printActionResponseExtended(queueResponse.getPrepareResponse());
		// newLine();
		for (ActionResponse actionResponse : queueResponse.getResponses()) {
			printActionResponseExtended(actionResponse);
			// newLine();
		}

		printActionResponseExtended(queueResponse.getConfirmResponse());
		printActionResponseExtended(queueResponse.getRefreshResponse());

	}

	private void printOverview(QueueResponse queueResponse) {

		if (queueResponse.getRestoreResponse().getStatus() != ActionResponse.STATUS.PENDING) {
			printSymbol("WARNING IT WAS NECESARY TO RESTORE THE CONFIGURATION!!");
			printActionResponseBrief(queueResponse.restoreResponse);
		}
		// newLine();
		printActionResponseBrief(queueResponse.getPrepareResponse());
		// newLine();
		for (ActionResponse actionResponse : queueResponse.getResponses()) {
			printActionResponseBrief(actionResponse);
			// newLine();
		}

		printActionResponseBrief(queueResponse.getConfirmResponse());
		printActionResponseBrief(queueResponse.getRefreshResponse());

	}

	private void printActionResponseBrief(ActionResponse actionResponse) {
		printSymbol("--- actionid: " + actionResponse.getActionID() + ", status: " + actionResponse.getStatus() + " ---");
		if (actionResponse.getInformation() != null) {
			printSymbol("Information: " + actionResponse.getInformation());
		}
		List<Response> responses = actionResponse.getResponses();
		/* create new action */
		String[] titles = { "Command Name", "Status" };
		String[][] matrix = new String[responses.size()][2];
		int num = 0;
		for (Response response : responses) {
			String commandName = response.getCommandName();
			String params[] = { commandName, response.getStatus().toString() };
			matrix[num] = params;
			num++;
		}
		super.printTable(titles, matrix, -1);
	}

	private void printActionResponseExtended(ActionResponse actionResponse) {
		printSymbol("--- actionid: " + actionResponse.getActionID() + ", status: " + actionResponse.getStatus() + " ---");
		if (actionResponse.getInformation() != null) {
			printSymbol("Information: " + actionResponse.getInformation());
		}
		List<Response> responses = actionResponse.getResponses();
		/* create new action */
		for (Response response : responses) {
			printSymbol("Command: " + response.getCommandName());
			printSymbol("Status: " + response.getStatus().toString());

			printSymbol("Message: " + response.getSentMessage());
			printSymbol("Information: " + response.getInformation());
			for (String error : response.getErrors()) {
				printSymbol("Error: " + error);
			}
			printSymbol("");
		}
	}

}
