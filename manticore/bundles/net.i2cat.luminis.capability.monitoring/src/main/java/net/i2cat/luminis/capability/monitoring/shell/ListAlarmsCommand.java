package net.i2cat.luminis.capability.monitoring.shell;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceManager;
import org.opennaas.core.resources.alarms.IAlarmsRepository;
import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.shell.GenericKarafCommand;

/**
 * Lists alarms per resource
 *
 * @author Isart Canyameres
 *
 */
@Command(scope = "alarms", name = "list", description = "List alarms of given resource (all alarms if no resource is given)")
public class ListAlarmsCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "A space delimited list of resource type and name.", required = true, multiValued = true)
	private List<String>	resourceIDs;

	@Override
	protected Object doExecute() throws Exception {

		printInitCommand("list alarms");
		try {

			IAlarmsRepository alarmsRepo = getAlarmsRepository();
			ResourceManager manager = (ResourceManager) getResourceManager();

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

					printInfo("Resource ID: " + friendlyId);
					// printSymbol(horizontalSeparator);

					List<ResourceAlarm> alarms = alarmsRepo.getResourceAlarms(resourceId);
					printAlarms(alarms);

				} else {
					printError("The resource " + friendlyId + " is not found on repository.");
				}
				// printSymbol(underLine);
			}

		} catch (Exception e) {
			printError(e);
			printError("Error listing alarms.");
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

	private void printAlarms(List<ResourceAlarm> alarms) {
		int i = 0;
		for (ResourceAlarm alarm : alarms) {
			printInfo("Alarm " + i);
			for (String name : alarm.getPropertyNames()) {
				printInfo(doubleTab + "Property " + name + ": " + alarm.getProperty(name));
			}
			i++;
			// printSymbol(horizontalSeparator);
		}
	}
}
