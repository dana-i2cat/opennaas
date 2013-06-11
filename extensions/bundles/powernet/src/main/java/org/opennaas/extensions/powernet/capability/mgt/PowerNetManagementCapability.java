package org.opennaas.extensions.powernet.capability.mgt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.gim.controller.GIMController;
import org.opennaas.extensions.gim.controller.ModelElementNotFoundException;
import org.opennaas.extensions.gim.model.core.IPowerConsumer;
import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.core.IPowerSupply;
import org.opennaas.extensions.gim.model.core.entities.GIModel;
import org.opennaas.extensions.gim.model.core.entities.PowerConsumer;
import org.opennaas.extensions.gim.model.core.entities.PowerDelivery;
import org.opennaas.extensions.gim.model.core.entities.PowerSupply;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.energy.EnergyClass;
import org.opennaas.extensions.gim.model.energy.EnergyType;
import org.opennaas.extensions.gim.model.load.RatedLoad;
import org.opennaas.extensions.gim.model.load.DeliveryRatedLoad;

public class PowerNetManagementCapability extends AbstractCapability implements IPowerNetManagementCapability {

	public static String	CAPABILITY_TYPE	= "powernet_mgt";

	Log						log				= LogFactory.getLog(PowerNetManagementCapability.class);

	private String			resourceId		= "";
	private IResource		resource;

	public PowerNetManagementCapability(CapabilityDescriptor descriptor, String resourceId) {

		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new PowerNetManagement Capability");
	}

	@Override
	public void activate() throws CapabilityException {
		// registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(),
		// IPowerNetManagementCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		// unregisterService();
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
		((GIModel) resource.getModel()).getSupplies().add(supply);
		return supply.getId();
	}

	@Override
	public void deletePowerSupply(String supplyId) throws ModelElementNotFoundException {
		IPowerSupply supply = getPowerSupply(supplyId);
		// disconnect it
		if (!supply.getPowerDeliveries().isEmpty()) {
			for (int i = 0; i < supply.getPowerDeliveries().size(); i++) {
				disconnectSupplyDelivery(supplyId, supply.getPowerDeliveries().get(i).getId());
			}
		}

		((GIModel) resource.getModel()).getSupplies().remove(supply);
	}

	@Override
	public IPowerSupply getPowerSupply(String supplyId) throws ModelElementNotFoundException {
		return GIMController.getPowerSupply((GIModel) resource.getModel(), supplyId);
	}

	@Override
	public void setPowerSupplyEnergy(String supplyId, String energyName, String energyClass, String energyType, double co2perUnit,
			double greenPercentage) throws ModelElementNotFoundException {

		PowerSupply supply = (PowerSupply) getPowerSupply(supplyId);

		EnergyClass eClass = EnergyClass.fromString(energyClass);
		EnergyType eType = EnergyType.fromString(energyType);

		Energy energy = new Energy(eClass, eType, co2perUnit, greenPercentage);
		supply.setEnergy(energy);
	}

	@Override
	public void setPowerSupplyPrice(String supplyId, double pricePerUnit) throws ModelElementNotFoundException {
		((PowerSupply) getPowerSupply(supplyId)).setPricePerUnit(pricePerUnit);
	}

	@Override
	public void setPowerSupplyRatedLoad(String supplyId, double inputVoltage, double inputCurrent, double inputPower, double inputEnergy)
			throws ModelElementNotFoundException {

		PowerSupply supply = (PowerSupply) getPowerSupply(supplyId);

		RatedLoad load = new RatedLoad();
		load.setVoltage(inputVoltage);
		load.setCurrent(inputCurrent);
		load.setPower(inputPower);
		load.setEnergy(inputEnergy);

		supply.setRatedLoad(load);
	}

	@Override
	public String createPowerDelivery(String id) {
		PowerDelivery delivery = new PowerDelivery();
		delivery.setId(id);
		((GIModel) resource.getModel()).getDeliveries().add(delivery);
		return delivery.getId();
	}

	@Override
	public void deletePowerDelivery(String deliveryId) throws ModelElementNotFoundException {
		IPowerDelivery delivery = getPowerDelivery(deliveryId);
		// disconnect it from consumers
		if (!delivery.getPowerConsumers().isEmpty()) {
			for (int i = 0; i < delivery.getPowerConsumers().size(); i++) {
				disconnectDeliveryConsumer(deliveryId, delivery.getPowerConsumers().get(i).getId());
			}
		}

		// disconnect it from supplies
		if (!delivery.getPowerSupplies().isEmpty()) {
			for (int i = 0; i < delivery.getPowerSupplies().size(); i++) {
				disconnectSupplyDelivery(delivery.getPowerSupplies().get(i).getId(), deliveryId);
			}
		}

		((GIModel) resource.getModel()).getDeliveries().remove(delivery);
	}

	@Override
	public IPowerDelivery getPowerDelivery(String deliveryId) throws ModelElementNotFoundException {
		return GIMController.getPowerDelivery((GIModel) resource.getModel(), deliveryId);
	}

	@Override
	public void setPowerDeliveryRatedLoad(String deliveryId, double inputVoltage, double inputCurrent, double inputPower, double inputEnergy,
			double outputVoltage, double outputCurrent) throws ModelElementNotFoundException {

		PowerDelivery delivery = (PowerDelivery) getPowerDelivery(deliveryId);

		DeliveryRatedLoad load = new DeliveryRatedLoad();
		load.setVoltage(inputVoltage);
		load.setCurrent(inputCurrent);
		load.setPower(inputPower);
		load.setEnergy(inputEnergy);

		load.setOutputVoltage(outputVoltage);
		load.setOutputCurrent(outputCurrent);

		delivery.setDeliveryRatedLoad(load);
	}

	@Override
	public String createPowerConsumer(String id) {
		PowerConsumer consumer = new PowerConsumer();
		consumer.setId(id);
		((GIModel) resource.getModel()).getConsumers().add(consumer);
		return consumer.getId();
	}

	@Override
	public void deletePowerConsumer(String consumerId) throws ModelElementNotFoundException {
		IPowerConsumer consumer = getPowerConsumer(consumerId);
		// disconnect it from deliveries
		if (!consumer.getPowerDeliveries().isEmpty()) {
			for (int i = 0; i < consumer.getPowerDeliveries().size(); i++) {
				disconnectDeliveryConsumer(consumer.getPowerDeliveries().get(i).getId(), consumerId);
			}
		}

		((GIModel) resource.getModel()).getConsumers().remove(consumer);
	}

	@Override
	public IPowerConsumer getPowerConsumer(String consumerId) throws ModelElementNotFoundException {
		return GIMController.getPowerConsumer((GIModel) resource.getModel(), consumerId);
	}

	@Override
	public void setPowerConsumerRatedLoad(String consumerId, double inputVoltage, double inputCurrent, double inputPower, double inputEnergy)
			throws ModelElementNotFoundException {

		PowerConsumer consumer = (PowerConsumer) getPowerConsumer(consumerId);

		RatedLoad load = new RatedLoad();
		load.setVoltage(inputVoltage);
		load.setCurrent(inputCurrent);
		load.setPower(inputPower);
		load.setEnergy(inputEnergy);

		consumer.setRatedLoad(load);
	}

	@Override
	public void connectSupplyDelivery(String supplyId, String deliveryId) throws ModelElementNotFoundException {

		PowerSupply supply = (PowerSupply) getPowerSupply(supplyId);
		PowerDelivery delivery = (PowerDelivery) getPowerDelivery(deliveryId);

		supply.getPowerDeliveries().add(delivery);
		delivery.getPowerSupplies().add(supply);
	}

	@Override
	public void connectDeliveryConsumer(String deliveryId, String consumerId) throws ModelElementNotFoundException {
		PowerConsumer consumer = (PowerConsumer) getPowerConsumer(consumerId);
		PowerDelivery delivery = (PowerDelivery) getPowerDelivery(deliveryId);

		delivery.getPowerConsumers().add(consumer);
		consumer.getPowerDeliveries().add(delivery);
	}

	@Override
	public void disconnectSupplyDelivery(String supplyId, String deliveryId) throws ModelElementNotFoundException {
		PowerSupply supply = (PowerSupply) getPowerSupply(supplyId);
		PowerDelivery delivery = (PowerDelivery) getPowerDelivery(deliveryId);

		supply.getPowerDeliveries().remove(delivery);
		delivery.getPowerSupplies().remove(supply);
	}

	@Override
	public void disconnectDeliveryConsumer(String deliveryId, String consumerId) throws ModelElementNotFoundException {
		PowerConsumer consumer = (PowerConsumer) getPowerConsumer(consumerId);
		PowerDelivery delivery = (PowerDelivery) getPowerDelivery(deliveryId);

		delivery.getPowerConsumers().remove(consumer);
		consumer.getPowerDeliveries().remove(delivery);
	}
}
