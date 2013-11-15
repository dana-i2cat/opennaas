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
@Command(scope = "routing", name = "deleteRoute", description = "Delete specific route.")
public class DeleteRouteCommand extends GenericKarafCommand {

    @Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
    private String resourceName;
    @Argument(index = 1, name = "ipSource", description = "Id of the route or the Source IP", required = true, multiValued = false)
    private String ipSource;
    @Argument(index = 2, name = "ipDest", description = "IP version of the route or the Destination IP", required = true, multiValued = false)
    private String ipDest;
    @Argument(index = 3, name = "switchDPID", description = "DPID of the Switch", required = false, multiValued = false)
    private String switchDPID;
    @Argument(index = 4, name = "inputPort", description = "Input Port of the Switch.", required = false, multiValued = false)
    private int inputPort;
    @Argument(index = 5, name = "outputPort", description = "Output Port of the Switch.", required = false, multiValued = false)
    private int outputPort;

    @Override
    protected Object doExecute() throws Exception {
        printInitCommand("Delete Route");
        try {
            IResource resource = getResourceFromFriendlyName(resourceName);
            RoutingCapability capab = (RoutingCapability) resource.getCapabilityByType("routing");
            Response response;
            if(switchDPID == null){
                printInitCommand("Delete Route "+ipSource+" from table IPv"+ipDest);
                response = capab.removeRoute(Integer.parseInt(ipSource), Integer.parseInt(ipDest));
            }else{
                response = capab.removeRoute(ipSource, ipDest, switchDPID, inputPort, outputPort);
            }
            printInfo("Deletting... " + response.getStatus()+" - "+ response.getEntity());
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
