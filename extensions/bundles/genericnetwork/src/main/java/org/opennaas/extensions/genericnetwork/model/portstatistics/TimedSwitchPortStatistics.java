package org.opennaas.extensions.genericnetwork.model.portstatistics;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper containing {@code SortedMap<Long, Map<String, List<TimedStatistics>>> statisticsMap}, 
 * with the key first being the timestamp and the second one the switch id.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TimedSwitchPortStatistics {

	/**
	 * timestamp, switchid, List<TimedStatistics>
	 */
	@XmlElementWrapper
	private SortedMap<Long, Map<String, List<TimedStatistics>>> statisticsMap;
	
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
