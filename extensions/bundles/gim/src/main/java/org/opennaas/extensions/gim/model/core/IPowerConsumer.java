package org.opennaas.extensions.gim.model.core;



import java.util.List;

import org.opennaas.extensions.gim.model.load.RatedLoad;


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
