package net.i2cat.mantychore.queuemanager.shell;

import java.util.List;

import net.i2cat.mantychore.queuemanager.QueueManager;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.queue.ModifyParams;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "queue", name = "remove", description = "Execute all actions in queue")
public class RemoveCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Resource name of the owner queue", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "posQueue", description = "Position in the queue", required = true, multiValued = false)
	private int		posQueue;

	@Override
	protected Object doExecute() throws Exception {

		initcommand("Execute all actions in queue");

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
			printSymbol("removing action " + posQueue + "...");
			ModifyParams params = ModifyParams.newRemoveOperation(posQueue);
			queue.sendMessage(QueueConstants.MODIFY, params);
			printSymbol("Executed!!!");

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

}
