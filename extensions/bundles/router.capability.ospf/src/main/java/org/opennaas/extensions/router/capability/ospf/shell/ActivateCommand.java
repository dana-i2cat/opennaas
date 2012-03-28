package org.opennaas.extensions.router.capability.ospf.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.ospf.OSPFCapability;

/**
 * @author Isart Canyameres
 */
@Command(scope = "ospf", name = "activate", description = "Activate OSPF in given device")
public class ActivateCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to activate OSPF on", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("Activate OSPF ");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);
			OSPFCapability ospfCapability = (OSPFCapability) getCapability(router.getCapabilities(), OSPFCapability.CAPABILITY_NAME);
			Response response = ospfCapability.activateOSPF();
			return printResponseStatus(response, resourceId);
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error activating OSPF");
			printError(e);
			printEndCommand();
			return -1;
		}
	}
}