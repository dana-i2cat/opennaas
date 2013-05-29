package gim.core;

import gim.energy.Energy;
import gim.load.RatedLoad;

import java.util.List;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public interface IPowerSupply {

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
