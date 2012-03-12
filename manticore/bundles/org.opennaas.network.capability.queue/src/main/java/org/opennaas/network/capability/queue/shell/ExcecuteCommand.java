package org.opennaas.network.capability.queue.shell;

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
public class ExcecuteCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Execute all queues");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);
			QueueCapability queueCapability = (QueueCapability) getCapability(router.getCapabilities(), QueueCapability.CAPABILITY_NAME);
			QueueResponse response = queueCapability.execute();
			// TODO return printResponseStatus(response);
			return null;
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