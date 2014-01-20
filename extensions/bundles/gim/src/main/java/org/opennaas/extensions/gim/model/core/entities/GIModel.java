package org.opennaas.extensions.gim.model.core.entities;

/*
 * #%L
 * GIM :: GIModel and APC PDU driver
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

import java.util.List;

public class GIModel {

	private String				id;

	private List<PowerConsumer>	consumers;
	private List<PowerDelivery>	deliveries;
	private List<PowerSupply>	supplies;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public List<PowerConsumer> getConsumers() {
		return consumers;
	}

	public void setConsumers(List<PowerConsumer> consumers) {
		this.consumers = consumers;
	}

	public List<PowerDelivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(List<PowerDelivery> deliveries) {
		this.deliveries = deliveries;
	}

	public List<PowerSupply> getSupplies() {
		return supplies;
	}

	public void setSupplies(List<PowerSupply> supplies) {
		this.supplies = supplies;
	}

	@Override
	public String toString() {
		return "GIModel [id=" + id + ",\n consumers=" + consumers + ",\n deliveries=" + deliveries + ",\n supplies=" + supplies + "]";
	}

}
