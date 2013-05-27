package gim.resources;

import gim.IPowerConsumer;
import gim.IPowerDelivery;

import java.util.List;

/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 15:41:49
 */
public class NetworkResource extends ONS_Resource implements IPowerConsumer {

	public PowerDeliveryResource	m_PowerDeliveryResource;
	private List<IPowerDelivery>	powerDeliveries;

	public NetworkResource() {

	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	public List<IPowerDelivery> getPowerDeliveries() {
		return powerDeliveries;
	}

	public void setPowerDeliveries(List<IPowerDelivery> powerDelivery) {
		this.powerDeliveries = powerDelivery;
	}

}