package net.i2cat.luminis.capability.monitoring.shell;

import java.util.List;

import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceManager;
import net.i2cat.nexus.resources.alarms.IAlarmsRepository;
import net.i2cat.nexus.resources.alarms.ResourceAlarm;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

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

			for (String id : resourceIDs) {
				String resourceId;
				try {
					resourceId = getResourceId(id, manager);
				} catch (Exception e) {
					printError(e);
					printEndCommand();
					return -1;
				}

				if (resourceId != null) {

					printInfo("Resource ID: " + id);
					printSymbol(horizontalSeparator);

					List<ResourceAlarm> alarms = alarmsRepo.getResourceAlarms(resourceId);
					printAlarms(alarms);

				} else {
					printError("The resource " + id + " is not found on repository.");
				}
				printSymbol(underLine);
			}

		} catch (Exception e) {
			printError(e);
			printError("Error showing information of resource.");
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
				printInfo("Property " + name + ": " + alarm.getProperty(name));
			}
			i++;
			printSymbol(horizontalSeparator);
		}
	}
}
