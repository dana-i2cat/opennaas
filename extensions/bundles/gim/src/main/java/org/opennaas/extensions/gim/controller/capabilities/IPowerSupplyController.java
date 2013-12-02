package org.opennaas.extensions.gim.controller.capabilities;

import org.opennaas.extensions.gim.model.energy.Energy;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public interface IPowerSupplyController {

	public Energy getAggregatedEnergy() throws Exception;

	public double getAggregatedPricePerEnergyUnit() throws Exception;

}
