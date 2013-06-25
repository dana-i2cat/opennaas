package org.opennaas.extensions.gim.model.core;

import java.util.List;

import org.opennaas.extensions.gim.model.core.entities.PowerConsumer;
import org.opennaas.extensions.gim.model.core.entities.PowerSupply;
import org.opennaas.extensions.gim.model.load.DeliveryRatedLoad;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public interface IPowerDelivery {

	public String getId();

	/**
	 * 
	 * @return @code{IPowerSupply}s this system takes energy from.
	 */
	public List<PowerSupply> getPowerSupplies();

	/**
	 * 
	 * @return @code{IPowerConsumer}s this system delivers energy to.
	 */
	public List<PowerConsumer> getPowerConsumers();

	/**
	 * 
	 * @return the DeliveryRatedLoad (both input and output) this system is designated to handle
	 */
	public DeliveryRatedLoad getDeliveryRatedLoad();

	public String toString();

}
