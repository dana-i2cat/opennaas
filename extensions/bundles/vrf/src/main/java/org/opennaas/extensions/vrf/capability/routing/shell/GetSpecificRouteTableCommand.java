package org.opennaas.extensions.vrf.capability.routing.shell;

import javax.ws.rs.core.Response;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.vrf.capability.routing.RoutingCapability;

/**
 * 
 * @author Josep Batallé (josep.batalle@i2cat.net)
 * 
 */
@Command(scope = "routing", name = "getSpecRouteTable", description = "Get Route table.")
public class GetSpecificRouteTableCommand extends GenericKarafCommand {

    @Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
    private String resourceName;
    @Argument(index = 1, name = "type", description = "Type of traffic IPv4 or IPv6", required = true, multiValued = false)
    private int type;

    @Override
    protected Object doExecute() throws Exception {
        printInitCommand("Get Route table");
        try {
            IResource resource = getResourceFromFriendlyName(resourceName);
            RoutingCapability capab = (RoutingCapability) resource.getCapabilityByType("routing");
            Response response = capab.getRoutes(type);
            printInfo(response.getStatus()+" - The table in json is: " + response.getEntity());
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
