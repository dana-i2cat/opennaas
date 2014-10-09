package org.opennaas.extensions.genericnetwork.model.portstatistics;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
public class TimedStatistics {
	
	private long timestamp;
	
	private String switchId;
	private String portId;
	
	private String throughput;
	private String packetLoss;
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getSwitchId() {
		return switchId;
	}
	
	public void setSwitchId(String switchId) {
		this.switchId = switchId;
	}
	
	public String getPortId() {
		return portId;
	}
	
	public void setPortId(String portId) {
		this.portId = portId;
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
		result = prime * result + ((portId == null) ? 0 : portId.hashCode());
		result = prime * result
				+ ((switchId == null) ? 0 : switchId.hashCode());
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
		if (portId == null) {
			if (other.portId != null)
				return false;
		} else if (!portId.equals(other.portId))
			return false;
		if (switchId == null) {
			if (other.switchId != null)
				return false;
		} else if (!switchId.equals(other.switchId))
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
		return "TimedStatistics [timestamp=" + timestamp + ", switchId="
				+ switchId + ", portId=" + portId + ", throughput="
				+ throughput + ", packetLoss=" + packetLoss + "]";
	}
}
