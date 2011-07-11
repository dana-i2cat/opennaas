package net.i2cat.mantychore.queuemanager.shell;

import java.util.List;

import net.i2cat.mantychore.queuemanager.QueueManager;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.queue.ModifyParams;
import net.i2cat.nexus.resources.queue.QueueConstants;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

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
