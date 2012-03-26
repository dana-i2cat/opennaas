package org.opennaas.extensions.router.capability.chassis.shell;

import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.ManagedSystemElement;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;

@Command(scope = "chassis", name = "listLogicalRouters", description = "List all logical resources of a given resource.")
public class ListLogicalRoutersCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name to show the logical routers.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("list logical routers");

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
				return null;
			}

			IResource resource = manager.getResource(resourceIdentifier);

			validateResource(resource);

			// TODO implement force refresh of the router configuration
			// maybe asking (parser) only for logical router information
			//
			ComputerSystem model = (ComputerSystem) resource.getModel();

			// printInfo("Found " + model.getChildren().size() + " logical resources.");
			for (ManagedSystemElement systemElement : model.getManagedSystemElements()) {

				if (systemElement instanceof ComputerSystem) {

					printSymbol(systemElement.getName());
				}
			}

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return null;
		} catch (Exception e) {
			printError("Error listing logical routers.");
			printError(e);
			printEndCommand();
			return null;
		}
		printEndCommand();
		return null;
	}
}
