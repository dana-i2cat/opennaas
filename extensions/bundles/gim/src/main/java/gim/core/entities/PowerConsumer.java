package gim.core.entities;

import gim.core.IPowerConsumer;
import gim.core.IPowerDelivery;
import gim.load.RatedLoad;

import java.util.List;

public class PowerConsumer implements IPowerConsumer {

	private List<IPowerDelivery>	powerDeliveries;
	private RatedLoad				ratedLoad;

	public List<IPowerDelivery> getPowerDeliveries() {
		return powerDeliveries;
	}

	public void setPowerDeliveries(List<IPowerDelivery> powerDeliveries) {
		this.powerDeliveries = powerDeliveries;
	}

	public RatedLoad getRatedLoad() {
		return ratedLoad;
	}

	public void setRatedLoad(RatedLoad ratedLoad) {
		this.ratedLoad = ratedLoad;
	}

}
