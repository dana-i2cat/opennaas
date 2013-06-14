package org.opennaas.extensions.gim.model.core.entities;

import java.util.List;

import org.opennaas.extensions.gim.model.core.IPowerConsumer;
import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.load.RatedLoad;

public class PowerConsumer implements IPowerConsumer {

	private String				id;
	private List<PowerDelivery>	powerDeliveries;
	private RatedLoad			ratedLoad;

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
