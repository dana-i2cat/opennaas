package net.i2cat.nexus.resources.shell;

import java.util.List;

import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.ResourceManager;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

/**
 * Start one or more resources
 * 
 * @author Scott Campbell (CRC)
 * 
 */
@Command(scope = "resource", name = "start", description = "Start one or more resources")
public class StartResourceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "A space delimited list of resource type:name to be started", required = true, multiValued = true)
	private List<String>	resourceIDs;

	@Override
	protected Object doExecute() throws Exception {
		initcommand("resource start");

		try {
			ResourceManager manager = (ResourceManager) getResourceManager();
			for (String id : resourceIDs) {

				if (!splitResourceName(id))
					return null;

				IResourceIdentifier identifier = null;
				try {
					identifier = manager.getIdentifierFromResourceName(args[0], args[1]);
					if (identifier != null) {
						printInfo("Starting Resource: "
								+ args[1]);
						manager.startResource(identifier);
						counter++;
						printInfo("Resource " + args[1] + " started.");
					} else {
						printError("The resource " + args[1] +
										" is not found on repository.");
					}
				} catch (ResourceException e) {
					printError(e);
					printError("Didn't started the resource " + args[1] + ". ");

				}

				printSymbol(horizontalSeparator);
			}
			printInfo("Started " + counter + " resource/s from " + resourceIDs.size());

		} catch (Exception e) {
			printError("An error occurred starting the resource.");
			printError(e);
		}
		endcommand();
		return null;

	}
}
