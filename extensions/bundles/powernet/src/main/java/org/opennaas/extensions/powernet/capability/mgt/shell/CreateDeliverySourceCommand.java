package org.opennaas.extensions.powernet.capability.mgt.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;

@Command(scope = "gim", name = "createDeliverySource", description = "Creates a power delivery source")
public class CreateDeliverySourceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "deliveryId", description = "The id of the delivery.", required = true, multiValued = false)
	private String	deliveryId;

	@Argument(index = 2, name = "sourceId", description = "The id of the source.", required = true, multiValued = false)
	private String	sourceId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("createDeliverySource");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			IPowerNetManagementCapability capab = (IPowerNetManagementCapability) resource
					.getCapabilityByInterface(IPowerNetManagementCapability.class);

			// build source
			PowerSource source = new PowerSource();
			source.setId(sourceId);
			source.setElementId(deliveryId);
			source.setPowerMonitorLog(new PowerMonitorLog(0, 0));
			source.setPowerState(true);

			String id = capab.addPowerDeliverySource(deliveryId, sourceId, source);
			printInfo(id);
		} catch (Exception e) {
			printError("Error in createDeliverySource " + resourceName + " with id " + deliveryId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

}
