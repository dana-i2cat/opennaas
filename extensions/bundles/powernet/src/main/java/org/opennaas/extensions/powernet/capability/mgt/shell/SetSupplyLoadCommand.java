package org.opennaas.extensions.powernet.capability.mgt.shell;

import java.lang.StringBuffer;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;
import org.opennaas.extensions.gim.controller.ModelElementNotFoundException;

@Command(scope = "gim", name = "setSupplyLoad", description = "Set load given power supply is designated to work with")
public class SetSupplyLoadCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "supplyId", description = "The id of the supply.", required = true, multiValued = false)
	private String	supplyId;
	
	@Argument(index = 2, name = "voltage", description = "The input voltage.", required = true, multiValued = false)
	private double voltage;
	@Argument(index = 3, name = "current", description = "The input current.", required = true, multiValued = false)
	private double current; 
	@Argument(index = 4, name = "power", description = "The input power.", required = true, multiValued = false)
	private double power;
	@Argument(index = 5, name = "energy", description = "The input energy.", required = true, multiValued = false)
	private double energy;
	

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("setSupplyLoad");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			IPowerNetManagementCapability capab = (IPowerNetManagementCapability) resource
					.getCapabilityByInterface(IPowerNetManagementCapability.class);
			capab.setPowerSupplyRatedLoad(supplyId, voltage, current, power, energy);
		} catch (Exception e) {
			printError("Error in setSupplyLoad in resource" + resourceName + " and supply " + supplyId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

}
