package net.i2cat.mantychore.queuemanager.shell;

import java.util.List;

import net.i2cat.mantychore.queuemanager.QueueManager;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.command.Response;
import net.i2cat.nexus.resources.queue.QueueConstants;
import net.i2cat.nexus.resources.queue.QueueResponse;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

@Command(scope = "queue", name = "execute", description = "Execute all actions in queue")
public class ExecuteCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Resource name of the owner queue", required = true, multiValued = false)
	private String	resourceId;

	@Option(name = "--debug", aliases = { "-d" }, description = "Get all the debug info.")
	private boolean	debug;

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
			if (queue == null) {
				printError("Could not found capability " + QueueManager.QUEUE + " in resource " + resourceId);
				return -1;
			}
			
			printSymbol("Executing actions...");
			QueueResponse queueResponse = (QueueResponse) queue.sendMessage(QueueConstants.EXECUTE, null);
			printSymbol("Executed in " + queueResponse.getTotalTime() + " ms");

			if (debug) {
				printDebug(queueResponse);
			} else {
				printOverview(queueResponse);
			}

		} catch (Exception e) {
			printError("Error getting queue.");
			printError(e);
			endcommand();
			return -1;
		}
		endcommand();
		return null;
	}

	private void printDebug(QueueResponse queueResponse) {
		if (queueResponse.getRestoreResponse().getStatus() != ActionResponse.STATUS.PENDING) {
			printSymbol("WARNING IT WAS NECESARY TO RESTORE THE CONFIGURATION!!");
			printActionResponseExtended(queueResponse.restoreResponse);
		}
		newLine();
		printActionResponseExtended(queueResponse.getPrepareResponse());
		newLine();
		for (ActionResponse actionResponse : queueResponse.getResponses()) {
			printActionResponseExtended(actionResponse);
			newLine();
		}

		printActionResponseExtended(queueResponse.getConfirmResponse());

	}

	private void printOverview(QueueResponse queueResponse) {

		if (queueResponse.getRestoreResponse().getStatus() != ActionResponse.STATUS.PENDING) {
			printSymbol("WARNING IT WAS NECESARY TO RESTORE THE CONFIGURATION!!");
			printActionResponseBrief(queueResponse.restoreResponse);
		}
		newLine();
		printActionResponseBrief(queueResponse.getPrepareResponse());
		newLine();
		for (ActionResponse actionResponse : queueResponse.getResponses()) {
			printActionResponseBrief(actionResponse);
			newLine();
		}

		printActionResponseBrief(queueResponse.getConfirmResponse());

	}

	private void printActionResponseBrief(ActionResponse actionResponse) {
		printSymbol("--- actionid: " + actionResponse.getActionID() + " status: " + actionResponse.getStatus() + " ---");
		List<Response> responses = actionResponse.getResponses();
		/* create new action */
		String[] titles = { "Command Name", "Status" };
		String[][] matrix = new String[responses.size()][2];
		int num = 0;
		for (Response response : responses) {
			String commandName = response.getCommandName();
			String params[] = { commandName, response.getStatus().toString() };
			matrix[num] = params;
			num++;
		}

		super.printTable(titles, matrix, -1);

	}

	private void printActionResponseExtended(ActionResponse actionResponse) {
		printSymbol("--- actionid: " + actionResponse.getActionID() + " status: " + actionResponse.getStatus() + " ---");
		List<Response> responses = actionResponse.getResponses();
		/* create new action */
		for (Response response : responses) {
			printSymbol("Command: " + response.getCommandName());
			printSymbol("Message: " + response.getSentMessage());
			newSeparator();
			printSymbol("Information: " + response.getInformation());
		}

	}

//	public ICapability getCapability(List<ICapability> capabilities, String type) throws Exception {
//		for (ICapability capability : capabilities) {
//			if (capability.getCapabilityInformation().getType().equals(type)) {
//				return capability;
//			}
//		}
//		throw new Exception("Error getting the capability");
//	}
//
//	private boolean validateResource(IResource resource) throws ResourceException {
//		if (resource == null)
//			throw new ResourceException("No resource found.");
//		if (resource.getModel() == null)
//			throw new ResourceException("The resource didn't have a model initialized. Start the resource first.");
//		if (resource.getCapabilities() == null) {
//			throw new ResourceException("The resource didn't have the capabilities initialized. Start the resource first.");
//		}
//		return true;
//	}

}
