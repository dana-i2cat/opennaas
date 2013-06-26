package org.opennaas.extensions.powernet.capability.mgt.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;
import org.opennaas.extensions.gim.controller.ModelElementNotFoundException;

@Command(scope = "gim", name = "deleteDelivery", description = "Deletes a power delivery")
public class DeleteDeliveryCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "deliveryId", description = "The id of the delivery.", required = true, multiValued = false)
	private String	deliveryId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("deleteDelivery");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			IPowerNetManagementCapability capab = (IPowerNetManagementCapability) resource
					.getCapabilityByInterface(IPowerNetManagementCapability.class);
			capab.deletePowerDelivery(deliveryId);
			printInfo("Deleted Delivery " + deliveryId);
		} catch (Exception e) {
			printError("Error in deleteDelivery " + resourceName + " with id " + deliveryId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

}
