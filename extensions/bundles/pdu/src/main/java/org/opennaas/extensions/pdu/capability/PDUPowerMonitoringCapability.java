package org.opennaas.extensions.pdu.capability;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.gim.controller.capabilities.IPDUPowerMonitoringCapability;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDU;
import org.opennaas.extensions.gim.model.core.entities.pdu.PDUPort;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;
import org.opennaas.extensions.pdu.Activator;
import org.opennaas.extensions.pdu.model.PDUModel;

public class PDUPowerMonitoringCapability extends AbstractPDUCapability implements IPDUPowerMonitoringIDsCapability {

	private static Log						log				= LogFactory.getLog(PDUPowerMonitoringCapability.class);

	public static String					CAPABILITY_TYPE	= "pdu_pw_mon";
	private String							resourceId		= "";

	private IPDUPowerMonitoringCapability	driver;

	public PDUPowerMonitoringCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new PDUPowerMonitoringCapability Capability");
	}

	@Override
	public void activate() throws CapabilityException {
		// try{
		// driver = instantiateDriver();
		// } catch (Exception e) {
		// throw new CapabilityException(e);
		// }
		registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(),
				IPDUPowerMonitoringIDsCapability.class.getName());
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

	// IPDUPowerMonitoringCapability methods

	/**
	 * 
	 * @return return current MeasuredLoad.
	 * @throws Exception
	 */
	public MeasuredLoad getCurrentPowerMetrics(String portId) throws Exception {
		return getDriver().getCurrentPowerMetrics(portId);
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @return A PowerMonitorLog including all read @code{MesuredLoad}s from @code{from} to @code{to}, both included.
	 * @throws Exception
	 */
	public PowerMonitorLog getPowerMetricsByTimeRange(String portId, Date from, Date to) throws Exception {
		return getDriver().getPowerMetricsByTimeRange(portId, from, to);
	}

	private IPDUPowerMonitoringCapability getDriver() throws Exception {
		// FIXME CAPABILITY SHOULD NOT INSTANTIATE ITS OWN DRIVER.
		if (driver == null)
			driver = instantiateDriver();

		return driver;
	}

	// FIXME CAPABILITY SHOULD NOT INSTANTIATE ITS OWN DRIVER.
	private IPDUPowerMonitoringCapability instantiateDriver() throws Exception {

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
