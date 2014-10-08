package org.opennaas.extensions.genericnetwork.model.portstatistics;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
public class TimedStatistics {
	
	private long timestamp;
	private String throughput;
	private String packetLoss;
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getThroughput() {
		return throughput;
	}
	
	public void setThroughput(String throughput) {
		this.throughput = throughput;
	}
	
	public String getPacketLoss() {
		return packetLoss;
	}
	
	public void setPacketLoss(String packetLoss) {
		this.packetLoss = packetLoss;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((packetLoss == null) ? 0 : packetLoss.hashCode());
		result = prime * result
				+ ((throughput == null) ? 0 : throughput.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
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
		TimedStatistics other = (TimedStatistics) obj;
		if (packetLoss == null) {
			if (other.packetLoss != null)
				return false;
		} else if (!packetLoss.equals(other.packetLoss))
			return false;
		if (throughput == null) {
			if (other.throughput != null)
				return false;
		} else if (!throughput.equals(other.throughput))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimedStatistics [timestamp=" + timestamp + ", throughput="
				+ throughput + ", packetLoss=" + packetLoss + "]";
	}
}
