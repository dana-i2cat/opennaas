package net.i2cat.luminis.capability.monitoring.shell;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceManager;
import org.opennaas.core.resources.alarms.IAlarmsRepository;
import org.opennaas.core.resources.shell.GenericKarafCommand;

/**
 * Lists alarms per resource
 *
 * @author Carlos Baez
 */
@Command(scope = "alarms", name = "clear", description = "Clear all alarms fromgiven resources")
public class ClearAlarmsCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "A space delimited list of resource type and name.", required = true, multiValued = true)
	private List<String>	resourceIDs;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("Clear alarms");
		try {

			IAlarmsRepository alarmsRepo = getAlarmsRepository();
			ResourceManager manager = (ResourceManager) getResourceManager();

			/* Clear alarms for all the resources */
			if (resourceIDs == null || resourceIDs.size() == 0) {
				alarmsRepo.clear();
				printEndCommand();
				return null;
			}

			for (String friendlyId : resourceIDs) {
				String resourceId;
				try {
					resourceId = getResourceId(friendlyId, manager);
				} catch (Exception e) {
					printError(e);
					printEndCommand();
					return -1;
				}

				if (resourceId != null) {
					alarmsRepo.clearResourceAlarms(resourceId);
					printInfo("Cleared alarms for resource: " + friendlyId);

				} else {
					printError("The resource " + friendlyId + " is not found on repository.");
				}
				// printSymbol(underLine);
			}

		} catch (Exception e) {
			printError(e);
			printError("Error clearing alarms.");
		}
		printEndCommand();
		return null;

	}

	private String getResourceId(String friendlyName, IResourceManager resourceManager) throws Exception {

		String[] argsRouterName = new String[2];
		argsRouterName = splitResourceName(friendlyName);

		IResourceIdentifier identifier = resourceManager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
		return identifier.getId();
	}
}
