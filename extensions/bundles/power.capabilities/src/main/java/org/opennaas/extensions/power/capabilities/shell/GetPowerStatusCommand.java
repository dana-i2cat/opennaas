package org.opennaas.extensions.power.capabilities.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.power.capabilities.IPowerManagementCapability;

@Command(scope = "power", name = "getPowerStatus", description = "Reads power status of a specified consumer")
public class GetPowerStatusCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("getPowerStatus of resource: " + resourceId);

		IResource resource = getResourceFromFriendlyName(resourceId);
		boolean status = ((IPowerManagementCapability) resource.getCapabilityByInterface(IPowerManagementCapability.class))
				.getPowerStatus();

		printStatus(status);

		printEndCommand();
		return null;

	}

	private void printStatus(boolean status) {
		if (status)
			printSymbol("Powered On");
		else
			printSymbol("Powered Off");
	}

}
