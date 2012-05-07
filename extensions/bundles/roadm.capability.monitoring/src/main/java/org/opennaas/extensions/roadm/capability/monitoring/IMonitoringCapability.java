package org.opennaas.extensions.roadm.capability.monitoring;

import java.util.List;

import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;

public interface IMonitoringCapability extends ICapability {

	/**
	 * @throws CapabilityException
	 */
	public void clearAlarms() throws CapabilityException;

	/**
	 * @throws CapabilityException
	 */
	public List<ResourceAlarm> getAlarms() throws CapabilityException;
}
