package org.opennaas.extensions.gim.controller;

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

import org.opennaas.extensions.gim.model.core.entities.sockets.PowerSource;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;

public interface IDeliveryController {

	// Aggregated methods

	// public MeasuredLoad getCurrentPowerMetrics(String deliveryId) throws Exception;

	// Per socket methods

	public Energy getReceptorEnergy(String deliveryId, String socketId) throws Exception;

	public double getReceptorEnergyPrice(String deliveryId, String socketId) throws Exception;

	public boolean getSourcePowerStatus(String deliveryId, String socketId) throws Exception;

	public void powerOnSource(String deliveryId, String socketId) throws Exception;

	public void powerOffSource(String deliveryId, String socketId) throws Exception;

	public MeasuredLoad getSourceCurrentPowerMetrics(String deliveryId, String socketId) throws Exception;

	public Energy calculateSourceAggregatedEnergy(String deliveryId, String sourceId) throws Exception;

	public double calculateSourceAggregatedEnergyPrice(String deliveryId, String sourceId) throws Exception;

	public PowerSource getPowerSource(String deliveryId, String sourceId) throws Exception;

}
