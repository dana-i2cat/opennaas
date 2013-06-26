package org.opennaas.extensions.powernet.capability.mgt.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;

@Command(scope = "gim", name = "deleteDeliveryReceptor", description = "Deletes a power delivery receptor")
public class DeleteDeliveryReceptorCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "deliveryId", description = "The id of the delivery.", required = true, multiValued = false)
	private String	deliveryId;

	@Argument(index = 2, name = "receptorId", description = "The id of the delivery receptor.", required = true, multiValued = false)
	private String	receptorId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("deleteDeliveryReceptor");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			IPowerNetManagementCapability capab = (IPowerNetManagementCapability) resource
					.getCapabilityByInterface(IPowerNetManagementCapability.class);
			capab.removePowerDeliveryReceptor(deliveryId, receptorId);
		} catch (Exception e) {
			printError("Error in deleteDeliveryReceptor " + resourceName + " in delivery" + deliveryId + "with id " + receptorId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

}
