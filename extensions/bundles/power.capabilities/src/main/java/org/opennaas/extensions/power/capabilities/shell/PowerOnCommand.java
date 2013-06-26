package org.opennaas.extensions.power.capabilities.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.pdu.capability.IPDUPowerManagementIDsCapability;
import org.opennaas.extensions.power.capabilities.IPowerManagementCapability;

@Command(scope = "power", name = "powerOn", description = "Turns power on for specified consumer")
public class PowerOnCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id.", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("powerOn of resource: " + resourceId);

		IResource resource = getResourceFromFriendlyName(resourceId);
		boolean result = ((IPowerManagementCapability) resource.getCapabilityByInterface(IPowerManagementCapability.class))
				.powerOn();

		printResult(result);

		printEndCommand();
		return null;

	}

	private void printResult(boolean result) {
		if (result)
			printSymbol("Powered On");
		else
			printSymbol("Not Powered On");
	}

}
