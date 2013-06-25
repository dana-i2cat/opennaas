package org.opennaas.extensions.power.capabilities;

import java.util.Date;

import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.gim.controller.capabilities.IPowerMonitoringController;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;
import org.opennaas.extensions.power.capabilities.driver.ConsumerDriverInstantiator;

public class PowerMonitoringCapability  extends AbstractPowerConsumerCapability implements IPowerMonitoringCapability {

	public static String					CAPABILITY_TYPE	= "consumer_pw_mon";
	
	private String							resourceId		= "";
	
	private IPowerMonitoringController driver;

	
	public PowerMonitoringCapability(CapabilityDescriptor descriptor, String resourceId) {
		super(descriptor);
		this.resourceId = resourceId;
	}
	
	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}
	
	@Override
	public MeasuredLoad getCurrentPowerMetrics() throws Exception {
		return getDriver().getCurrentPowerMetrics();
	}

	@Override
	public PowerMonitorLog getPowerMetricsByTimeRange(Date from, Date to)
			throws Exception {
		return getDriver().getPowerMetricsByTimeRange(from, to);
	}
	
	private IPowerMonitoringController getDriver() throws Exception {
		// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
		if (driver == null)
			driver = instantiateDriver();

		return driver;
	}

	// FIXME CAPABILITY SHOULD NOT INSTANTIATE IT'S OWN DRIVER.
	private IPowerMonitoringController instantiateDriver() throws Exception {

		// FIXME PDUDriverInstantiator should be unknown for the capability
		// capability should take the driver from an OSGI service.
		return (IPowerMonitoringController) ConsumerDriverInstantiator.create(resourceId, getPowernetId(), consumerId, descriptor);
	}

	@Override
	public void resyncModel() throws Exception {
		// Nothing to do
	}

}
