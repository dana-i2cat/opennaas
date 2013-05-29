package gim.core.entities;

import gim.core.IPowerConsumer;
import gim.core.IPowerDelivery;
import gim.core.IPowerMonitorLogging;
import gim.core.IPowerSupply;
import gim.load.DeliveryRatedLoad;
import gim.log.PowerMonitorLog;

import java.util.List;

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
