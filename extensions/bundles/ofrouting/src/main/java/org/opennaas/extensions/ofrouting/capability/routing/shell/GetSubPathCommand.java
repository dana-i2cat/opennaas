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
@Command(scope = "routing", name = "getSubPath", description = "Get Path of route.")
public class GetSubPathCommand extends GenericKarafCommand {

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

    @Override
    protected Object doExecute() throws Exception {
        printInitCommand("Get Path");
        try {
            IResource resource = getResourceFromFriendlyName(resourceName);
            RoutingCapability capab = (RoutingCapability) resource.getCapabilityByType("routing");
            int greeting = capab.getPath(ipSource, ipDest, switchip, inputPort);
            printInfo("The outputport is: " + greeting);
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
