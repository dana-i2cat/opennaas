package org.opennaas.extensions.bod.capability.l2bod;

import com.google.common.base.Objects;
import org.joda.time.DateTime;

import org.opennaas.extensions.network.model.topology.Interface;

/**
 * Immutable parameters class for RequestConnectionAction.
 */
public class RequestConnectionParameters
{
	public final Interface interface1;
	public final Interface interface2;
	public final long capacity;
	public final int vlanid;
	public final DateTime startTime;
	public final DateTime endTime;

	public RequestConnectionParameters(Interface interface1, Interface interface2,
									   long capacity, int vlanid,
									   DateTime startTime, DateTime endTime)
	{
		this.interface1 = interface1;
		this.interface2 = interface2;
		this.capacity = capacity;
		this.vlanid = vlanid;
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
			.add("vlandid", vlanid)
			.add("startTime", startTime)
			.add("endTime", endTime)
			.toString();
	}
}
