package org.opennaas.extensions.power.capabilities;

import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.gim.controller.capabilities.IPowerSupplyController;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.power.capabilities.driver.ConsumerDriverInstantiator;

public class PowerSupplyCapability extends AbstractPowerConsumerCapability implements IPowerSupplyCapability {

	public static String					CAPABILITY_TYPE	= "consumer_pw_sup";
	
	private String							resourceId		= "";
	
	private IPowerSupplyController driver;
	
	public PowerSupplyCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public Energy getAggregatedEnergy() throws Exception {
		return getDriver().getAggregatedEnergy();
	}

	@Override
	public double getAggregatedPricePerEnergyUnit() throws Exception {
		return getDriver().getAggregatedPricePerEnergyUnit();
	}

	@Override
	public void resyncModel() throws Exception {
		//Nothing to do
	}
	
	private IPowerSupplyController getDriver() throws Exception {
		// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
		if (driver == null)
			driver = instantiateDriver();

		return driver;
	}

	// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
	private IPowerSupplyController instantiateDriver() throws Exception {

		// FIXME PDUDriverInstantiator should be unknown for the capability
		// capability should take the driver from an OSGI service.
		return (IPowerSupplyController) ConsumerDriverInstantiator.create(resourceId, getPowernetId(), consumerId, descriptor);
	}
	
	
	

}
