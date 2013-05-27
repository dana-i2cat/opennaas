package gim.resources;

import gim.IPowerDelivery;
import gim.IPowerSupply;
import gim.load.MeasuredLoad;

import java.util.List;

/**
 * A NetworkResource delegating IPowerDelivery methods to the first power delivery system in getPowerDelivery().
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public class SinglePoweredNetworkResource extends NetworkResource implements IPowerDelivery {

	public MeasuredLoad getCurrentPowerMetrics() throws Exception {
		return getPowerDeliveries().get(0).getCurrentPowerMetrics();
	}

	public boolean getPowerStatus() throws Exception {
		return getSiglePowerDelivery().getPowerStatus();
	}

	public boolean powerOn() throws Exception {
		return getSiglePowerDelivery().powerOn();
	}

	public boolean powerOff() throws Exception {
		return getSiglePowerDelivery().powerOff();
	}

	public List<IPowerSupply> getPowerSupplies() {
		try {
			return getSiglePowerDelivery().getPowerSupplies();
		} catch (Exception e) {
			return null;
		}
	}

	private IPowerDelivery getSiglePowerDelivery() throws Exception {
		if (getPowerDeliveries().isEmpty())
			throw new Exception("Unsupported Operation");
		return getPowerDeliveries().get(0);
	}

}
