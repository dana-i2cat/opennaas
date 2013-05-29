package gim.core;

import gim.load.DeliveryRatedLoad;

import java.util.List;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
public interface IPowerDelivery {

	/**
	 * 
	 * @return @code{IPowerSupply}s this system takes energy from.
	 */
	public List<IPowerSupply> getPowerSupplies();

	/**
	 * 
	 * @return @code{IPowerConsumer}s this system delivers energy to.
	 */
	public List<IPowerConsumer> getPowerConsumers();

	/**
	 * 
	 * @return the DeliveryRatedLoad (both input and output) this system is designated to handle
	 */
	public DeliveryRatedLoad getDeliveryRatedLoad();

}
