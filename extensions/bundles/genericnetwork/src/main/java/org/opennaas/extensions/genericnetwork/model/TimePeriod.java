package org.opennaas.extensions.genericnetwork.model;

/**
 * Time period stored in UNIX time.
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class TimePeriod {

	private long	startTime;
	private long	endTime;

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (endTime ^ (endTime >>> 32));
		result = prime * result + (int) (startTime ^ (startTime >>> 32));
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
		TimePeriod other = (TimePeriod) obj;
		if (endTime != other.endTime)
			return false;
		if (startTime != other.startTime)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimePeriod [startTime=" + startTime + ", endTime=" + endTime + "]";
	}

}
