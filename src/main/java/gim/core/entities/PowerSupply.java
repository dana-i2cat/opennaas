package gim.core.entities;

import gim.core.IPowerDelivery;
import gim.core.IPowerMonitorLogging;
import gim.core.IPowerSupply;
import gim.energy.Energy;
import gim.load.RatedLoad;
import gim.log.PowerMonitorLog;

import java.util.List;

public class PowerSupply implements IPowerSupply, IPowerMonitorLogging {

	private PowerMonitorLog			powerMonitorLog;
	private Energy					energy;
	private double					pricePerUnit;
	private RatedLoad				ratedLoad;
	private List<IPowerDelivery>	powerDeliveries;

	public PowerMonitorLog getPowerMonitorLog() {
		return powerMonitorLog;
	}

	public void setPowerMonitorLog(PowerMonitorLog powerMonitorLog) {
		this.powerMonitorLog = powerMonitorLog;
	}

	public Energy getEnergy() {
		return energy;
	}

	public void setEnergy(Energy energy) {
		this.energy = energy;
	}

	public double getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(double pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public RatedLoad getRatedLoad() {
		return ratedLoad;
	}

	public void setRatedLoad(RatedLoad ratedLoad) {
		this.ratedLoad = ratedLoad;
	}

	/**
	 * @return the powerDeliveries
	 */
	public List<IPowerDelivery> getPowerDeliveries() {
		return powerDeliveries;
	}

	/**
	 * @param powerDeliveries
	 *            the powerDeliveries to set
	 */
	public void setPowerDeliveries(List<IPowerDelivery> powerDeliveries) {
		this.powerDeliveries = powerDeliveries;
	}

}
