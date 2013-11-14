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
@Command(scope = "routing", name = "getPath", description = "Get Path of route.")
public class GetPathCommand extends GenericKarafCommand {

    @Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
    private String resourceName;
    @Argument(index = 1, name = "ipSource", description = "Ip Source.", required = true, multiValued = false)
    private String ipSource;
    @Argument(index = 2, name = "ipDest", description = "Destination IP", required = true, multiValued = false)
    private String ipDest;
    @Argument(index = 3, name = "switchip", description = "Ip of the Switch", required = true, multiValued = false)
    private String switchip;
    @Argument(index = 4, name = "inputPort", description = "Input Port of the Switch.", required = true, multiValued = false)
    private int inputPort;
    @Argument(index = 5, name = "mechanism", description = "Mechanism (Proactive or reactive).", required = true, multiValued = false)
    private boolean mechanism;

    @Override
    protected Object doExecute() throws Exception {
        printInitCommand("Get Path");
        try {
            IResource resource = getResourceFromFriendlyName(resourceName);
            RoutingCapability capab = (RoutingCapability) resource.getCapabilityByType("routing");
            Response response = capab.getRoute(ipSource, ipDest, switchip, inputPort, mechanism);
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
