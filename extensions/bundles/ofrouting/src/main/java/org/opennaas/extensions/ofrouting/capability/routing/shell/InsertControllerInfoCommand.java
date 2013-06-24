package org.opennaas.extensions.ofrouting.capability.routing.shell;

import javax.ws.rs.core.Response;
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
@Command(scope = "routing", name = "insertController", description = "Insert controller.")
public class InsertControllerInfoCommand extends GenericKarafCommand {

    @Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
    private String resourceName;
    @Argument(index = 1, name = "ipController", description = "Ip Source.", required = true, multiValued = false)
    private String ipSource;
    @Argument(index = 2, name = "portController", description = "Destination IP", required = true, multiValued = false)
    private String ipDest;
    @Argument(index = 3, name = "switchMac", description = "Mac of the Switch", required = true, multiValued = false)
    private String switchMac;

    @Override
    protected Object doExecute() throws Exception {
        printInitCommand("Get Path");
        try {
            IResource resource = getResourceFromFriendlyName(resourceName);
            RoutingCapability capab = (RoutingCapability) resource.getCapabilityByType("routing");
            Response greeting = capab.putSwitchController(ipSource, ipDest, switchMac);
            printInfo("The outputport is: " + greeting.getStatus());
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
