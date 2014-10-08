package org.opennaas.extensions.genericnetwork.model.portstatistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper containing  Map<String, Map<String, List<TimedStatistics>>> statisticsMap, 
 * with the key first being the switch id and the second one the port id.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TimedSwitchPortStatistics {
	/**
	 * switchid: portId : TimedStatistics
	 */
	@XmlElementWrapper
	private Map<String, Map<String, List<TimedStatistics>>> statisticsMap;
	
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
		this.statisticsMap = new HashMap<String, Map<String, List<TimedStatistics>>>(other.getStatisticsMap());
	}

	public Map<String, Map<String, List<TimedStatistics>>> getStatisticsMap() {
		return statisticsMap;
	}

	public void setStatisticsMap(
			Map<String, Map<String, List<TimedStatistics>>> statisticsMap) {
		this.statisticsMap = statisticsMap;
	}
}
