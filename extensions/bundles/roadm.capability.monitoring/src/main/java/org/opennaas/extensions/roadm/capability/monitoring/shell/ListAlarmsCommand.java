package org.opennaas.extensions.roadm.capability.monitoring.shell;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceManager;
import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.roadm.capability.monitoring.IMonitoringCapability;

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
			ResourceManager manager = (ResourceManager) getResourceManager();
			for (String friendlyId : resourceIDs) {
				IResourceIdentifier resourceIdentifier = getResourceIdentifier(friendlyId, manager);
				IResource resource = manager.getResource(resourceIdentifier);
				IMonitoringCapability monitoringCapability = (IMonitoringCapability) resource.getCapabilityByInterface(IMonitoringCapability.class);
				List<ResourceAlarm> alarms = monitoringCapability.getAlarms();
				printAlarms(alarms);
			}
		} catch (Exception e) {
			printError("Error listing alarms.");
		}
		printEndCommand();
		return null;
	}

	private IResourceIdentifier getResourceIdentifier(String friendlyName, IResourceManager resourceManager) throws Exception {
		String[] argsRouterName = new String[2];
		argsRouterName = splitResourceName(friendlyName);
		IResourceIdentifier identifier = resourceManager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
		return identifier;
	}

	private void printAlarms(List<ResourceAlarm> alarms) {
		int i = 0;
		for (ResourceAlarm alarm : alarms) {
			printInfo("Alarm " + i);
			for (String name : alarm.getPropertyNames()) {
				printInfo(doubleTab + "Property " + name + ": " + alarm.getProperty(name));
			}
			i++;
		}
	}
}
