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

@Command(scope = "gim", name = "connectDeliveryConsumer", description = "Connects consumer with given delivery.")
public class ConnectDeliveryConsumerCommand extends GenericKarafCommand {

	@Option(name = "--disconnect", aliases = "-d", description = "Disconnect given elements, instead of normal behaviour", required = false, multiValued = false)
	boolean			disconnect	= false;

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "deliveryId", description = "The id of the delivery giving energy.", required = true, multiValued = false)
	private String	deliveryId;

	@Argument(index = 2, name = "deliverySourceId", description = "The id of the power source soket in delivery giving the energy.", required = true, multiValued = false)
	private String	deliverySourceId;

	@Argument(index = 3, name = "consumerId", description = "The id of the consumer receiving.", required = true, multiValued = false)
	private String	consumerId;

	@Argument(index = 4, name = "consumerReceptorId", description = "The id of the power receptor soket in consumer receiving the energy.", required = true, multiValued = false)
	private String	consumerReceptorId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("connectDeliveryConsumer");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			IPowerNetManagementCapability capab = (IPowerNetManagementCapability) resource
					.getCapabilityByInterface(IPowerNetManagementCapability.class);
			if (!disconnect) {
				capab.connectDeliveryConsumer(deliveryId, deliverySourceId, consumerId, consumerReceptorId);
			} else {
				capab.disconnectDeliveryConsumer(deliveryId, deliverySourceId, consumerId, consumerReceptorId);
			}
		} catch (Exception e) {
			printError("Error in connectDeliveryConsumer in resource" + resourceName + " with delivery " + deliveryId + " and consumer " + consumerId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

}
