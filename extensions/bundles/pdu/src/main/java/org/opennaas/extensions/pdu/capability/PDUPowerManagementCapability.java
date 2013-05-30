package org.opennaas.extensions.pdu.capability;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.pdu.Activator;
import org.opennaas.extensions.pdu.model.PDUModel;

import org.opennaas.extensions.gim.controller.capabilities.IPDUPowerManagementCapability;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;


public class PDUPowerManagementCapability extends AbstractNotQueueingCapability implements IPDUPowerManagementCapability {

	private static Log						log				= LogFactory.getLog(PDUPowerManagementCapability.class);

	public static String					CAPABILITY_TYPE	= "pdu_pw_mgt";
	private String							resourceId		= "";

	private IPDUPowerManagementCapability	driver;

	public PDUPowerManagementCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Example Capability");
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IPDUPowerManagementCapability.class.getName());
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

	// IPDUPowerManagementCapability methods

	/**
	 * 
	 * @return true if power is on, false otherwise.
	 * @throws Exception
	 */
	public boolean getPowerStatus(PDUPort port) throws Exception {
		return getDriver().getPowerStatus(port);
	}

	/**
	 * Turn on power.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean powerOn(PDUPort port) throws Exception {
		return getDriver().powerOn(port);
	}

	/**
	 * Turn off power.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean powerOff(PDUPort port) throws Exception {
		return getDriver().powerOff(port);
	}

	private IPDUPowerManagementCapability getDriver() throws Exception {
		// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
		if (driver == null)
			driver = instantiateDriver();

		return driver;
	}

	// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
	private IPDUPowerManagementCapability instantiateDriver() throws Exception {

		String ip = getCapabilityDescriptor().getPropertyValue("pdu.driver.ipaddress");
		PDU pdu = ((PDUModel) resource.getModel()).getPdu();

		// FIXME PDUDriverInstantiator should be unknown for the capability
		// capability should take the driver from an OSGI service.
		return PDUDriverInstantiator.create(pdu, ip);
	}

}
