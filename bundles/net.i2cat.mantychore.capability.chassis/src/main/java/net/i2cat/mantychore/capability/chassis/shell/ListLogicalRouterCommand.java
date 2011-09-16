package net.i2cat.mantychore.capability.chassis.shell;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.ManagedSystemElement;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "chassis", name = "listLogicalRouter", description = "List all logical resources of a given resource.")
public class ListLogicalRouterCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name to show the logical routers.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		initcommand("list logical router");

		try {
			IResourceManager manager = getResourceManager();

			if (!splitResourceName(resourceId))
				return null;

			IResourceIdentifier resourceIdentifier = null;

			resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
			if (resourceIdentifier == null) {
				printError("Error in identifier.");
				endcommand();
				return null;
			}

			IResource resource = manager.getResource(resourceIdentifier);

			validateResource(resource);

			// TODO implement force refresh of the router configuration
			// maybe asking (parser) only for logical router information
			//
			ComputerSystem model = (ComputerSystem) resource.getModel();
			//
			for (ManagedSystemElement systemElement : model.getManagedSystemElements()) {

				if (systemElement instanceof ComputerSystem) {
					ComputerSystem logicalrouter = (ComputerSystem) systemElement;

					printInfo(logicalrouter.getName());
				}
			}
			// printInfo("Found " + model.getChildren().size() + " logical resources.");
			// for (Object systemElement : model.getChildren()) {
			// printSymbol(bullet + " " + (String) systemElement);
			// if (systemElement instanceof ComputerSystem) {
			// ComputerSystem logicalrouter = (ComputerSystem) systemElement;
			// // check that the element is a Logical Router
			// printInfo(logicalrouter.getName());
			// }
			// }

		} catch (ResourceException e) {
			printError(e);
			endcommand();
			return null;
		} catch (Exception e) {
			printError("Error listing interfaces.");
			printError(e);
			endcommand();
			return null;
		}
		endcommand();
		return null;
	}
}
