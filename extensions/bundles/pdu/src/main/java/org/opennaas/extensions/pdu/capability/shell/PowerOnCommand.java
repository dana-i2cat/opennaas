package org.opennaas.extensions.pdu.capability.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.pdu.capability.IPDUPowerManagementIDsCapability;

@Command(scope = "pdu", name = "powerOn", description = "Turns power on for specified port")
public class PowerOnCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id where given port is.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "port", description = "Port to power on", required = true, multiValued = false)
	private String	portId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("powerOn of port : " + portId + " in resource: " + resourceId);

		IResource resource = getResourceFromFriendlyName(resourceId);
		boolean result = ((IPDUPowerManagementIDsCapability) resource.getCapabilityByInterface(IPDUPowerManagementIDsCapability.class))
				.powerOn(portId);

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
