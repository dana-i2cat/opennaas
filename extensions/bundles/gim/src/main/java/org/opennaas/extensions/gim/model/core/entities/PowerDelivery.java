package org.opennaas.extensions.gim.model.core.entities;



import java.util.List;

import org.opennaas.extensions.gim.model.core.IPowerConsumer;
import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.core.IPowerMonitorLogging;
import org.opennaas.extensions.gim.model.core.IPowerSupply;
import org.opennaas.extensions.gim.model.load.DeliveryRatedLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;


public class PowerDelivery implements IPowerDelivery, IPowerMonitorLogging {

	private PowerMonitorLog			powerMonitorLog;
	private List<IPowerSupply>		powerSupplies;
	private List<IPowerConsumer>	powerConsumers;
	private DeliveryRatedLoad		deliveryRatedLoad;

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

}
