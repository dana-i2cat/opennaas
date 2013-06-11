package org.opennaas.extensions.powernet.capability.mgt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.gim.controller.ModelElementNotFoundException;
import org.opennaas.extensions.gim.model.core.IPowerConsumer;
import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.core.IPowerSupply;
import org.opennaas.extensions.powernet.Activator;

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
		//registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IPowerNetManagementCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		//unregisterService();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deletePowerSupply(String supplyId) throws ModelElementNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public IPowerSupply getPowerSupply(String supplyId) throws ModelElementNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPowerSupplyEnergy(String supplyId, String energyName, String energyClass, String energyType, double co2perUnit,
			double greenPercentage) throws ModelElementNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPowerSupplyPrice(String supplyId, double pricePerUnit) throws ModelElementNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPowerSupplyRatedLoad(String supplyId, double inputVoltage, double inputCurrent, double inputPower, double inputEnergy)
			throws ModelElementNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public String createPowerDelivery(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deletePowerDelivery(String deliveryId) throws ModelElementNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public IPowerDelivery getPowerDelivery(String deliveryId) throws ModelElementNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPowerDeliveryRatedLoad(String deliveryId, double inputVoltage, double inputCurrent, double inputPower, double inputEnergy,
			double outputVoltage, double outputCurrent) throws ModelElementNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public String createPowerConsumer(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deletePowerConsumer(String consumerId) throws ModelElementNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public IPowerConsumer getPowerConsumer(String consumerId) throws ModelElementNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPowerConsumerRatedLoad(String consumerId, double inputVoltage, double inputCurrent, double inputPower, double inputEnergy)
			throws ModelElementNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectSupplyDelivery(String supplyId, String deliveryId) throws ModelElementNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectDeliveryConsumer(String deliveryId, String consumerId) throws ModelElementNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnectSupplyDelivery(String supplyId, String deliveryId) throws ModelElementNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnectDeliveryConsumer(String deliveryId, String consumerId) throws ModelElementNotFoundException {
		// TODO Auto-generated method stub

	}
}
