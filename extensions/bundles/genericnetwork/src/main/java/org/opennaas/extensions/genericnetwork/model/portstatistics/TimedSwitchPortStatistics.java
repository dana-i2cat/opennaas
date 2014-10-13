package org.opennaas.extensions.genericnetwork.model.portstatistics;

/*
 * #%L
 * OpenNaaS :: Generic Network
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper containing {@code SortedMap<Long, Map<String, List<TimedStatistics>>> statisticsMap}, with the key first being the timestamp and the second
 * one the switch id.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimedSwitchPortStatistics {

	/**
	 * timestamp, switchid, List<TimedStatistics>
	 */
	@XmlElementWrapper
	private SortedMap<Long, Map<String, List<TimedStatistics>>>	statisticsMap;

	/**
	 * Default constructor
	 */
	public TimedSwitchPortStatistics() {

	}

	/**
	 * Copy constructor
	 * 
	 * @param other
	 */
	public TimedSwitchPortStatistics(TimedSwitchPortStatistics other) {
		this.statisticsMap = new TreeMap<Long, Map<String, List<TimedStatistics>>>(other.getStatisticsMap());
	}

	public SortedMap<Long, Map<String, List<TimedStatistics>>> getStatisticsMap() {
		return statisticsMap;
	}

	public void setStatisticsMap(
			SortedMap<Long, Map<String, List<TimedStatistics>>> statisticsMap) {
		this.statisticsMap = statisticsMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((statisticsMap == null) ? 0 : statisticsMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimedSwitchPortStatistics other = (TimedSwitchPortStatistics) obj;
		if (statisticsMap == null) {
			if (other.statisticsMap != null)
				return false;
		} else if (!statisticsMap.equals(other.statisticsMap))
			return false;
		return true;
	}

}
