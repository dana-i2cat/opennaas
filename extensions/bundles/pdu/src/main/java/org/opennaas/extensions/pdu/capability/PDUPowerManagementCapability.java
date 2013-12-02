package org.opennaas.extensions.pdu.capability;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.gim.controller.capabilities.IPDUPowerManagementController;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;
import org.opennaas.extensions.pdu.Activator;
import org.opennaas.extensions.pdu.model.PDUModel;

public class PDUPowerManagementCapability extends AbstractPDUCapability implements IPDUPowerManagementIDsCapability {

	private static Log						log				= LogFactory.getLog(PDUPowerManagementCapability.class);

	public static String					CAPABILITY_TYPE	= "pdu_pw_mgt";
	private String							resourceId		= "";

	private IPDUPowerManagementController	driver;

	public PDUPowerManagementCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new PDUPowerManagementCapability Capability");
	}

	@Override
	public void activate() throws CapabilityException {
		// try{
		// driver = instantiateDriver();
		// } catch (Exception e) {
		// throw new CapabilityException(e);
		// }
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(),
				IPDUPowerManagementIDsCapability.class.getName());
		super.activate();
	}

	@Override
	public void deactivate() throws CapabilityException {
		driver = null;
		unregisterService();
		super.deactivate();
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	// IPDUPowerManagementController methods

	/**
	 * 
	 * @return true if power is on, false otherwise.
	 * @throws Exception
	 */
	public boolean getPowerStatus(String portId) throws Exception {
		return getDriver().getPowerStatus(portId);
	}

	/**
	 * Turn on power.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean powerOn(String portId) throws Exception {
		if (getPowerStatus(portId))
			throw new Exception("Already powered on");
		return getDriver().powerOn(portId);
	}

	/**
	 * Turn off power.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean powerOff(String portId) throws Exception {
		if (!getPowerStatus(portId))
			throw new Exception("Already powered off");
		return getDriver().powerOff(portId);
	}

	private IPDUPowerManagementController getDriver() throws Exception {
		// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
		if (driver == null)
			driver = instantiateDriver();

		return driver;
	}

	// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
	private IPDUPowerManagementController instantiateDriver() throws Exception {

		String ip = getCapabilityDescriptor().getPropertyValue("pdu.driver.ipaddress");
		String deliveryId = getCapabilityDescriptor().getPropertyValue("powernet.delivery.id");

		// FIXME PDUDriverInstantiator should be unknown for the capability
		// capability should take the driver from an OSGI service.
		return PDUDriverInstantiator.create(resourceId, getPowernetId(), deliveryId, ip);
	}

	private PDU getPdu() {
		return ((PDUModel) resource.getModel()).getPdu();
	}

	@Override
	public void resyncModel() throws Exception {
		List<PDUPort> pduPorts = getDriver().listPorts();
		PDU pdu = getPdu();
		pdu.setPowerSources(pduPorts);
	}

}
