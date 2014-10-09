package org.opennaas.extensions.genericnetwork.model.portstatistics;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *  Wrapper containing {@code List<TimedStatistics>}.
 *  
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TimedPortStatistics {
	
	@XmlElementWrapper
	private List<TimedStatistics> statistics;
	
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
		this.statistics = new ArrayList<TimedStatistics>(other.statistics);
	}

	public List<TimedStatistics> getStatistics() {
		return statistics;
	}

	public void setStatistics(List<TimedStatistics> statistics) {
		this.statistics = statistics;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((statistics == null) ? 0 : statistics.hashCode());
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
		TimedPortStatistics other = (TimedPortStatistics) obj;
		if (statistics == null) {
			if (other.statistics != null)
				return false;
		} else if (!statistics.equals(other.statistics))
			return false;
		return true;
	}
}
