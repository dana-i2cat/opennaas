package org.opennaas.extensions.powernet.capability.mgt.shell;

import java.lang.StringBuffer;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;
import org.opennaas.extensions.gim.controller.ModelElementNotFoundException;

@Command(scope = "gim", name = "setDeliveryLoad", description = "Set load given power delivery is designated to work with")
public class SetDeliveryLoadCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "deliveryId", description = "The id of the delivery.", required = true, multiValued = false)
	private String	deliveryId;
	
	@Argument(index = 2, name = "inputVoltage", description = "The input voltage.", required = true, multiValued = false)
	private double inputVoltage;
	@Argument(index = 3, name = "inputCurrent", description = "The input current.", required = true, multiValued = false)
	private double inputCurrent; 
	@Argument(index = 4, name = "inputPower", description = "The input power.", required = true, multiValued = false)
	private double inputPower;
	@Argument(index = 5, name = "inputEnergy", description = "The input energy.", required = true, multiValued = false)
	private double inputEnergy;
	@Argument(index = 6, name = "outputVoltage", description = "The input voltage.", required = true, multiValued = false)
	private double outputVoltage;
	@Argument(index = 7, name = "outputCurrent", description = "The input current.", required = true, multiValued = false)
	private double outputCurrent;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("setDeliveryLoad");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			IPowerNetManagementCapability capab = (IPowerNetManagementCapability) resource
					.getCapabilityByInterface(IPowerNetManagementCapability.class);
			capab.setPowerDeliveryRatedLoad(deliveryId, inputVoltage, inputCurrent, inputPower, inputEnergy, outputVoltage, outputCurrent);
		} catch (Exception e) {
			printError("Error in setDeliveryLoad in resource" + resourceName + " and delivery " + deliveryId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

}
