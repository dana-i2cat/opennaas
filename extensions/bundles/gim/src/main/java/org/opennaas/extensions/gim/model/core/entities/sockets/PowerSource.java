package org.opennaas.extensions.gim.model.core.entities.sockets;

import org.opennaas.extensions.gim.model.energy.Energy;

public class PowerSource extends PowerSocket {

	private Energy	energy;
	private double	pricePerUnit;

	/**
	 * @return the energy
	 */
	public Energy getEnergy() {
		return energy;
	}

	/**
	 * @param energy
	 *            the energy to set
	 */
	public void setEnergy(Energy energy) {
		this.energy = energy;
	}

	/**
	 * @return the pricePerUnit
	 */
	public double getPricePerUnit() {
		return pricePerUnit;
	}

	/**
	 * @param pricePerUnit
	 *            the pricePerUnit to set
	 */
	public void setPricePerUnit(double pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

}
