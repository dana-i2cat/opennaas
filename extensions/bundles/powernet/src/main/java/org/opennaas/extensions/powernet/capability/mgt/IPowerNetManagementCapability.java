package org.opennaas.extensions.powernet.capability.mgt;

import javax.ws.rs.Path;

import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.gim.controller.ModelElementNotFoundException;
import org.opennaas.extensions.gim.model.core.IPowerConsumer;
import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.core.IPowerSupply;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
@Path("/")
public interface IPowerNetManagementCapability extends ICapability {

	// PowerSupplies CRUD
	/**
	 * @param id
	 * @return supply id
	 */
	public String createPowerSupply(String id);

	public void deletePowerSupply(String supplyId) throws ModelElementNotFoundException;

	public IPowerSupply getPowerSupply(String supplyId) throws ModelElementNotFoundException;

	public void setPowerSupplyEnergy(String supplyId, String energyName, String energyClass, String energyType, double co2perUnit,
			double greenPercentage) throws ModelElementNotFoundException;

	public void setPowerSupplyPrice(String supplyId, double pricePerUnit) throws ModelElementNotFoundException;

	public void setPowerSupplyRatedLoad(String supplyId, double inputVoltage, double inputCurrent, double inputPower, double inputEnergy)
			throws ModelElementNotFoundException;

	// PowerDeliveries CRUD

	/**
	 * @param id
	 * @return delivery id
	 */
	public String createPowerDelivery(String id);

	public void deletePowerDelivery(String deliveryId) throws ModelElementNotFoundException;

	public IPowerDelivery getPowerDelivery(String deliveryId) throws ModelElementNotFoundException;

	public void setPowerDeliveryRatedLoad(String deliveryId, double inputVoltage, double inputCurrent, double inputPower, double inputEnergy,
			double outputVoltage, double outputCurrent) throws ModelElementNotFoundException;

	// PowerConsumers CRUD
	/**
	 * @param id
	 * @return consumer id
	 */
	public String createPowerConsumer(String id);

	public void deletePowerConsumer(String consumerId) throws ModelElementNotFoundException;

	public IPowerConsumer getPowerConsumer(String consumerId) throws ModelElementNotFoundException;

	public void setPowerConsumerRatedLoad(String consumerId, double inputVoltage, double inputCurrent, double inputPower, double inputEnergy)
			throws ModelElementNotFoundException;

	// connections
	/**
	 * Connects an existing supply with an existing delivery.
	 * <p/>
	 * Applying this actions represents the fact that given delivery is taking power from given supply.
	 * 
	 * @param supplyId
	 * @param deliveryId
	 */
	public void connectSupplyDelivery(String supplyId, String deliveryId) throws ModelElementNotFoundException;

	/**
	 * Connects an existing consumer with an existing delivery.
	 * <p/>
	 * Applying this actions represents the fact that given consumer is taking power from given delivery.
	 * 
	 * @param deliveryId
	 * @param consumerId
	 */
	public void connectDeliveryConsumer(String deliveryId, String consumerId) throws ModelElementNotFoundException;

	public void disconnectSupplyDelivery(String supplyId, String deliveryId) throws ModelElementNotFoundException;

	public void disconnectDeliveryConsumer(String deliveryId, String consumerId) throws ModelElementNotFoundException;

}
