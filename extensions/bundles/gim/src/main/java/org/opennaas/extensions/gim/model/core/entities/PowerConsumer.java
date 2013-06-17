package org.opennaas.extensions.gim.model.core.entities;

import java.util.List;

import org.opennaas.extensions.gim.model.core.IPowerConsumer;
import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerReceptor;
import org.opennaas.extensions.gim.model.load.RatedLoad;

public class PowerConsumer extends GIMElement implements IPowerConsumer {

	private List<PowerDelivery>	powerDeliveries;
	private RatedLoad			ratedLoad;
	private List<PowerReceptor>	powerReceptors;

	public List<PowerDelivery> getPowerDeliveries() {
		return powerDeliveries;
	}

	public void setPowerDeliveries(List<PowerDelivery> powerDeliveries) {
		this.powerDeliveries = powerDeliveries;
	}

	public RatedLoad getRatedLoad() {
		return ratedLoad;
	}

	public void setRatedLoad(RatedLoad ratedLoad) {
		this.ratedLoad = ratedLoad;
	}

	/**
	 * @return the powerReceptors
	 */
	public List<PowerReceptor> getPowerReceptors() {
		return powerReceptors;
	}

	/**
	 * @param powerReceptors
	 *            the powerReceptors to set
	 */
	public void setPowerReceptors(List<PowerReceptor> powerReceptors) {
		this.powerReceptors = powerReceptors;
	}

	@Override
	public String toString() {
		String deliveries;
		if (powerDeliveries == null) {
			deliveries = "null";
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			for (IPowerDelivery delivery : powerDeliveries) {
				sb.append("PowerDelivery [id=" + delivery.getId() + "],");
			}
			sb.append("}");
			deliveries = sb.toString();
		}

		return "PowerConsumer [id=" + id + ", ratedLoad=" + ratedLoad + ", powerDeliveries=" + deliveries + "]";
	}

}
