package gim.core;

import gim.load.RatedLoad;

import java.util.List;

/**
 * An interface to announce implementors have an IPowerDelivery.
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public interface IPowerConsumer {

	/**
	 * 
	 * @return @code{IPowerDelivery} this system gets energy from.
	 */
	public List<IPowerDelivery> getPowerDeliveries();

	/**
	 * 
	 * @return the load this consumer is designated to handle.
	 */
	public RatedLoad getRatedLoad();

}
