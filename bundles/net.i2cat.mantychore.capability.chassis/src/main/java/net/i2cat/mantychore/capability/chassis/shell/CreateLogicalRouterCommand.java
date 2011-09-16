package net.i2cat.mantychore.capability.chassis.shell;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "chassis", name = "createLogicalRouter", description = "Create a logical router on a given resource.")
public class CreateLogicalRouterCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "logicalRouter", description = "The logical router to be created.", required = true, multiValued = false)
	private String	LRname;

	@Override
	protected Object doExecute() throws Exception {
		initcommand("create  Logical Router");

		try {
			IResourceManager manager = getResourceManager();

			if (!splitResourceName(resourceId))
				return -1;

			IResourceIdentifier resourceIdentifier = null;

			resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
			if (resourceIdentifier == null) {
				printError("Error in identifier.");
				endcommand();
				return -1;
			}
			IResource resource = manager.getResource(resourceIdentifier);
			validateResource(resource);
			ICapability chassisCapability = getCapability(resource.getCapabilities(), ChassisCapability.CHASSIS);
			printInfo("Sending message to the queue");

			chassisCapability.sendMessage(ActionConstants.CREATELOGICALROUTER, LRname);

		} catch (ResourceException e) {
			printError(e);
			endcommand();
			return -1;
		} catch (Exception e) {
			printError("Error creating Logical router.");
			printError(e);
			endcommand();
			return -1;
		}
		endcommand();
		return null;
	}

}
