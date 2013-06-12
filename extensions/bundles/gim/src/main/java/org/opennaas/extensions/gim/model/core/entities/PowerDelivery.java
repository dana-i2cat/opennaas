package org.opennaas.extensions.gim.model.core.entities;

import java.util.List;

import org.opennaas.extensions.gim.model.core.IPowerConsumer;
import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.core.IPowerMonitorLogging;
import org.opennaas.extensions.gim.model.core.IPowerSupply;
import org.opennaas.extensions.gim.model.load.DeliveryRatedLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

public class PowerDelivery implements IPowerDelivery, IPowerMonitorLogging {

	private String					id;
	private PowerMonitorLog			powerMonitorLog;
	private DeliveryRatedLoad		deliveryRatedLoad;
	private List<IPowerSupply>		powerSupplies;
	private List<IPowerConsumer>	powerConsumers;

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

	public PowerMonitorLog getPowerMonitorLog() {
		return powerMonitorLog;
	}

	public void setPowerMonitorLog(PowerMonitorLog powerMonitorLog) {
		this.powerMonitorLog = powerMonitorLog;
	}

	public List<IPowerSupply> getPowerSupplies() {
		return powerSupplies;
	}

	public void setPowerSupplies(List<IPowerSupply> powerSupplies) {
		this.powerSupplies = powerSupplies;
	}

	public DeliveryRatedLoad getDeliveryRatedLoad() {
		return deliveryRatedLoad;
	}

	public void setDeliveryRatedLoad(DeliveryRatedLoad deliveryRatedLoad) {
		this.deliveryRatedLoad = deliveryRatedLoad;
	}

	public List<IPowerConsumer> getPowerConsumers() {
		return powerConsumers;
	}

	/**
	 * @param powerConsumers
	 *            the powerConsumers to set
	 */
	public void setPowerConsumers(List<IPowerConsumer> powerConsumers) {
		this.powerConsumers = powerConsumers;
	}

	@Override
	public String toString() {

		String consumers;
		if (powerConsumers == null) {
			consumers = "null";
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			for (IPowerConsumer consumer : powerConsumers) {
				sb.append("PowerConsumer [id=" + consumer.getId() + "],");
			}
			sb.append("}");
			consumers = sb.toString();
		}

		String supplies;
		if (powerSupplies == null) {
			supplies = "null";
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			for (IPowerSupply supply : powerSupplies) {
				sb.append("PowerSupply [id=" + supply.getId() + "],");
			}
			sb.append("}");
			supplies = sb.toString();
		}

		return "PowerDelivery [id=" + id + ", deliveryRatedLoad=" + deliveryRatedLoad + ", powerSupplies=" + supplies + ", powerConsumers=" + consumers + "]";
	}

}
