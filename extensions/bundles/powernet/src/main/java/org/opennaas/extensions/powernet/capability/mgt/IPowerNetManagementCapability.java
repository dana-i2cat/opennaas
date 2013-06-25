package org.opennaas.extensions.powernet.capability.mgt;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.gim.controller.ModelElementNotFoundException;
import org.opennaas.extensions.gim.model.core.entities.PowerConsumer;
import org.opennaas.extensions.gim.model.core.entities.PowerDelivery;
import org.opennaas.extensions.gim.model.core.entities.PowerSupply;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.DeliveryRatedLoad;
import org.opennaas.extensions.gim.model.load.RatedLoad;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * 
 */
@Path("/")
public interface IPowerNetManagementCapability extends ICapability {

	// PowerSupplies CRUD

	/**
	 * 
	 * @return List of existing power supplies ids.
	 */
	@Path("/supply/")
	@GET
	public List<String> getPowerSupplies();

	/**
	 * @param id
	 * @return supply id
	 */
	@Path("/supply/{id}")
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public String createPowerSupply(@PathParam("id") String id);

	@Path("/supply/{id}")
	@DELETE
	public void deletePowerSupply(@PathParam("id") String supplyId) throws ModelElementNotFoundException;

	@Path("/supply/{id}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public PowerSupply getPowerSupply(@PathParam("id") String supplyId) throws ModelElementNotFoundException;

	public void setPowerSupplyEnergy(@PathParam("id") String supplyId, String energyName, String energyClass, String energyType, double co2perUnit,
			double greenPercentage) throws ModelElementNotFoundException;

	@Path("/supply/{id}/energy")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void setPowerSupplyEnergy(@PathParam("id") String supplyId, Energy energy) throws ModelElementNotFoundException;

	@Path("/supply/{id}/price")
	@POST
	public void setPowerSupplyPrice(@PathParam("id") String supplyId, double pricePerUnit) throws ModelElementNotFoundException;

	public void setPowerSupplyRatedLoad(@PathParam("id") String supplyId, double inputVoltage, double inputCurrent, double inputPower,
			double inputEnergy)
			throws ModelElementNotFoundException;

	@Path("/supply/{id}/load")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void setPowerSupplyRatedLoad(@PathParam("id") String supplyId, RatedLoad ratedLoad)
			throws ModelElementNotFoundException;

	// PowerDeliveries CRUD

	/**
	 * 
	 * @return List of existing power deliveries ids.
	 */
	@Path("/delivery/")
	@GET
	public List<String> getPowerDeliveries();

	/**
	 * @param id
	 * @return delivery id
	 */
	@Path("/delivery/{id}")
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public String createPowerDelivery(@PathParam("id") String id);

	@Path("/delivery/{id}")
	@DELETE
	public void deletePowerDelivery(@PathParam("id") String deliveryId) throws ModelElementNotFoundException;

	@Path("/delivery/{id}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public PowerDelivery getPowerDelivery(@PathParam("id") String deliveryId) throws ModelElementNotFoundException;

	public void setPowerDeliveryRatedLoad(@PathParam("id") String deliveryId, double inputVoltage, double inputCurrent, double inputPower,
			double inputEnergy,
			double outputVoltage, double outputCurrent) throws ModelElementNotFoundException;

	@Path("/delivery/{id}/load")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void setPowerDeliveryRatedLoad(@PathParam("id") String deliveryId, DeliveryRatedLoad load) throws ModelElementNotFoundException;

	// PowerConsumers CRUD

	/**
	 * 
	 * @return List of existing power consumers ids.
	 */
	@Path("/consumer/")
	@GET
	public List<String> getPowerConsumers();

	/**
	 * @param id
	 * @return consumer id
	 */
	@Path("/consumer/{id}")
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public String createPowerConsumer(@PathParam("id") String id);

	@Path("/consumer/{id}")
	@DELETE
	public void deletePowerConsumer(@PathParam("id") String consumerId) throws ModelElementNotFoundException;

	@Path("/consumer/{id}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public PowerConsumer getPowerConsumer(@PathParam("id") String consumerId) throws ModelElementNotFoundException;

	public void setPowerConsumerRatedLoad(@PathParam("id") String consumerId, double inputVoltage, double inputCurrent, double inputPower,
			double inputEnergy)
			throws ModelElementNotFoundException;

	@Path("/consumer/{id}/load")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void setPowerConsumerRatedLoad(@PathParam("id") String consumerId, RatedLoad ratedLoad)
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
	@Path("/connectSupplyDelivery/")
	@POST
	public void connectSupplyDelivery(@QueryParam("supplyId") String supplyId, @QueryParam("deliveryId") String deliveryId)
			throws ModelElementNotFoundException;

	/**
	 * Connects an existing consumer with an existing delivery.
	 * <p/>
	 * Applying this actions represents the fact that given consumer is taking power from given delivery.
	 * 
	 * @param deliveryId
	 * @param consumerId
	 */
	@Path("/connectDeliveryConsumer/")
	@POST
	public void connectDeliveryConsumer(@QueryParam("deliveryId") String deliveryId, @QueryParam("consumerId") String consumerId)
			throws ModelElementNotFoundException;

	@Path("/disconnectSupplyDelivery/")
	@POST
	public void disconnectSupplyDelivery(@QueryParam("supplyId") String supplyId, @QueryParam("deliveryId") String deliveryId)
			throws ModelElementNotFoundException;

	@Path("/disconnectDeliveryConsumer/")
	@POST
	public void disconnectDeliveryConsumer(@QueryParam("deliveryId") String deliveryId, @QueryParam("consumerId") String consumerId)
			throws ModelElementNotFoundException;

}
