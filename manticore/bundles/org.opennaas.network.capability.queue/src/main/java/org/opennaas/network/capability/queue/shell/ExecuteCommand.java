package org.opennaas.network.capability.queue.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.command.Response;
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
			QueueCapability queueCapability = (QueueCapability) getCapability(network.getCapabilities(), QueueCapability.CAPABILITY_NAME);
			Response response = queueCapability.execute();
			return printResponseStatus(response);
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error activating OSPF");
			printError(e);
			printEndCommand();
			return -1;
		}
	}
}