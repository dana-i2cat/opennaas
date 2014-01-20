package org.opennaas.extensions.network.capability.queue.shell;

/*
 * #%L
 * OpenNaaS :: Network :: Queue Capability
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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.network.capability.queue.QueueCapability;
import org.opennaas.extensions.network.capability.queue.Response;

/**
 * @author Jordi Puig
 */
@Command(scope = "netqueue", name = "execute", description = "It will execute each resource queue")
public class ExecuteCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "network:networkName", description = "The network where execute the queues", required = true, multiValued = false)
	private String	networkId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Execute all queues");
		try {
			IResource network = getResourceFromFriendlyName(networkId);
			QueueCapability queueCapability = (QueueCapability) getCapability(network.getCapabilities(), QueueCapability.NETQUEUE_CAPABILITY_NAME);
			Response response = queueCapability.execute();
			printNetQueueResponse(response.getResponse());
			return null;
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error executing queues");
			printError(e);
			printEndCommand();
			return -1;
		}
	}

	/**
	 * The response is a Map with key = name of the resource and value QueueResponse of the resource<br>
	 * Iterate all the resources and print his queue response
	 * 
	 * @param response
	 */
	private void printNetQueueResponse(Map<String, QueueResponse> response) {
		Set<String> keySet = response.keySet();
		if (!keySet.isEmpty()) {
			printInfo("Executed " + keySet.size() + " queues.");
			Iterator<String> iterator = keySet.iterator();
			while (iterator.hasNext()) {
				String resource = iterator.next();
				QueueResponse queueResponse = response.get(resource);
				String state = queueResponse.isOk() ? "OK" : "ERROR";
				printInfo(resource + " --> State: " + state);
			}
			printInfo(underLine);
		}
	}
}