package org.opennaas.extensions.bod.capability.l2bod;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;
import org.opennaas.extensions.network.model.topology.Interface;

import com.google.common.base.Objects;

/**
 * Immutable parameters class for RequestConnectionAction.
 */
@XmlRootElement
public class RequestConnectionParameters
{
	public Interface	interface1;
	public Interface	interface2;
	/**
	 * Capacity in B/s
	 */
	public long			capacity;
	/**
	 * VlanId to set into interface1
	 */
	public int			vlanid1;
	/**
	 * VlanId to set into interface2
	 */
	public int			vlanid2;
	public DateTime		startTime;
	public DateTime		endTime;

	/**
	 * 
	 */
	public RequestConnectionParameters() {
	}

	/**
	 * Constructor to use same vlan in both endpoints
	 * 
	 * @param interface1
	 * @param interface2
	 * @param capacity
	 * @param vlanid
	 * @param startTime
	 * @param endTime
	 */
	public RequestConnectionParameters(Interface interface1, Interface interface2,
			long capacity, int vlanid,
			DateTime startTime, DateTime endTime)
	{
		this.interface1 = interface1;
		this.interface2 = interface2;
		this.capacity = capacity;
		this.vlanid1 = vlanid;
		this.vlanid2 = vlanid;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	/**
	 * Constructor allowing different vlans in connection endpoints
	 * 
	 * @param interface1
	 * @param interface2
	 * @param capacity
	 * @param vlanid1
	 * @param vlanid2
	 * @param startTime
	 * @param endTime
	 */
	public RequestConnectionParameters(Interface interface1, Interface interface2,
			long capacity, int vlanid1, int vlanid2,
			DateTime startTime, DateTime endTime)
	{
		this.interface1 = interface1;
		this.interface2 = interface2;
		this.capacity = capacity;
		this.vlanid1 = vlanid1;
		this.vlanid2 = vlanid2;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	@Override
	public String toString()
	{
		return Objects.toStringHelper(this)
				.add("interface1", interface1)
				.add("interface2", interface2)
				.add("capacity", capacity)
				.add("vlandid1", vlanid1)
				.add("vlandid2", vlanid2)
				.add("startTime", startTime)
				.add("endTime", endTime)
				.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (capacity ^ (capacity >>> 32));
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + vlanid1;
		result = prime * result + vlanid2;
		result = prime * result + ((toString() == null) ? 0 : toString().hashCode());
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
		RequestConnectionParameters other = (RequestConnectionParameters) obj;
		if (capacity != other.capacity)
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (interface1 == null) {
			if (other.interface1 != null)
				return false;
		}
		if (interface2 == null) {
			if (other.interface2 != null)
				return false;
		}
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (vlanid1 != other.vlanid1)
			return false;
		if (vlanid2 != other.vlanid2)
			return false;

		if (toString() == null) {
			if (other.toString() != null)
				return false;
		}
		if (!toString().equals(other.toString()))
			return false;

		return true;
	}

}
