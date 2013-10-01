package org.opennaas.extensions.powernet.capability.mgt.shell;

import java.lang.StringBuffer;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;
import org.opennaas.extensions.gim.controller.ModelElementNotFoundException;
import org.opennaas.extensions.gim.model.core.IPowerSupply;
import org.opennaas.extensions.gim.model.core.IPowerDelivery;

@Command(scope = "gim", name = "showSupply", description = "Shows a power supply")
public class ShowSupplyCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "supplyId", description = "The id of the supply.", required = true, multiValued = false)
	private String	supplyId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("showSupply");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			IPowerNetManagementCapability capab = (IPowerNetManagementCapability) resource
					.getCapabilityByInterface(IPowerNetManagementCapability.class);
			IPowerSupply supply = capab.getPowerSupply(supplyId);
			printSupply(supply);
		} catch (Exception e) {
			printError("Error in showSupply " + resourceName + " with id " + supplyId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

	private void printSupply(IPowerSupply supply) {
		printSymbol(supply.toString());
	}

}
