package org.opennaas.extensions.powernet.capability.mgt.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;

@Command(scope = "gim", name = "setConsumerLoad", description = "Set load given power consumer receptor is designated to work with")
public class SetConsumerLoadCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "consumerId", description = "The id of the consumer.", required = true, multiValued = false)
	private String	consumerId;

	@Argument(index = 2, name = "voltage", description = "The input voltage.", required = true, multiValued = false)
	private double	voltage;
	@Argument(index = 3, name = "current", description = "The input current.", required = true, multiValued = false)
	private double	current;
	@Argument(index = 4, name = "power", description = "The input power.", required = true, multiValued = false)
	private double	power;
	@Argument(index = 5, name = "energy", description = "The input energy.", required = true, multiValued = false)
	private double	energy;

	@Option(name = "--receptorId", aliases = "-r", description = "The id of the receptor.", required = false, multiValued = false)
	private String	receptorId		= null;

	@Option(name = "--allReceptors", aliases = "-A", description = "Causes the command to set load to all receptors in specified consumer.", required = false, multiValued = false)
	private boolean	allReceptors	= false;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("setConsumerLoad");

		if (!allReceptors && receptorId == null) {
			printError("Either receptorId or allReceptors option must be specified");
			printEndCommand();
			return null;
		}

		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			IPowerNetManagementCapability capab = (IPowerNetManagementCapability) resource
					.getCapabilityByInterface(IPowerNetManagementCapability.class);

			if (!allReceptors) {
				capab.setPowerConsumerReceptorRatedLoad(consumerId, receptorId, voltage, current, power, energy);
			} else {
				for (String id : capab.getPowerConsumerReceptors(consumerId)) {
					capab.setPowerConsumerReceptorRatedLoad(consumerId, id, voltage, current, power, energy);
				}
			}

		} catch (Exception e) {
			printError("Error in setConsumerLoad in resource" + resourceName + " and consumer " + consumerId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

}
