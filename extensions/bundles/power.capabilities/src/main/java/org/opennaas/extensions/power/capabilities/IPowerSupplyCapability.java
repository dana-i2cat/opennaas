package org.opennaas.extensions.power.capabilities;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.gim.model.energy.Energy;

@Path("/")
public interface IPowerSupplyCapability extends ICapability {

	@Path("/energy")
	@GET
	public Energy getAggregatedEnergy() throws Exception;

	@Path("/price")
	@GET
	public double getAggregatedPricePerEnergyUnit() throws Exception;

}
