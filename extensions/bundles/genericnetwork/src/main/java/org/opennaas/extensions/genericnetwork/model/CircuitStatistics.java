package org.opennaas.extensions.genericnetwork.model;

import java.io.Serializable;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class CircuitStatistics implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5141194713845400788L;

	private long				startTime;
	private long				endTime;
	private String				slaFlowId;
	private String				throughput;
	private String				packetLoss;
	private String				delay;
	private String				jitter;
	private String				flowData;

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

	public String getSlaFlowId() {
		return slaFlowId;
	}

	public void setSlaFlowId(String slaFlowId) {
		this.slaFlowId = slaFlowId;
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

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public String getJitter() {
		return jitter;
	}

	public void setJitter(String jitter) {
		this.jitter = jitter;
	}

	public String getFlowData() {
		return flowData;
	}

	public void setFlowData(String flowData) {
		this.flowData = flowData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((delay == null) ? 0 : delay.hashCode());
		result = prime * result + (int) (endTime ^ (endTime >>> 32));
		result = prime * result + ((flowData == null) ? 0 : flowData.hashCode());
		result = prime * result + ((jitter == null) ? 0 : jitter.hashCode());
		result = prime * result + ((packetLoss == null) ? 0 : packetLoss.hashCode());
		result = prime * result + ((slaFlowId == null) ? 0 : slaFlowId.hashCode());
		result = prime * result + (int) (startTime ^ (startTime >>> 32));
		result = prime * result + ((throughput == null) ? 0 : throughput.hashCode());
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
		CircuitStatistics other = (CircuitStatistics) obj;
		if (delay == null) {
			if (other.delay != null)
				return false;
		} else if (!delay.equals(other.delay))
			return false;
		if (endTime != other.endTime)
			return false;
		if (flowData == null) {
			if (other.flowData != null)
				return false;
		} else if (!flowData.equals(other.flowData))
			return false;
		if (jitter == null) {
			if (other.jitter != null)
				return false;
		} else if (!jitter.equals(other.jitter))
			return false;
		if (packetLoss == null) {
			if (other.packetLoss != null)
				return false;
		} else if (!packetLoss.equals(other.packetLoss))
			return false;
		if (slaFlowId == null) {
			if (other.slaFlowId != null)
				return false;
		} else if (!slaFlowId.equals(other.slaFlowId))
			return false;
		if (startTime != other.startTime)
			return false;
		if (throughput == null) {
			if (other.throughput != null)
				return false;
		} else if (!throughput.equals(other.throughput))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CircuitStatistic [startTime=" + startTime + ", endTime=" + endTime + ", slaFlowId=" + slaFlowId + ", throughput=" + throughput + ", packetLoss=" + packetLoss + ", delay=" + delay + ", jitter=" + jitter + ", flowData=" + flowData + "]";
	}
}
