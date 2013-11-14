package org.opennaas.extensions.vrf.capability.routing.shell;

import java.io.FileInputStream;
import java.io.InputStream;
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
@Command(scope = "routing", name = "insertFileRoute", description = "Insert Route from json file to a table.")
public class InsertFileRouteCommand extends GenericKarafCommand {

    @Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
    private String resourceName;
    @Argument(index = 1, name = "fileName", description = "Path and filename.", required = true, multiValued = false)
    private String fileName;
    
    @Override
    protected Object doExecute() throws Exception {
        printInitCommand("Insert Route Files");
        try {
            IResource resource = getResourceFromFriendlyName(resourceName);
            RoutingCapability capab = (RoutingCapability) resource.getCapabilityByType("routing");
            InputStream inputStream = new FileInputStream(fileName);
            Response response = capab.insertRouteFile(fileName, inputStream);
            printInfo("Inserting file... " + response.getStatus()+" - "+ response.getEntity());
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
