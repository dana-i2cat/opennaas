package org.opennaas.extensions.powernet.capability.mgt.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerReceptor;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;

@Command(scope = "gim", name = "createDeliveryReceptor", description = "Creates a power delivery receptor")
public class CreateDeliveryReceptorCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "deliveryId", description = "The id of the delivery.", required = true, multiValued = false)
	private String	deliveryId;

	@Argument(index = 2, name = "receptorId", description = "The id of the receptor.", required = true, multiValued = false)
	private String	receptorId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("createDeliveryReceptor");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			IPowerNetManagementCapability capab = (IPowerNetManagementCapability) resource
					.getCapabilityByInterface(IPowerNetManagementCapability.class);

			// build receptor
			PowerReceptor receptor = new PowerReceptor();
			receptor.setId(receptorId);
			receptor.setElementId(deliveryId);
			receptor.setPowerMonitorLog(new PowerMonitorLog(0, 0));
			receptor.setPowerState(true);

			String id = capab.addPowerDeliveryReceptor(deliveryId, receptorId, receptor);
			printInfo(id);
		} catch (Exception e) {
			printError("Error in createDeliveryReceptor " + resourceName + " with id " + deliveryId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

}
