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

import java.util.List;

import org.opennaas.extensions.gim.model.core.entities.sockets.PowerReceptor;
import org.opennaas.extensions.gim.model.energy.Energy;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;

public interface IConsumerController {

	// Aggregated methods

	public Energy getAggregatedEnergy(String consumerId) throws Exception;

	public double getAggregatedEnergyPrice(String consumerId) throws Exception;

	public boolean getPowerStatus(String consumerId) throws Exception;

	public void powerOn(String consumerId) throws Exception;

	public void powerOff(String consumerId) throws Exception;

	public MeasuredLoad getCurrentPowerMetrics(String consumerId) throws Exception;

	// Per socket methods

	public Energy getReceptorEnergy(String consumerId, String socketId) throws Exception;

	public double getReceptorEnergyPrice(String consumerId, String socketId) throws Exception;

	public boolean getReceptorPowerStatus(String consumerId, String socketId) throws Exception;

	public void powerOnReceptor(String consumerId, String socketId) throws Exception;

	public void powerOffReceptor(String consumerId, String socketId) throws Exception;

	public MeasuredLoad getReceptorCurrentPowerMetrics(String consumerId, String socketId) throws Exception;

	public PowerReceptor getPowerReceptor(String consumerId, String socketId) throws Exception;

	public List<PowerReceptor> getPowerReceptors(String consumerId) throws Exception;

}
