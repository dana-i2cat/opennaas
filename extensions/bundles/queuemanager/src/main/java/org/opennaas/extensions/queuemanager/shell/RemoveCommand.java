package org.opennaas.extensions.queuemanager.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.queue.ModifyParams;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.queuemanager.QueueManager;

@Command(scope = "queue", name = "remove", description = "Remove an action from the queue")
public class RemoveCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the resource owning the queue", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "posQueue", description = "Position in the queue of the action to remove", required = true, multiValued = false)
	private int		posQueue;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("Remove action from queue");

		try {
			IResourceManager manager = getResourceManager();

			String[] argsRouterName = new String[2];
			try {
				argsRouterName = splitResourceName(resourceId);
			} catch (Exception e) {
				printError(e.getMessage());
				printEndCommand();
				return -1;
			}

			IResourceIdentifier resourceIdentifier = null;
			resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);

			/* validate resource identifier */
			if (resourceIdentifier == null) {
				printError("Could not get resource with name: " + argsRouterName[0] + ":" + argsRouterName[1]);
				printEndCommand();
				return -1;
			}

			IResource resource = manager.getResource(resourceIdentifier);
			validateResource(resource);
			IQueueManagerCapability queue = (IQueueManagerCapability) getCapability(resource.getCapabilities(), QueueManager.QUEUE);
			// printSymbol("Removing action " + posQueue + "...");
			ModifyParams params = ModifyParams.newRemoveOperation(posQueue);
			queue.modify(params);

		} catch (Exception e) {
			printError("Error removing action from queue.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}
