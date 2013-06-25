package org.opennaas.extensions.powernet.capability.mgt.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;

@Command(scope = "gim", name = "connectSupplyDelivery", description = "Connects supply with given delivery.")
public class ConnectSupplyDeliveryCommand extends GenericKarafCommand {

	@Option(name = "--disconnect", aliases = "-d", description = "Disconnect given elements, instead of normal behaviour", required = false, multiValued = false)
	boolean			disconnect	= false;

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "supplyId", description = "The id of the supply giving energy.", required = true, multiValued = false)
	private String	supplyId;

	@Argument(index = 2, name = "supplySourceId", description = "The id of the power source socket in supply giving the energy.", required = true, multiValued = false)
	private String	supplySourceId;

	@Argument(index = 3, name = "deliveryId", description = "The id of the delivery receiving energy.", required = true, multiValued = false)
	private String	deliveryId;

	@Argument(index = 4, name = "deliveryReceptorId", description = "The id of the power receptor socket in delivery receiving the energy.", required = true, multiValued = false)
	private String	deliveryReceptorId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("connectSupplyDelivery");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			IPowerNetManagementCapability capab = (IPowerNetManagementCapability) resource
					.getCapabilityByInterface(IPowerNetManagementCapability.class);
			if (!disconnect) {
				capab.connectSupplyDelivery(supplyId, supplySourceId, deliveryId, deliveryReceptorId);
			} else {
				capab.disconnectSupplyDelivery(supplyId, supplySourceId, deliveryId, deliveryReceptorId);
			}
		} catch (Exception e) {
			printError("Error in connectSupplyDelivery in resource" + resourceName + " with supply " + supplyId + " and delivery " + deliveryId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

}
