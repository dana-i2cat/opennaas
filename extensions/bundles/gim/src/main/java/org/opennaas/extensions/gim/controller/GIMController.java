package org.opennaas.extensions.gim.controller;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.extensions.gim.model.core.entities.GIModel;
import org.opennaas.extensions.gim.model.core.entities.PowerConsumer;
import org.opennaas.extensions.gim.model.core.entities.PowerDelivery;
import org.opennaas.extensions.gim.model.core.entities.PowerSupply;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerReceptor;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSocket;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

public class GIMController {

	public static PDUPort getPortById(PDU pdu, String portId) throws ModelElementNotFoundException {
		return (PDUPort) getSocketById(pdu.getPowerSources(), portId);
	}

	public static PowerSupply getPowerSupply(GIModel model, String supplyId) throws ModelElementNotFoundException {

		if (supplyId == null)
			throw new ModelElementNotFoundException("Element id: " + supplyId);

		for (PowerSupply supply : model.getSupplies()) {
			if (supplyId.equals(supply.getId()))
				return supply;
		}

		throw new ModelElementNotFoundException("Element id: " + supplyId);
	}

	public static PowerDelivery getPowerDelivery(GIModel model, String deliveryId) throws ModelElementNotFoundException {

		if (deliveryId == null)
			throw new ModelElementNotFoundException("Element id: " + deliveryId);

		for (PowerDelivery delivery : model.getDeliveries()) {
			if (deliveryId.equals(delivery.getId()))
				return delivery;
		}

		throw new ModelElementNotFoundException("Element id: " + deliveryId);

	}

	public static PowerConsumer getPowerConsumer(GIModel model, String consumerId) throws ModelElementNotFoundException {

		if (consumerId == null)
			throw new ModelElementNotFoundException("Element id: " + consumerId);

		for (PowerConsumer consumer : model.getConsumers()) {
			if (consumerId.equals(consumer.getId()))
				return consumer;
		}

		throw new ModelElementNotFoundException("Element id: " + consumerId);

	}

	public static Energy getEnergyUsedInReceptor(PowerReceptor receptor) {
		return receptor.getAttachedTo().getEnergy();
	}

	public static PowerSocket getSocketById(List<? extends PowerSocket> sockets, String id) throws ModelElementNotFoundException {
		if (id == null)
			throw new ModelElementNotFoundException("Socket id: " + id);

		if (sockets == null || sockets.isEmpty())
			throw new ModelElementNotFoundException("Socket id: " + id);

		for (PowerSocket aSocket : sockets) {
			if (aSocket.getId().equals(id)) {
				return aSocket;
			}
		}
		throw new ModelElementNotFoundException("Socket id: " + id);
	}

	public static MeasuredLoad getLastMeasuredLoad(PowerMonitorLog log) throws ModelElementNotFoundException {
		if (log == null || log.getMeasuredLoads() == null || log.getMeasuredLoads().isEmpty())
			throw new ModelElementNotFoundException("No entries found in MonitoringLog " + log);

		return log.getMeasuredLoads().get(log.getMeasuredLoads().size() - 1);
	}

	public static List<PowerSource> getConsumerAttachedSources(PowerConsumer consumer) {
		List<PowerSource> sources = new ArrayList<PowerSource>();
		for (PowerReceptor receptor : consumer.getPowerReceptors()) {
			PowerSource source = receptor.getAttachedTo();
			if (source != null) {
				sources.add(source);
			}
		}
		return sources;
	}

}
