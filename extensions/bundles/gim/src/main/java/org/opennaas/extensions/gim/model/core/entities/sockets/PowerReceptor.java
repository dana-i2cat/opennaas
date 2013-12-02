package org.opennaas.extensions.gim.model.core.entities.sockets;

public class PowerReceptor extends PowerSocket {

	private PowerSource	attachedTo;

	/**
	 * @return the attachedTo
	 */
	public PowerSource getAttachedTo() {
		return attachedTo;
	}

	/**
	 * @param attachedTo
	 *            the attachedTo to set
	 */
	public void setAttachedTo(PowerSource attachedTo) {
		this.attachedTo = attachedTo;
	}

}
