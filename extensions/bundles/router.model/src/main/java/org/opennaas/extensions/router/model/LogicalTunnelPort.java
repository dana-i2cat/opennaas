package org.opennaas.extensions.router.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LogicalTunnelPort extends NetworkPort implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8777159572331512197L;
	private long				peer_unit;

	public void setPeer_unit(long peer_unit) {
		this.peer_unit = peer_unit;
	}

	public long getPeer_unit() {
		return peer_unit;
	}

	public void merge(LogicalTunnelPort hashLogicalPort) {
		super.merge(hashLogicalPort);
		if (peer_unit == 0)
			peer_unit = hashLogicalPort.getPeer_unit();

	}

}
