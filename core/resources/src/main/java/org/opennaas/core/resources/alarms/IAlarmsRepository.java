package org.opennaas.core.resources.alarms;

import java.util.List;

import org.opennaas.core.resources.ResourceNotFoundException;

public interface IAlarmsRepository {

	/**
	 * Return all ResourceAlarms in repository
	 * 
	 * @return
	 */
	public List<ResourceAlarm> getAlarms();

	/**
	 * 
	 * @param resourceId
	 * @return alarms concerning the resource with given resourceId
	 */
	public List<ResourceAlarm> getResourceAlarms(String resourceId) throws ResourceNotFoundException;

	/**
	 * 
	 * @param alarmId
	 * @return ResourceAlarm with specified Id
	 */
	public ResourceAlarm getAlarmById(String alarmId) throws ResourceNotFoundException;

	/**
	 * Add given alarm to repository
	 * 
	 * @param alarmToAdd
	 * @return alarmId
	 */
	public String addResourceAlarm(ResourceAlarm alarmToAdd, String resourceId);

	/**
	 * Removes all alarms from repository
	 */
	public void clear();

	/**
	 * Remove alarms concerning resource with given resourceid
	 * 
	 * @param resourceId
	 */
	public void clearResourceAlarms(String resourceId);

}