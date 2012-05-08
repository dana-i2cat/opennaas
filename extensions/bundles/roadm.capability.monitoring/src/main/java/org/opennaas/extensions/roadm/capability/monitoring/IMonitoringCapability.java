package org.opennaas.extensions.roadm.capability.monitoring;

import java.util.List;

import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

public interface IMonitoringCapability extends ICapability {

	/**
	 * Clear the alarms of the resource
	 * 
	 * @throws CapabilityException
	 */
	public void clearAlarms() throws CapabilityException;

	/**
	 * Get the alarms of the resource
	 * 
	 * @throws CapabilityException
	 */
	public List<ResourceAlarm> getAlarms() throws CapabilityException;
}
