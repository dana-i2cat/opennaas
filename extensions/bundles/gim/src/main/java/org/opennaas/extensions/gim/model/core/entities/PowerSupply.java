package org.opennaas.extensions.gim.model.core.entities;



import java.util.List;

import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.core.IPowerMonitorLogging;
import org.opennaas.extensions.gim.model.core.IPowerSupply;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.RatedLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;


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
