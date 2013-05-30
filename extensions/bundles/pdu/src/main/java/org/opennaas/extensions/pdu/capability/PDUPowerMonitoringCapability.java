package org.opennaas.extensions.pdu.capability;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.pdu.Activator;
import org.opennaas.extensions.pdu.model.PDUModel;

import org.opennaas.extensions.gim.controller.capabilities.IPDUPowerMonitoringCapability;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

public class PDUPowerMonitoringCapability extends AbstractNotQueueingCapability implements IPDUPowerMonitoringCapability {

	private static Log						log				= LogFactory.getLog(PDUPowerMonitoringCapability.class);

	public static String					CAPABILITY_TYPE	= "pdu_pw_mon";
	private String							resourceId		= "";

	private IPDUPowerMonitoringCapability	driver;

	public PDUPowerMonitoringCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Example Capability");
	}

	@Override
	public void activate() throws CapabilityException {
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IPDUPowerMonitoringCapability.class.getName());
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

	// IPDUPowerMonitoringCapability methods

	/**
	 * 
	 * @return return current MeasuredLoad.
	 * @throws Exception
	 */
	public MeasuredLoad getCurrentPowerMetrics(PDUPort port) throws Exception {
		return getDriver().getCurrentPowerMetrics(port);
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @return A PowerMonitorLog including all read @code{MesuredLoad}s from @code{from} to @code{to}, both included.
	 * @throws Exception
	 */
	public PowerMonitorLog getPowerMetricsByTimeRange(PDUPort port, Date from, Date to) throws Exception {
		return getDriver().getPowerMetricsByTimeRange(port, from, to);
	}

	private IPDUPowerMonitoringCapability getDriver() throws Exception {
		// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
		if (driver == null)
			driver = instantiateDriver();

		return driver;
	}

	// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
	private IPDUPowerMonitoringCapability instantiateDriver() throws Exception {

		String ip = getCapabilityDescriptor().getPropertyValue("pdu.driver.ipaddress");
		PDU pdu = ((PDUModel) resource.getModel()).getPdu();

		// FIXME PDUDriverInstantiator should be unknown for the capability
		// capability should take the driver from an OSGI service.
		return PDUDriverInstantiator.create(pdu, ip);
	}

}
