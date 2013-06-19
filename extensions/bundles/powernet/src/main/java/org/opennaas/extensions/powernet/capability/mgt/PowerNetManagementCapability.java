package org.opennaas.extensions.powernet.capability.mgt;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.gim.controller.GIMController;
import org.opennaas.extensions.gim.controller.ModelElementNotFoundException;
import org.opennaas.extensions.gim.model.core.entities.GIModel;
import org.opennaas.extensions.gim.model.core.entities.PowerConsumer;
import org.opennaas.extensions.gim.model.core.entities.PowerDelivery;
import org.opennaas.extensions.gim.model.core.entities.PowerSupply;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerReceptor;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.energy.EnergyClass;
import org.opennaas.extensions.gim.model.energy.EnergyType;
import org.opennaas.extensions.gim.model.load.RatedLoad;
import org.opennaas.extensions.powernet.Activator;

public class PowerNetManagementCapability extends AbstractCapability implements IPowerNetManagementCapability {

	public static String	CAPABILITY_TYPE	= "powernet_mgt";

	Log						log				= LogFactory.getLog(PowerNetManagementCapability.class);

	private String			resourceId		= "";

	public PowerNetManagementCapability(CapabilityDescriptor descriptor, String resourceId) {

		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new PowerNetManagement Capability");
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IPowerNetManagementCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		unregisterService();
		super.deactivate();
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {
		// TODO Auto-generated method stub
		return null;
	}

	// IPowerNetManagementCapability IMPLEMENTATION

	@Override
	public String createPowerSupply(String id) {
		PowerSupply supply = new PowerSupply();
		supply.setId(id);
		supply.setPowerDeliveries(new ArrayList<PowerDelivery>());
		((GIModel) resource.getModel()).getSupplies().add(supply);
		return supply.getId();
	}

	@Override
	public void deletePowerSupply(String supplyId) throws ModelElementNotFoundException {
		PowerSupply supply = getPowerSupply(supplyId);
		// disconnect it
		for (PowerSource source : supply.getPowerSources()) {
			disconnectPowerSource(source);
		}
		supply.setPowerSources(null);

		((GIModel) resource.getModel()).getSupplies().remove(supply);
	}

	@Override
	public PowerSupply getPowerSupply(String supplyId) throws ModelElementNotFoundException {
		return GIMController.getPowerSupply((GIModel) resource.getModel(), supplyId);
	}

	@Override
	public String createPowerDelivery(String id) {
		PowerDelivery delivery = new PowerDelivery();
		delivery.setId(id);
		delivery.setPowerConsumers(new ArrayList<PowerConsumer>());
		delivery.setPowerSupplies(new ArrayList<PowerSupply>());
		((GIModel) resource.getModel()).getDeliveries().add(delivery);
		return delivery.getId();
	}

	@Override
	public void deletePowerDelivery(String deliveryId) throws ModelElementNotFoundException {
		PowerDelivery delivery = getPowerDelivery(deliveryId);

		// disconnect its sources
		for (PowerSource source : delivery.getPowerSources()) {
			disconnectPowerSource(source);
		}
		delivery.setPowerSources(null);

		// disconnect its receptors
		for (PowerReceptor receptor : delivery.getPowerReceptors()) {
			receptor.setAttachedTo(null);
		}
		delivery.setPowerReceptors(null);

		((GIModel) resource.getModel()).getDeliveries().remove(delivery);
	}

	@Override
	public PowerDelivery getPowerDelivery(String deliveryId) throws ModelElementNotFoundException {
		return GIMController.getPowerDelivery((GIModel) resource.getModel(), deliveryId);
	}

	@Override
	public String createPowerConsumer(String id) {
		PowerConsumer consumer = new PowerConsumer();
		consumer.setId(id);
		consumer.setPowerDeliveries(new ArrayList<PowerDelivery>());
		((GIModel) resource.getModel()).getConsumers().add(consumer);
		return consumer.getId();
	}

	@Override
	public void deletePowerConsumer(String consumerId) throws ModelElementNotFoundException {
		PowerConsumer consumer = getPowerConsumer(consumerId);
		// disconnect it from deliveries
		for (PowerReceptor receptor : consumer.getPowerReceptors()) {
			receptor.setAttachedTo(null);
		}
		consumer.setPowerReceptors(null);

		((GIModel) resource.getModel()).getConsumers().remove(consumer);
	}

	@Override
	public PowerConsumer getPowerConsumer(String consumerId) throws ModelElementNotFoundException {
		return GIMController.getPowerConsumer((GIModel) resource.getModel(), consumerId);
	}

	@Override
	public void connectSupplyDelivery(String supplyId, String supplySourceId, String deliveryId, String delivertReceptorId)
			throws ModelElementNotFoundException {

		PowerSupply supply = getPowerSupply(supplyId);
		PowerDelivery delivery = getPowerDelivery(deliveryId);

		PowerSource source = (PowerSource) GIMController.getSocketById(supply.getPowerSources(), supplySourceId);
		PowerReceptor receptor = (PowerReceptor) GIMController.getSocketById(delivery.getPowerReceptors(), delivertReceptorId);

		receptor.setAttachedTo(source);
	}

	@Override
	public void connectDeliveryConsumer(String deliveryId, String deliverySourceId, String consumerId, String consumerReceptorId)
			throws ModelElementNotFoundException {
		PowerConsumer consumer = getPowerConsumer(consumerId);
		PowerDelivery delivery = getPowerDelivery(deliveryId);

		PowerSource source = (PowerSource) GIMController.getSocketById(delivery.getPowerSources(), deliverySourceId);
		PowerReceptor receptor = (PowerReceptor) GIMController.getSocketById(consumer.getPowerReceptors(), consumerReceptorId);

		receptor.setAttachedTo(source);
	}

	@Override
	public void disconnectSupplyDelivery(String supplyId, String supplySourceId, String deliveryId, String delivertReceptorId)
			throws ModelElementNotFoundException {
		PowerSupply supply = getPowerSupply(supplyId);
		PowerDelivery delivery = getPowerDelivery(deliveryId);

		PowerSource source = (PowerSource) GIMController.getSocketById(supply.getPowerSources(), supplySourceId);
		PowerReceptor receptor = (PowerReceptor) GIMController.getSocketById(delivery.getPowerReceptors(), delivertReceptorId);

		receptor.setAttachedTo(null);
	}

	@Override
	public void disconnectDeliveryConsumer(String deliveryId, String deliverySourceId, String consumerId, String consumerReceptorId)
			throws ModelElementNotFoundException {
		PowerConsumer consumer = getPowerConsumer(consumerId);
		PowerDelivery delivery = getPowerDelivery(deliveryId);

		PowerSource source = (PowerSource) GIMController.getSocketById(delivery.getPowerSources(), deliverySourceId);
		PowerReceptor receptor = (PowerReceptor) GIMController.getSocketById(consumer.getPowerReceptors(), consumerReceptorId);

		receptor.setAttachedTo(null);
	}

	@Override
	public List<String> getPowerSupplies() {
		List<PowerSupply> elements = ((GIModel) resource.getModel()).getSupplies();

		List<String> ids = new ArrayList<String>(elements.size());
		for (PowerSupply element : elements) {
			ids.add(element.getId());
		}
		return ids;
	}

	@Override
	public List<String> getPowerDeliveries() {
		List<PowerDelivery> elements = ((GIModel) resource.getModel()).getDeliveries();

		List<String> ids = new ArrayList<String>(elements.size());
		for (PowerDelivery element : elements) {
			ids.add(element.getId());
		}
		return ids;
	}

	@Override
	public List<String> getPowerConsumers() {
		List<PowerConsumer> elements = ((GIModel) resource.getModel()).getConsumers();

		List<String> ids = new ArrayList<String>(elements.size());
		for (PowerConsumer element : elements) {
			ids.add(element.getId());
		}
		return ids;
	}

	private void disconnectPowerSource(PowerSource powerSource) {
		// check consumers
		for (PowerConsumer consumer : ((GIModel) resource.getModel()).getConsumers()) {
			for (PowerReceptor receptor : consumer.getPowerReceptors()) {
				if (powerSource.equals(receptor.getAttachedTo()))
					receptor.setAttachedTo(null);
			}
		}
		// check deliveries
		for (PowerDelivery delivery : ((GIModel) resource.getModel()).getDeliveries()) {
			for (PowerReceptor receptor : delivery.getPowerReceptors()) {
				if (powerSource.equals(receptor.getAttachedTo()))
					receptor.setAttachedTo(null);
			}
		}
	}

	@Override
	public List<String> getPowerSupplySources(String supplyId) throws ModelElementNotFoundException {
		List<PowerSource> sources = getPowerSupply(supplyId).getPowerSources();
		List<String> ids = new ArrayList<String>(sources.size());
		for (PowerSource source : sources) {
			ids.add(source.getId());
		}

		return ids;
	}

	@Override
	public PowerSource getPowerSupplySource(String supplyId, String sourceId) throws ModelElementNotFoundException {
		return (PowerSource) GIMController.getSocketById(getPowerSupply(supplyId).getPowerSources(), sourceId);
	}

	@Override
	public String addPowerSupplySource(String supplyId, String sourceId, PowerSource source) throws ModelElementNotFoundException {
		source.setId(sourceId);
		getPowerSupply(supplyId).getPowerSources().add(source);
		return sourceId;
	}

	@Override
	public void removePowerSupplySource(String supplyId, String sourceId) throws ModelElementNotFoundException {
		getPowerSupply(supplyId).getPowerSources().remove(getPowerSupplySource(supplyId, sourceId));
	}

	@Override
	public List<String> getPowerDeliverySources(String deliveryId) throws ModelElementNotFoundException {
		List<PowerSource> sources = getPowerDelivery(deliveryId).getPowerSources();
		List<String> ids = new ArrayList<String>(sources.size());
		for (PowerSource source : sources) {
			ids.add(source.getId());
		}

		return ids;
	}

	@Override
	public PowerSource getPowerDeliverySource(String deliveryId, String sourceId) throws ModelElementNotFoundException {
		return (PowerSource) GIMController.getSocketById(getPowerDelivery(deliveryId).getPowerSources(), sourceId);
	}

	@Override
	public String addPowerDeliverySource(String deliveryId, String sourceId, PowerSource source) throws ModelElementNotFoundException {
		source.setId(sourceId);
		getPowerDelivery(deliveryId).getPowerSources().add(source);
		return sourceId;
	}

	@Override
	public void removePowerDeliverySource(String deliveryId, String sourceId) throws ModelElementNotFoundException {
		getPowerDelivery(deliveryId).getPowerSources().remove(getPowerDeliverySource(deliveryId, sourceId));
	}

	@Override
	public List<String> getPowerDeliveryReceptors(String deliveryId) throws ModelElementNotFoundException {
		List<PowerReceptor> receptors = getPowerDelivery(deliveryId).getPowerReceptors();
		List<String> ids = new ArrayList<String>(receptors.size());
		for (PowerReceptor receptor : receptors) {
			ids.add(receptor.getId());
		}

		return ids;
	}

	@Override
	public PowerReceptor getPowerDeliveryReceptor(String deliveryId, String receptorId) throws ModelElementNotFoundException {
		return (PowerReceptor) GIMController.getSocketById(getPowerDelivery(deliveryId).getPowerReceptors(), receptorId);
	}

	@Override
	public String addPowerDeliveryReceptor(String deliveryId, String receptorId, PowerReceptor receptor) throws ModelElementNotFoundException {
		receptor.setId(receptorId);
		getPowerDelivery(deliveryId).getPowerReceptors().add(receptor);
		return receptorId;
	}

	@Override
	public void removePowerDeliveryReceptor(String deliveryId, String receptorId) throws ModelElementNotFoundException {
		getPowerDelivery(deliveryId).getPowerReceptors().remove(getPowerDeliveryReceptor(deliveryId, receptorId));
	}

	@Override
	public List<String> getPowerConsumerReceptors(String consumerId) throws ModelElementNotFoundException {
		List<PowerReceptor> receptors = getPowerConsumer(consumerId).getPowerReceptors();
		List<String> ids = new ArrayList<String>(receptors.size());
		for (PowerReceptor receptor : receptors) {
			ids.add(receptor.getId());
		}

		return ids;
	}

	@Override
	public PowerReceptor getPowerConsumerReceptor(String consumerId, String receptorId) throws ModelElementNotFoundException {
		return (PowerReceptor) GIMController.getSocketById(getPowerConsumer(consumerId).getPowerReceptors(), receptorId);
	}

	@Override
	public String addPowerConsumerReceptor(String consumerId, String receptorId, PowerReceptor receptor) throws ModelElementNotFoundException {
		receptor.setId(receptorId);
		getPowerConsumer(consumerId).getPowerReceptors().add(receptor);
		return receptorId;
	}

	@Override
	public void removePowerConsumerReceptor(String consumerId, String receptorId) throws ModelElementNotFoundException {
		getPowerConsumer(consumerId).getPowerReceptors().remove(getPowerConsumerReceptor(consumerId, receptorId));
	}

	@Override
	public void setPowerSupplySourceEnergy(String supplyId, String sourceId, String energyName, String energyClass, String energyType,
			double co2perUnit, double greenPercentage) throws ModelElementNotFoundException {
		setPowerSupplySourceEnergy(supplyId, sourceId, buildEnergy(energyName, energyClass, energyType, co2perUnit, greenPercentage));
	}

	@Override
	public void setPowerSupplySourceEnergy(String supplyId, String sourceId, Energy energy) throws ModelElementNotFoundException {
		getPowerSupplySource(supplyId, sourceId).setEnergy(energy);
	}

	@Override
	public void setPowerSupplySourcePrice(String supplyId, String sourceId, double pricePerUnit) throws ModelElementNotFoundException {
		getPowerSupplySource(supplyId, sourceId).setPricePerUnit(pricePerUnit);
	}

	@Override
	public void setPowerSupplySourceRatedLoad(String supplyId, String sourceId, double inputVoltage, double inputCurrent, double inputPower,
			double inputEnergy) throws ModelElementNotFoundException {

	}

	@Override
	public void setPowerSupplySourceRatedLoad(String supplyId, String sourceId, RatedLoad ratedLoad) throws ModelElementNotFoundException {
		getPowerSupplySource(supplyId, sourceId).setRatedLoad(ratedLoad);
	}

	@Override
	public void setPowerDeliverySourceRatedLoad(String deliveryId, String sourceId, double voltage, double current, double power, double energy)
			throws ModelElementNotFoundException {
		RatedLoad load = new RatedLoad();
		load.setVoltage(voltage);
		load.setCurrent(current);
		load.setPower(power);
		load.setEnergy(energy);

		setPowerDeliverySourceRatedLoad(deliveryId, sourceId, load);
	}

	@Override
	public void setPowerDeliverySourceRatedLoad(String deliveryId, String sourceId, RatedLoad load) throws ModelElementNotFoundException {
		getPowerDeliverySource(deliveryId, sourceId).setRatedLoad(load);
	}

	@Override
	public void setPowerDeliveryReceptorRatedLoad(String deliveryId, String receptorId, RatedLoad load) throws ModelElementNotFoundException {
		getPowerDeliveryReceptor(deliveryId, receptorId).setRatedLoad(load);
	}

	@Override
	public void setPowerDeliverySourceEnergy(String deliveryId, String sourceId, String energyName, String energyClass, String energyType,
			double co2perUnit, double greenPercentage) throws ModelElementNotFoundException {
		setPowerDeliverySourceEnergy(deliveryId, sourceId, buildEnergy(energyName, energyClass, energyType, co2perUnit, greenPercentage));
	}

	@Override
	public void setPowerDeliverySourceEnergy(String deliveryId, String sourceId, Energy energy) throws ModelElementNotFoundException {
		getPowerDeliverySource(deliveryId, sourceId).setEnergy(energy);
	}

	@Override
	public void setPowerDeliverySourcePrice(String deliveryId, String sourceId, double pricePerUnit) throws ModelElementNotFoundException {
		getPowerDeliverySource(deliveryId, sourceId).setPricePerUnit(pricePerUnit);
	}

	@Override
	public void setPowerConsumerReceptorRatedLoad(String consumerId, String receptorId, RatedLoad load) throws ModelElementNotFoundException {
		getPowerConsumerReceptor(consumerId, receptorId).setRatedLoad(load);
	}

	@Override
	public void setPowerDeliveryReceptorRatedLoad(String deliveryId, String receptorId, double voltage, double current, double power, double energy)
			throws ModelElementNotFoundException {
		setPowerDeliveryReceptorRatedLoad(deliveryId, receptorId, buildRatedLoad(voltage, current, power, energy));
	}

	@Override
	public void setPowerConsumerReceptorRatedLoad(String consumerId, String receptorId, double voltage, double current, double power, double energy)
			throws ModelElementNotFoundException {
		setPowerConsumerReceptorRatedLoad(consumerId, receptorId, buildRatedLoad(voltage, current, power, energy));
	}

	private RatedLoad buildRatedLoad(double voltage, double current, double power, double energy) {
		RatedLoad load = new RatedLoad();
		load.setVoltage(voltage);
		load.setCurrent(current);
		load.setPower(power);
		load.setEnergy(energy);
		return load;
	}

	private Energy buildEnergy(String energyName, String energyClass, String energyType,
			double co2perUnit, double greenPercentage) {
		return new Energy(EnergyClass.fromString(energyClass), EnergyType.fromString(energyType), co2perUnit, greenPercentage);
	}

}
