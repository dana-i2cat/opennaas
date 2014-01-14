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

import org.opennaas.extensions.gim.model.core.IPowerDelivery;
import org.opennaas.extensions.gim.model.core.IPowerMonitorLogging;
import org.opennaas.extensions.gim.model.core.IPowerSupply;
import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.RatedLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

public class PowerSupply extends GIMElement implements IPowerSupply, IPowerMonitorLogging {

	private PowerMonitorLog		powerMonitorLog;
	private Energy				energy;
	private double				pricePerUnit;
	private RatedLoad			ratedLoad;
	private List<PowerDelivery>	powerDeliveries;
	private List<PowerSource>	powerSources;

	public PowerMonitorLog getPowerMonitorLog() {
		return powerMonitorLog;
	}

	public void setPowerMonitorLog(PowerMonitorLog powerMonitorLog) {
		this.powerMonitorLog = powerMonitorLog;
	}

	public Energy getEnergy() {
		return energy;
	}

	public void setEnergy(Energy energy) {
		this.energy = energy;
	}

	public double getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(double pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public RatedLoad getRatedLoad() {
		return ratedLoad;
	}

	public void setRatedLoad(RatedLoad ratedLoad) {
		this.ratedLoad = ratedLoad;
	}

	/**
	 * @return the powerDeliveries
	 */
	public List<PowerDelivery> getPowerDeliveries() {
		return powerDeliveries;
	}

	/**
	 * @param powerDeliveries
	 *            the powerDeliveries to set
	 */
	public void setPowerDeliveries(List<PowerDelivery> powerDeliveries) {
		this.powerDeliveries = powerDeliveries;
	}

	/**
	 * @return the powerSources this supply exposes
	 */
	public List<PowerSource> getPowerSources() {
		return powerSources;
	}

	/**
	 * @param powerSources
	 *            the powerSources to set
	 */
	public void setPowerSources(List<PowerSource> powerSources) {
		this.powerSources = powerSources;
	}

	@Override
	public String toString() {
		String deliveries;
		if (powerDeliveries == null) {
			deliveries = "null";
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			for (IPowerDelivery delivery : powerDeliveries) {
				sb.append("PowerDelivery [id=" + delivery.getId() + "],");
			}
			sb.append("}");
			deliveries = sb.toString();
		}
		return "PowerSupply [id=" + id + ", energy=" + energy + ", pricePerUnit=" + pricePerUnit + ", ratedLoad=" + ratedLoad + ", powerDeliveries=" + deliveries + "]";
	}

}
