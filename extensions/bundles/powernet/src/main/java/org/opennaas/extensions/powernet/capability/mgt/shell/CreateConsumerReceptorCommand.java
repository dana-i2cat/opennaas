package org.opennaas.extensions.powernet.capability.mgt.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerReceptor;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;

@Command(scope = "gim", name = "createConsumerReceptor", description = "Creates a power consumer receptor")
public class CreateConsumerReceptorCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "consumerId", description = "The id of the consumer.", required = true, multiValued = false)
	private String	consumerId;

	@Argument(index = 2, name = "receptorId", description = "The id of the consumer receptor.", required = true, multiValued = false)
	private String	receptorId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("createConsumerReceptor");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			IPowerNetManagementCapability capab = (IPowerNetManagementCapability) resource
					.getCapabilityByInterface(IPowerNetManagementCapability.class);

			// build receptor
			PowerReceptor receptor = new PowerReceptor();
			receptor.setId(receptorId);
			receptor.setElementId(consumerId);
			receptor.setPowerMonitorLog(new PowerMonitorLog(0, 0));
			receptor.setPowerState(true);

			String id = capab.addPowerConsumerReceptor(consumerId, receptorId, receptor);
			printInfo(id);
		} catch (Exception e) {
			printError("Error in createConsumerReceptor " + resourceName + " with id " + consumerId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

}
