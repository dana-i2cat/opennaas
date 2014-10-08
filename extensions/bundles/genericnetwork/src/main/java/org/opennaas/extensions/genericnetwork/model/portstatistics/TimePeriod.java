package org.opennaas.extensions.genericnetwork.model.portstatistics;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
public class TimePeriod {
	
	private long init;
	private long end;
	
	public long getInit() {
		return init;
	}
	
	public void setInit(long init) {
		this.init = init;
	}
	
	public long getEnd() {
		return end;
	}
	
	public void setEnd(long end) {
		this.end = end;
	}
	
	public boolean includes(long timestamp) {
		return (init <= timestamp && end >= timestamp);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (end ^ (end >>> 32));
		result = prime * result + (int) (init ^ (init >>> 32));
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
		if (end != other.end)
			return false;
		if (init != other.init)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimePeriod [init=" + init + ", end=" + end + "]";
	}
}
