package org.opennaas.extensions.gim.model.core.entities;

import java.util.List;

public class GIModel {

	private String				id;

	private List<PowerConsumer>	consumers;
	private List<PowerDelivery>	deliveries;
	private List<PowerSupply>	supplies;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public List<PowerConsumer> getConsumers() {
		return consumers;
	}

	public void setConsumers(List<PowerConsumer> consumers) {
		this.consumers = consumers;
	}

	public List<PowerDelivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(List<PowerDelivery> deliveries) {
		this.deliveries = deliveries;
	}

	public List<PowerSupply> getSupplies() {
		return supplies;
	}

	public void setSupplies(List<PowerSupply> supplies) {
		this.supplies = supplies;
	}

	@Override
	public String toString() {
		return "GIModel [id=" + id + ",\n consumers=" + consumers + ",\n deliveries=" + deliveries + ",\n supplies=" + supplies + "]";
	}

}
