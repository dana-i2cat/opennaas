package org.opennaas.extensions.queuemanager.shell;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.queue.ModifyParams;
import org.opennaas.core.resources.queue.QueueConstants;
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

			ICapability queue = getCapability(resource.getCapabilities(), QueueManager.QUEUE);
			if (queue == null) {
				printError("Could not found capability " + QueueManager.QUEUE + " in resource " + resourceId);
				return -1;
			}

			List<IAction> listActions = (List<IAction>) queue.sendMessage(QueueConstants.GETQUEUE, null);

			int numActions = listActions.size();
			for (int i = 0; i < numActions; i++) {
				ModifyParams params = ModifyParams.newRemoveOperation(0);
				Response resp = (Response) queue.sendMessage(QueueConstants.MODIFY, params);
			}

			printInfo("Removed " + numActions + " actions from the queue");

		} catch (Exception e) {
			printError(e.getMessage());
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;

	}
}