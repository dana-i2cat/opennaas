package org.opennaas.extensions.ofrouting.capability.routing.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.ofrouting.capability.routing.RoutingCapability;

/**
 * 
 * @author josep
 * 
 */
@Command(scope = "routing", name = "getRouteTable", description = "Get Route table.")
public class GetRouteTableCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;
        
	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Get Route table");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			RoutingCapability capab = (RoutingCapability) resource.getCapabilityByType("routing");
			String greeting = capab.getRouteTable();
			printInfo("The json is: " + greeting);
		} catch (Exception e) {
			printError("Error greeting from resource " + resourceName);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}
}
