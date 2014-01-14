package org.opennaas.extensions.power.capabilities.driver;

/*
 * #%L
 * OpenNaaS :: Power :: Capabilities
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

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.gim.controller.BasicConsumerController;
import org.opennaas.extensions.gim.controller.GIMController;
import org.opennaas.extensions.gim.controller.ModelElementNotFoundException;
import org.opennaas.extensions.gim.controller.RouterWithPDUPowerController;
import org.opennaas.extensions.gim.model.core.entities.GIModel;
import org.opennaas.extensions.gim.model.core.entities.PowerConsumer;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;
import org.opennaas.extensions.pdu.Activator;
import org.opennaas.extensions.pdu.capability.PDUResourcePowerController;

public class ConsumerDriverInstantiator {

	public static RouterWithPDUPowerController create(String resourceId,
			String powernetResourceName, String consumerId, CapabilityDescriptor descriptor) throws Exception {

		BasicConsumerController consumerController = new BasicConsumerController();
		consumerController.setModel(
				(GIModel) Activator.getResourceManagerService().getResource(
						Activator.getResourceManagerService().getIdentifierFromResourceName("powernet", powernetResourceName))
						.getModel());

		IResource pduResource = getPDUResource(consumerController.getModel(), consumerId);

		// FIXME A CONSUMER MAY HAVE MULTIPLE PDUS!!!!
		PDUResourcePowerController pduController = new PDUResourcePowerController();
		pduController.setPduResource(pduResource);

		RouterWithPDUPowerController routerController = new RouterWithPDUPowerController();
		routerController.setConsumerController(consumerController);
		routerController.setPduController(pduController);
		routerController.setConsumerId(consumerId);

		return routerController;
	}

	// FIXME A CONSUMER MAY HAVE MULTIPLE PDUS!!!!
	private static IResource getPDUResource(GIModel model, String consumerId) throws ModelElementNotFoundException, ResourceException,
			ActivatorException {
		PowerConsumer consumer = GIMController.getPowerConsumer(model, consumerId);
		List<IResource> pdus = new ArrayList<IResource>();
		for (PowerSource source : GIMController.getConsumerAttachedSources(GIMController.getPowerConsumer(model, consumerId))) {
			String pduResourceType = source.getElementId().split(":")[0];
			String pduResourceName = source.getElementId().split(":")[1];
			pdus.add(Activator.getResourceManagerService().getResource(
					Activator.getResourceManagerService().getIdentifierFromResourceName(pduResourceType, pduResourceName)));
		}

		if (pdus.isEmpty())
			throw new ModelElementNotFoundException("Failed to find pdu resources");

		// FIXME A CONSUMER MAY HAVE MULTIPLE PDUS!!!!
		return pdus.get(0);
	}

}
