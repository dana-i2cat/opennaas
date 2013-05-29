package controller.capabilities;

import gim.energy.Energy;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public interface IPowerSupplyCapability {

	public Energy getAggregatedEnergy();

	public double getAggregatedPricePerEnergyUnit();

}
