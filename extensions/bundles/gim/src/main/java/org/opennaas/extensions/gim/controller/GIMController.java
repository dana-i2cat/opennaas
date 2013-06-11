package org.opennaas.extensions.gim.controller;

import org.opennaas.extensions.gim.model.core.IPowerConsumer;
import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.core.IPowerSupply;
import org.opennaas.extensions.gim.model.core.entities.GIModel;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;

public class GIMController {

	public static PDUPort getPortById(PDU pdu, String portId) throws Exception {
		if (portId == null)
			throw new Exception("Could not find PDUport " + portId);

		for (PDUPort port : pdu.getPduPorts()) {
			if (portId.equals(port.getId())) {
				return port;
			}
		}
		throw new Exception("Could not find PDUport " + portId);
	}

	public static IPowerSupply getPowerSupply(GIModel model, String supplyId) throws ModelElementNotFoundException {

		if (supplyId == null)
			throw new ModelElementNotFoundException("Element id: " + supplyId);

		for (IPowerSupply supply : model.getSupplies()) {
			if (supplyId.equals(supply.getId()))
				return supply;
		}

		throw new ModelElementNotFoundException("Element id: " + supplyId);
	}

	public static IPowerDelivery getPowerDelivery(GIModel model, String deliveryId) throws ModelElementNotFoundException {

		if (deliveryId == null)
			throw new ModelElementNotFoundException("Element id: " + deliveryId);

		for (IPowerDelivery delivery : model.getDeliveries()) {
			if (deliveryId.equals(delivery.getId()))
				return delivery;
		}

		throw new ModelElementNotFoundException("Element id: " + deliveryId);

	}

	public static IPowerConsumer getPowerConsumer(GIModel model, String consumerId) throws ModelElementNotFoundException {

		if (consumerId == null)
			throw new ModelElementNotFoundException("Element id: " + consumerId);

		for (IPowerConsumer consumer : model.getConsumers()) {
			if (consumerId.equals(consumer.getId()))
				return consumer;
		}

		throw new ModelElementNotFoundException("Element id: " + consumerId);

	}

}
