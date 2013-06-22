package org.opennaas.extensions.power.capabilities;

import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.gim.controller.capabilities.IPDUPowerManagementCapability;
import org.opennaas.extensions.gim.controller.capabilities.IPowerManagementCapability;
import org.opennaas.extensions.pdu.capability.PDUDriverInstantiator;


public class PowerManagementCapability extends AbstractPowerConsumerCapability implements IPowerManagementCapability {

	public static String					CAPABILITY_TYPE	= "consumer_pw_mgt";
	
	private String							resourceId		= "";
	
	private IPowerManagementCapability driver;
	
	public PowerManagementCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public boolean getPowerStatus() throws Exception {
		return getDriver().getPowerStatus();
	}

	@Override
	public boolean powerOn() throws Exception {
		if (getPowerStatus())
			throw new Exception("Already powered on");
		return getDriver().powerOn();
	}

	@Override
	public boolean powerOff() throws Exception {
		if (!getPowerStatus())
			throw new Exception("Already powered off");
		return getDriver().powerOff();
	}

	@Override
	public void resyncModel() throws Exception {
		// Nothing to do
	}
	
	
	
	private IPowerManagementCapability getDriver() throws Exception {
		// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
		if (driver == null)
			driver = instantiateDriver();

		return driver;
	}

	// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
	private IPowerManagementCapability instantiateDriver() throws Exception {

		// FIXME PDUDriverInstantiator should be unknown for the capability
		// capability should take the driver from an OSGI service.
		return ConsumerDriverInstantiator.create(resourceId, getPowernetId(), consumerId, descriptor);
	}
}
