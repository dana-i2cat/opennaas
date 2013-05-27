package org.opennaas.extensions.router.capability.chassis.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.ComputerSystem;

@Command(scope = "chassis", name = "deleteLogicalRouter", description = "Delete a logical router on a given resource.")
public class DeleteLogicalRouterCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource owning the Logical Router to delete.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "logicalRouterId", description = "The logical router to be deleted.", required = true, multiValued = false)
	private String	logicalRouterName;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("delete logical router");

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
			if (resourceIdentifier == null) {
				printError("Could not get resource with name: " + argsRouterName[0] + ":" + argsRouterName[1]);
				printEndCommand();
				return -1;
			}

			IResource resource = manager.getResource(resourceIdentifier);

			validateResource(resource);

			IChassisCapability chassisCapability = (IChassisCapability) resource.getCapabilityByInterface(IChassisCapability.class);
			chassisCapability.deleteLogicalRouter(prepareParams());

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error listing interfaces.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

	private ComputerSystem prepareParams() {
		ComputerSystem lr = new ComputerSystem();
		lr.setName(logicalRouterName);
		lr.setElementName(logicalRouterName);

		return lr;
	}
}
