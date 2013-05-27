package org.opennaas.core.resources.shell;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IncorrectLifecycleStateException;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.ResourceManager;

/**
 * Stop one or more resources
 *
 * @author Scott Campbell (CRC)
 *
 */
@Command(scope = "resource", name = "stop", description = "Stop one or more resources")
public class StopResourceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "A space delimited list of resource type:name to be stopped", required = true, multiValued = true)
	private List<String>	resourceIDs;

	@Option(name = "--force", aliases = { "-f" }, description = "Force stop resources")
	boolean					force;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("resource stop");
		int counter = 0;

		try {
			ResourceManager manager = (ResourceManager) getResourceManager();
			for (String id : resourceIDs) {

				String[] argsRouterName = new String[2];
				try {
					argsRouterName = splitResourceName(id);
				} catch (Exception e) {
					printError(e.getMessage());
					printEndCommand();
					return -1;
				}

				IResourceIdentifier identifier = null;
				try {
					identifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
					if (identifier != null) {
						// printInfo("Stopping Resource...");

						if (!force) {
							manager.stopResource(identifier);
						} else {
							// printInfo("Forcing resource to stop");
							manager.forceStopResource(identifier);
						}

						counter++;
						printInfo("Resource " + id + " stopped.");
					} else {
						printError("Resource " + id + " is not found on repository.");
					}
				} catch (ResourceException e) {
					if (e.getCause() instanceof IncorrectLifecycleStateException)
						printError("Cannot stop resource " + id + " from state: " + ((IncorrectLifecycleStateException) e.getCause())
								.getResourceState());
					else
						printError("Cannot stop resource " + id + ": ", e);
				}

				// printSymbol(horizontalSeparator);
			}

			printInfo("Stopped " + counter + " resource/s from " + resourceIDs.size());

		} catch (Exception e) {
			printError("An error occurred stopping the resource.");
			printError(e);
		}
		printEndCommand();
		return null;

	}
}
