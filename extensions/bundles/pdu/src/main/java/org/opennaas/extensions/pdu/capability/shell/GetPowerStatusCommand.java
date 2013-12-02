package org.opennaas.extensions.pdu.capability.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.pdu.capability.IPDUPowerManagementIDsCapability;

@Command(scope = "pdu", name = "getPowerStatus", description = "Reads power status of a specified port")
public class GetPowerStatusCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id where given port is.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "port", description = "Port to read status from", required = true, multiValued = false)
	private String	portId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("getPowerStatus of port : " + portId + " in resource: " + resourceId);

		IResource resource = getResourceFromFriendlyName(resourceId);
		boolean status = ((IPDUPowerManagementIDsCapability) resource.getCapabilityByInterface(IPDUPowerManagementIDsCapability.class))
				.getPowerStatus(portId);

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
