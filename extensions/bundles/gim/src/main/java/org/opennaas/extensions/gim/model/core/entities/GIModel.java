package org.opennaas.extensions.gim.model.core.entities;



import java.util.List;

import org.opennaas.extensions.gim.model.core.IPowerConsumer;
import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.core.IPowerSupply;


public class GIModel {

	private List<IPowerConsumer>	consumers;
	private List<IPowerDelivery>	deliveries;
	private List<IPowerSupply>		supplies;

	public List<IPowerConsumer> getConsumers() {
		return consumers;
	}

	public void setConsumers(List<IPowerConsumer> consumers) {
		this.consumers = consumers;
	}

	public List<IPowerDelivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(List<IPowerDelivery> deliveries) {
		this.deliveries = deliveries;
	}

	public List<IPowerSupply> getSupplies() {
		return supplies;
	}

	public void setSupplies(List<IPowerSupply> supplies) {
		this.supplies = supplies;
	}

}
