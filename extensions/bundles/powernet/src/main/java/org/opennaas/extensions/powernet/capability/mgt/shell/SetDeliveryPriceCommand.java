package org.opennaas.extensions.powernet.capability.mgt.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;

@Command(scope = "gim", name = "setDeliveryPrice", description = "Set given delivery per energy unit price.")
public class SetDeliveryPriceCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "deliveryId", description = "The id of the delivery.", required = true, multiValued = false)
	private String	deliveryId;

	@Argument(index = 2, name = "price", description = "Given delivery price per energy unit", required = true, multiValued = false)
	private double	price;

	@Option(name = "--sourceId", aliases = "-s", description = "The id of the source.", required = false, multiValued = false)
	private String	sourceId	= null;

	@Option(name = "--allSources", aliases = "-A", description = "Causes the command to set load to all sources in specified delivery.", required = false, multiValued = false)
	private boolean	allSources	= false;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("setDeliveryPrice");

		if (!allSources && sourceId == null) {
			printError("Either sourceId or allSources option must be specified");
			printEndCommand();
			return null;
		}

		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			IPowerNetManagementCapability capab = (IPowerNetManagementCapability) resource
					.getCapabilityByInterface(IPowerNetManagementCapability.class);

			if (!allSources) {
				capab.setPowerDeliverySourcePrice(deliveryId, sourceId, price);
			} else {
				for (String id : capab.getPowerDeliverySources(deliveryId)) {
					capab.setPowerDeliverySourcePrice(deliveryId, id, price);
				}
			}

		} catch (Exception e) {
			printError("Error in setDeliveryPrice in resource" + resourceName + " and delivery " + deliveryId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

}
