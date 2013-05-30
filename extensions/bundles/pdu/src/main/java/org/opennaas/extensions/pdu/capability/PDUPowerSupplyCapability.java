package org.opennaas.extensions.pdu.capability;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.pdu.Activator;
import org.opennaas.extensions.pdu.model.PDUModel;


import org.opennaas.extensions.gim.controller.capabilities.IPowerSupplyCapability;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;
import org.opennaas.extensions.gim.model.energy.Energy;

public class PDUPowerSupplyCapability extends AbstractNotQueueingCapability implements IPowerSupplyCapability {

	private static Log				log				= LogFactory.getLog(PDUPowerSupplyCapability.class);

	public static String			CAPABILITY_TYPE	= "pdu_pw_sup";
	private String					resourceId		= "";

	private IPowerSupplyCapability	driver;

	public PDUPowerSupplyCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Example Capability");
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IPowerSupplyCapability.class.getName());
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

	// IPowerSupplyCapability methods

	public Energy getAggregatedEnergy() throws Exception {
		return getDriver().getAggregatedEnergy();
	}

	public double getAggregatedPricePerEnergyUnit() throws Exception {
		return getDriver().getAggregatedPricePerEnergyUnit();
	}

	private IPowerSupplyCapability getDriver() throws Exception {
		// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
		if (driver == null)
			driver = instantiateDriver();

		return driver;
	}

	// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
	private IPowerSupplyCapability instantiateDriver() throws Exception {

		String ip = getCapabilityDescriptor().getPropertyValue("pdu.driver.ipaddress");
		PDU pdu = ((PDUModel) resource.getModel()).getPdu();

		// FIXME PDUDriverInstantiator should be unknown for the capability
		// capability should take the driver from an OSGI service.
		return PDUDriverInstantiator.create(pdu, ip);
	}

}
