package org.opennaas.extensions.queuemanager.shell;

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