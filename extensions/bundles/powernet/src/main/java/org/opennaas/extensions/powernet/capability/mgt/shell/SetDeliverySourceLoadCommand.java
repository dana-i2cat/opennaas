package org.opennaas.extensions.powernet.capability.mgt.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;

@Command(scope = "gim", name = "setDeliverySourceLoad", description = "Set load given power delivery source is designated to work with")
public class SetDeliverySourceLoadCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "deliveryId", description = "The id of the delivery.", required = true, multiValued = false)
	private String	deliveryId;

	@Argument(index = 2, name = "inputVoltage", description = "The input voltage.", required = true, multiValued = false)
	private double	inputVoltage;
	@Argument(index = 3, name = "inputCurrent", description = "The input current.", required = true, multiValued = false)
	private double	inputCurrent;
	@Argument(index = 4, name = "inputPower", description = "The input power.", required = true, multiValued = false)
	private double	inputPower;
	@Argument(index = 5, name = "inputEnergy", description = "The input energy.", required = true, multiValued = false)
	private double	inputEnergy;

	@Option(name = "--sourceId", aliases = "-s", description = "The id of the source.", required = false, multiValued = false)
	private String	sourceId	= null;

	@Option(name = "--allSources", aliases = "-A", description = "Causes the command to set load to all sources in specified supply.", required = false, multiValued = false)
	private boolean	allSources	= false;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("setDeliverySourceLoad");

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
				capab.setPowerDeliverySourceRatedLoad(deliveryId, sourceId, inputVoltage, inputCurrent, inputPower, inputEnergy);
			} else {
				for (String id : capab.getPowerDeliverySources(deliveryId)) {
					capab.setPowerDeliverySourceRatedLoad(deliveryId, id, inputVoltage, inputCurrent, inputPower, inputEnergy);
				}
			}

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
