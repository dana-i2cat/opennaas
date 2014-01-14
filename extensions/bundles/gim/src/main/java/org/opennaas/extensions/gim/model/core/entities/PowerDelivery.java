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

import org.opennaas.extensions.gim.model.core.IPowerConsumer;
import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.core.IPowerMonitorLogging;
import org.opennaas.extensions.gim.model.core.IPowerSupply;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerReceptor;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;
import org.opennaas.extensions.gim.model.load.DeliveryRatedLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

public class PowerDelivery extends GIMElement implements IPowerDelivery, IPowerMonitorLogging {

	private PowerMonitorLog					powerMonitorLog;
	private DeliveryRatedLoad				deliveryRatedLoad;
	private List<? extends PowerSupply>		powerSupplies;
	private List<? extends PowerConsumer>	powerConsumers;
	private List<? extends PowerReceptor>	powerReceptors;
	private List<? extends PowerSource>		powerSources;

	public PowerMonitorLog getPowerMonitorLog() {
		return powerMonitorLog;
	}

	public void setPowerMonitorLog(PowerMonitorLog powerMonitorLog) {
		this.powerMonitorLog = powerMonitorLog;
	}

	public List<PowerSupply> getPowerSupplies() {
		return (List<PowerSupply>) powerSupplies;
	}

	public void setPowerSupplies(List<PowerSupply> powerSupplies) {
		this.powerSupplies = powerSupplies;
	}

	public DeliveryRatedLoad getDeliveryRatedLoad() {
		return deliveryRatedLoad;
	}

	public void setDeliveryRatedLoad(DeliveryRatedLoad deliveryRatedLoad) {
		this.deliveryRatedLoad = deliveryRatedLoad;
	}

	public List<PowerConsumer> getPowerConsumers() {
		return (List<PowerConsumer>) powerConsumers;
	}

	/**
	 * @param powerConsumers
	 *            the powerConsumers to set
	 */
	public void setPowerConsumers(List<? extends PowerConsumer> powerConsumers) {
		this.powerConsumers = powerConsumers;
	}

	/**
	 * @return the powerReceptors this delivery takes power from
	 */
	public List<PowerReceptor> getPowerReceptors() {
		return (List<PowerReceptor>) powerReceptors;
	}

	/**
	 * @param powerReceptors
	 *            the powerReceptors to set
	 */
	public void setPowerReceptors(List<? extends PowerReceptor> powerReceptors) {
		this.powerReceptors = powerReceptors;
	}

	/**
	 * @return the powerSources this delivery exposes
	 */
	public List<PowerSource> getPowerSources() {
		return (List<PowerSource>) powerSources;
	}

	/**
	 * @param powerSources
	 *            the powerSources to set
	 */
	public void setPowerSources(List<? extends PowerSource> powerSources) {
		this.powerSources = powerSources;
	}

	@Override
	public String toString() {

		String consumers;
		if (powerConsumers == null) {
			consumers = "null";
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			for (IPowerConsumer consumer : powerConsumers) {
				sb.append("PowerConsumer [id=" + consumer.getId() + "],");
			}
			sb.append("}");
			consumers = sb.toString();
		}

		String supplies;
		if (powerSupplies == null) {
			supplies = "null";
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			for (IPowerSupply supply : powerSupplies) {
				sb.append("PowerSupply [id=" + supply.getId() + "],");
			}
			sb.append("}");
			supplies = sb.toString();
		}

		return "PowerDelivery [id=" + id + ", deliveryRatedLoad=" + deliveryRatedLoad + ", powerSupplies=" + supplies + ", powerConsumers=" + consumers + "]";
	}

}
