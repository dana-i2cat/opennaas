package org.opennaas.network.capability.queue.shell;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.network.capability.queue.QueueCapability;

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
			Map<String, QueueResponse> response = queueCapability.execute();
			printNetQueueResponse(response);
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