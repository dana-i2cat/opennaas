package org.opennaas.extensions.powernet.capability.mgt.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;

@Command(scope = "gim", name = "deleteConsumerReceptor", description = "Deletes a power consumer receptor")
public class DeleteConsumerReceptorCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "consumerId", description = "The id of the consumer.", required = true, multiValued = false)
	private String	consumerId;

	@Argument(index = 2, name = "receptorId", description = "The id of the consumer receptor.", required = true, multiValued = false)
	private String	receptorId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("deleteConsumerReceptor");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			IPowerNetManagementCapability capab = (IPowerNetManagementCapability) resource
					.getCapabilityByInterface(IPowerNetManagementCapability.class);
			capab.removePowerConsumerReceptor(consumerId, receptorId);
		} catch (Exception e) {
			printError("Error in deleteConsumerReceptor " + resourceName + " in consumer" + consumerId + "with id " + receptorId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

}
