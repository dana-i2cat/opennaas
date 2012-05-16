package org.opennaas.extensions.ws.impl;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.extensions.roadm.capability.monitoring.IMonitoringCapability;
import org.opennaas.extensions.roadm.capability.monitoring.MonitoringCapability;
import org.opennaas.extensions.ws.services.IMonitoringCapabilityService;

/**
 * 
 * @author Eli Rigol
 * 
 */
@WebService(portName = "MonitoringCapabilityPort", serviceName = "MonitoringCapabilityService", targetNamespace = "http:/www.opennaas.org/ws")
public class MonitoringCapabilityServiceImpl extends GenericCapabilityService implements IMonitoringCapabilityService {

	Log	log	= LogFactory.getLog(MonitoringCapabilityServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IMonitoringCapabilityService#clearAlarms()
	 */
	@Override
	public void clearAlarms(String resourceId) throws CapabilityException {

		log.info("Start of clearAlarms call");
		IMonitoringCapability iMonitoringCapability = (IMonitoringCapability) getCapability(resourceId, MonitoringCapability.class);
		iMonitoringCapability.clearAlarms();
		log.info("End of clearAlarms call");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennaas.extensions.ws.services.IMonitoringCapabilityService#getAlarms()
	 */
	// TODO add when serialization for ResourceAlarm has been done
	// @Override
	// public List<ResourceAlarm> getAlarms(String resourceId) throws CapabilityException {
	// IMonitoringCapability iMonitoringCapability = (IMonitoringCapability) getCapability(resourceId, MonitoringCapability.CAPABILITY_NAME);
	// return iMonitoringCapability.getAlarms();
	// }

}
