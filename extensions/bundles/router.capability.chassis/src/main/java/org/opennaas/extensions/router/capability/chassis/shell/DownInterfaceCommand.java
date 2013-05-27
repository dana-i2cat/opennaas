package org.opennaas.extensions.router.capability.chassis.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.chassis.IChassisCapability;
import org.opennaas.extensions.router.model.LogicalPort;

@Command(scope = "chassis", name = "down", description = "Down a physical interface")
public class DownInterfaceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Id of the resource owning the interface", required = true, multiValued = false)
	private String	resourceId;
	@Argument(index = 1, name = "interface", description = "The name of the interface to set down", required = true, multiValued = false)
	private String	interfaceName;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("DOWN interface");

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
			chassisCapability.downPhysicalInterface(prepareConfigureStatusParams(interfaceName));

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error setting down interfaces.");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

	private LogicalPort prepareConfigureStatusParams(String nameInterface) {
		LogicalPort logicalPort = new LogicalPort();
		logicalPort.setName(nameInterface);
		return logicalPort;
	}
}
