package org.opennaas.extensions.gim.model.core;

import java.util.List;

import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.RatedLoad;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public interface IPowerSupply {

	public String getId();

	/**
	 * 
	 * @return the @code{Energy} being supplied.
	 */
	public Energy getEnergy();

	/**
	 * 
	 * @return price per energy unit (i.e. € per KW)
	 */
	public double getPricePerUnit();

	/**
	 * 
	 * @return the load this is designated to supply.
	 */
	public RatedLoad getRatedLoad();

	/**
	 * 
	 * @return @code{IPowerDelivery}s this system supplies.
	 */
	public List<IPowerDelivery> getPowerDeliveries();
}
