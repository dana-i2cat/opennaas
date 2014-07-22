package org.opennaas.extensions.pdu.capability;

/*
 * #%L
 * OpenNaaS :: PDU Resource
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
