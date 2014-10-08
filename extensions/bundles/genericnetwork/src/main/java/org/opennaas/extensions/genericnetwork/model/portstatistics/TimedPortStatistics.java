package org.opennaas.extensions.genericnetwork.model.portstatistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *  Wrapper containing  Map<String, List<TimedStatistics>>, with the key being the port id.
 *  
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TimedPortStatistics {
	
	/**
	 * portId : TimedStatistics
	 */
	@XmlElementWrapper
	private Map<String, List<TimedStatistics>> statisticsMap;
	
	/**
	 * Default constructor
	 */
	public TimedPortStatistics() {
		
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param other
	 */
	public TimedPortStatistics(TimedPortStatistics other) {
		this.statisticsMap = new HashMap<String, List<TimedStatistics>>(other.getStatisticsMap());
	}

	public Map<String, List<TimedStatistics>> getStatisticsMap() {
		return statisticsMap;
	}

	public void setStatisticsMap(HashMap<String, List<TimedStatistics>> statisticsMap) {
		this.statisticsMap = statisticsMap;
	}
}
