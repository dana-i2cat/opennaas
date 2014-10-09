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
 * Wrapper containing SortedMap<Long, Map<String, List<TimedStatistics>>> statisticsMap, 
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
}
