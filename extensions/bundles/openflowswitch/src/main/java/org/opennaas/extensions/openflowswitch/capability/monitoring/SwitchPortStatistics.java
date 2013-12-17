package org.opennaas.extensions.openflowswitch.capability.monitoring;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class wrapper containing a Map<Integer, PortStatistics>
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SwitchPortStatistics {

	private Map<Integer, PortStatistics>	statisticsMap;

	public Map<Integer, PortStatistics> getStatistics() {
		return statisticsMap;
	}

	public void setStatistics(Map<Integer, PortStatistics> statistics) {
		this.statisticsMap = statistics;
	}

}
