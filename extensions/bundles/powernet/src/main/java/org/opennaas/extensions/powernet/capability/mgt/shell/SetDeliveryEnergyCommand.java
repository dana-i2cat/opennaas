package org.opennaas.extensions.powernet.capability.mgt.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;

@Command(scope = "gim", name = "setDeliveryEnergy", description = "Set energy provided by given power delivery")
public class SetDeliveryEnergyCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "deliveryId", description = "The id of the delivery.", required = true, multiValued = false)
	private String	deliveryId;

	@Argument(index = 2, name = "energyClass", description = "The energyClass.", required = true, multiValued = false)
	private String	energyClass;
	@Argument(index = 3, name = "energyType", description = "The energyType.", required = true, multiValued = false)
	private String	energyType;
	@Argument(index = 4, name = "co2perUnit", description = "The CO2 per energy unit", required = true, multiValued = false)
	private double	co2perUnit;
	@Argument(index = 5, name = "greenPercentage", description = "The green percentage of the energy.", required = true, multiValued = false)
	private double	greenPercentage;
	@Argument(index = 6, name = "energyName", description = "The energy name [optional].", required = false, multiValued = false)
	private String	energyName;

	@Option(name = "--sourceId", aliases = "-s", description = "The id of the source.", required = false, multiValued = false)
	private String	sourceId	= null;

	@Option(name = "--allSources", aliases = "-A", description = "Causes the command to set energy to all sources in specified delivery.", required = false, multiValued = false)
	private boolean	allSources	= false;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("setDeliveryEnergy");

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
				capab.setPowerDeliverySourceEnergy(deliveryId, sourceId, energyName, energyClass, energyType, co2perUnit, greenPercentage);
			} else {
				for (String id : capab.getPowerDeliverySources(deliveryId)) {
					capab.setPowerDeliverySourceEnergy(deliveryId, id, energyName, energyClass, energyType, co2perUnit, greenPercentage);
				}
			}

		} catch (Exception e) {
			printError("Error in setDeliveryEnergy in resource" + resourceName + " and delivery " + deliveryId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

}
