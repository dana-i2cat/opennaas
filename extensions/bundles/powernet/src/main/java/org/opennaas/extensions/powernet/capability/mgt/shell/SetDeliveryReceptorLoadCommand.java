package org.opennaas.extensions.powernet.capability.mgt.shell;

/*
 * #%L
 * OpenNaaS :: Power Net Resource
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;

@Command(scope = "gim", name = "setDeliveryReceptorLoad", description = "Set load given power delivery receptor is designated to work with")
public class SetDeliveryReceptorLoadCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "deliveryId", description = "The id of the delivery.", required = true, multiValued = false)
	private String	deliveryId;

	@Argument(index = 2, name = "oltage", description = "The input voltage.", required = true, multiValued = false)
	private double	inputVoltage;
	@Argument(index = 3, name = "inputCurrent", description = "The input current.", required = true, multiValued = false)
	private double	inputCurrent;
	@Argument(index = 4, name = "inputPower", description = "The input power.", required = true, multiValued = false)
	private double	inputPower;
	@Argument(index = 5, name = "inputEnergy", description = "The input energy.", required = true, multiValued = false)
	private double	inputEnergy;

	@Option(name = "--receptorId", aliases = "-s", description = "The id of the receptor.", required = false, multiValued = false)
	private String	receptorId		= null;

	@Option(name = "--allReceptors", aliases = "-A", description = "Causes the command to set load to all receptors in specified delivery.", required = false, multiValued = false)
	private boolean	allReceptors	= false;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("setDeliveryReceptorLoad");

		if (!allReceptors && receptorId == null) {
			printError("Either sourceId or allSources option must be specified");
			printEndCommand();
			return null;
		}

		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			IPowerNetManagementCapability capab = (IPowerNetManagementCapability) resource
					.getCapabilityByInterface(IPowerNetManagementCapability.class);

			if (!allReceptors) {
				capab.setPowerDeliveryReceptorRatedLoad(deliveryId, receptorId, inputVoltage, inputCurrent, inputPower, inputEnergy);
			} else {
				for (String id : capab.getPowerDeliverySources(deliveryId)) {
					capab.setPowerDeliveryReceptorRatedLoad(deliveryId, id, inputVoltage, inputCurrent, inputPower, inputEnergy);
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
