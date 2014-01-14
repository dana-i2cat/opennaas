package org.opennaas.core.resources.alarms;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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