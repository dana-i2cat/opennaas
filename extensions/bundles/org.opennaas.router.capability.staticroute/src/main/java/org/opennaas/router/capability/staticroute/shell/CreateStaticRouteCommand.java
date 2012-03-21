package org.opennaas.router.capability.staticroute.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.router.capability.staticroute.StaticRouteCapability;

//
/**
 * @author Jordi Puig
 */
@Command(scope = "staticroute", name = "create", description = "Create a static route in given device")
public class CreateStaticRouteCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to create the static route", required = true, multiValued =
			false)
	private String	resourceId;

	@Argument(index = 1, name = "netIdIpAdress", description = "The net id ip address", required = true, multiValued =
			false)
	private String	netIdIpAdress;

	@Argument(index = 2, name = "maskIpAdress", description = "The mask id ip address", required = true, multiValued =
			false)
	private String	maskIpAdress;

	@Argument(index = 3, name = "nextHopIpAddress", description = "The next hop ip address", required = true, multiValued =
			false)
	private String	nextHopIpAddress;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Create the static route");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);
			StaticRouteCapability staticRouteCapability = (StaticRouteCapability) getCapability(router.getCapabilities(),
					StaticRouteCapability.CAPABILITY_NAME);
			Response response = staticRouteCapability.create(netIdIpAdress, maskIpAdress, nextHopIpAddress);
			return printResponseStatus(response);
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error creating the static route");
			printError(e);
			printEndCommand();
			return -1;
		}
	}
}