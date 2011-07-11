package net.i2cat.mantychore.queuemanager.shell;

import java.util.List;

import net.i2cat.mantychore.queuemanager.QueueManager;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.action.IAction;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.queue.QueueConstants;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "queue", name = "listActions", description = "List all the actions which are included in the queue.")
public class ListActionsCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Resource name of the owner queue", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		initcommand("List actions in the queue");

		try {
			IResourceManager manager = getResourceManager();

			/* validate resource id */
			if (!splitResourceName(resourceId))
				return -1;

			IResourceIdentifier resourceIdentifier = null;
			resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);

			/* validate resource identifier */
			if (resourceIdentifier == null) {
				printError("Error in identifier.");
				endcommand();
				return -1;
			}

			IResource resource = manager.getResource(resourceIdentifier);
			validateResource(resource);
			ICapability queue = getCapability(resource.getCapabilities(), QueueManager.QUEUE);

			List<IAction> listActions = (List<IAction>) queue.sendMessage(QueueConstants.GETQUEUE, null);

			String[] titles = { "num", "actionID" };
			String[][] matrix = new String[listActions.size()][2];
			int num = 0;
			for (IAction action : listActions) {
				String actionID = action.getActionID();
				String params[] = { Integer.toString(num), actionID };
				matrix[num] = params;
				num++;
			}
			super.printTable(titles, matrix, -1);

		} catch (Exception e) {
			printError("Error getting queue.");
			printError(e);
			endcommand();
			return -1;
		}
		endcommand();
		return null;
	}

	public ICapability getCapability(List<ICapability> capabilities, String type) throws Exception {
		for (ICapability capability : capabilities) {
			if (capability.getCapabilityInformation().getType().equals(type)) {
				return capability;
			}
		}
		throw new Exception("Error getting the capability");
	}

	private boolean validateResource(IResource resource) throws ResourceException {
		if (resource == null)
			throw new ResourceException("No resource found.");
		if (resource.getModel() == null)
			throw new ResourceException("The resource didn't have a model initialized. Start the resource first.");
		if (resource.getCapabilities() == null) {
			throw new ResourceException("The resource didn't have the capabilities initialized. Start the resource first.");
		}
		return true;
	}

}
