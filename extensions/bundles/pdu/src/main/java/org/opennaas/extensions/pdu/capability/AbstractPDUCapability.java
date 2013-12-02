package org.opennaas.extensions.pdu.capability;

import org.opennaas.core.resources.descriptor.CapabilityDescriptor;

public abstract class AbstractPDUCapability extends AbstractNotQueueingCapability {

	protected String	powernetId;
	protected String	deliveryId;

	public AbstractPDUCapability(CapabilityDescriptor descriptor) {
		super(descriptor);
		this.powernetId = descriptor.getPropertyValue("powernet.id");
		this.deliveryId = descriptor.getPropertyValue("powernet.delivery.id");
	}

	/**
	 * @return the powernetId
	 */
	public String getPowernetId() {
		return powernetId;
	}

	/**
	 * @param powernetId
	 *            the powernetId to set
	 */
	public void setPowernetId(String powernetId) {
		this.powernetId = powernetId;
	}

	/**
	 * @return the deliveryId
	 */
	public String getDeliveryId() {
		return deliveryId;
	}

	/**
	 * @param deliveryId
	 *            the deliveryId to set
	 */
	public void setDeliveryId(String deliveryId) {
		this.deliveryId = deliveryId;
	}

}
