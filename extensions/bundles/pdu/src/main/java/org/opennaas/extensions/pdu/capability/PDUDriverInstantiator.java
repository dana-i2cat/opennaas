package org.opennaas.extensions.pdu.capability;

/*
 * #%L
 * OpenNaaS :: PDU Resource
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

import org.opennaas.extensions.gim.controller.APCPDUPowerControllerDriver;
import org.opennaas.extensions.gim.controller.BasicDeliveryController;
import org.opennaas.extensions.gim.controller.PDUController;
import org.opennaas.extensions.gim.controller.snmp.APCDriver_SNMP;
import org.opennaas.extensions.gim.model.core.entities.GIModel;
import org.opennaas.extensions.pdu.Activator;

public class PDUDriverInstantiator {

	public static PDUController create(String resourceId, String powernetResourceName, String deliveryId, String pduIPAddress) throws Exception {

		APCDriver_SNMP snmpDriver;
		APCPDUPowerControllerDriver apcDriver;
		BasicDeliveryController deliveryController;
		PDUController pduController;

		snmpDriver = new APCDriver_SNMP(pduIPAddress);

		apcDriver = new APCPDUPowerControllerDriver();
		apcDriver.setDriver(snmpDriver);

		deliveryController = new BasicDeliveryController();
		deliveryController.setModel(
				(GIModel) Activator.getResourceManagerService().getResource(
						Activator.getResourceManagerService().getIdentifierFromResourceName("powernet", powernetResourceName))
						.getModel());

		pduController = new PDUController();
		pduController.setDeliveryController(deliveryController);
		pduController.setDeliveryId(deliveryId);
		pduController.setDriver(apcDriver);

		return pduController;
	}
}
