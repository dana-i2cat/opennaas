package net.i2cat.mantychore.model;

import java.io.Serializable;

public class LogicalTunnelPort extends NetworkPort implements Serializable  {
	
	private long peer_unit;

	public void setPeer_unit(long peer_unit) {
		this.peer_unit = peer_unit;
	}

	public long getPeer_unit() {
		return peer_unit;
	}

}
