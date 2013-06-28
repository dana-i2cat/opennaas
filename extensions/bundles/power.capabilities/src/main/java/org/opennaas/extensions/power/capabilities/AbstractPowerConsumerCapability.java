package org.opennaas.extensions.power.capabilities;

import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.extensions.pdu.capability.AbstractNotQueueingCapability;

public abstract class AbstractPowerConsumerCapability extends AbstractNotQueueingCapability {
	
	protected String	powernetId;
	protected String	consumerId;
	
	public AbstractPowerConsumerCapability(CapabilityDescriptor descriptor) {
		super(descriptor);
		this.powernetId = descriptor.getPropertyValue("powernet.id");
		this.consumerId = descriptor.getPropertyValue("powernet.consumer.id");
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
	 * @return the consumerId
	 */
	public String getConsumerId() {
		return consumerId;
	}

	/**
	 * @param consumerId
	 *            the consumerId to set
	 */
	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}
	

}
