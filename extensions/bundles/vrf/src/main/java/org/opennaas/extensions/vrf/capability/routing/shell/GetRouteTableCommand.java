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
@Command(scope = "routing", name = "getRouteTable", description = "Get Route table.")
public class GetRouteTableCommand extends GenericKarafCommand {

    @Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
    private String resourceName;
    @Argument(index = 1, name = "type", description = "Type of traffic IPv4 or IPv6", required = false, multiValued = false)
    private int type;

    @Override
    protected Object doExecute() throws Exception {
        printInitCommand("Get Entire Model");
        try {
            IResource resource = getResourceFromFriendlyName(resourceName);
            Response response;
            if(type != 0){
                RoutingCapability capab = (RoutingCapability) resource.getCapabilityByType("routing");
                 response = capab.getRoutes(type);
            }else{
                RoutingCapability capab = (RoutingCapability) resource.getCapabilityByType("routing");
                response = capab.getRoutes();
            }
            printInfo(response.getStatus()+" - The outputport is: " + response.getEntity());
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
