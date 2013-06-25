package org.opennaas.extensions.pdu.capability;

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
