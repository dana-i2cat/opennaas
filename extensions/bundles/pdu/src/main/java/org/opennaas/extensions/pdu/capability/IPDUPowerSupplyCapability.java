package org.opennaas.extensions.pdu.capability;

import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.gim.model.energy.Energy;

public interface IPDUPowerSupplyCapability extends ICapability {

	public Energy getAggregatedEnergy() throws Exception;

	public double getAggregatedPricePerEnergyUnit() throws Exception;

}
