package org.opennaas.extensions.power.capabilities;

import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.gim.model.energy.Energy;

public interface IPowerSupplyCapability extends ICapability {
	
	public Energy getAggregatedEnergy() throws Exception;

	public double getAggregatedPricePerEnergyUnit() throws Exception;

}
