package org.opennaas.extensions.power.capabilities;

/*
 * #%L
 * OpenNaaS :: Power :: Capabilities
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

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.gim.model.load.MeasuredLoad;
import org.opennaas.extensions.gim.model.log.PowerMonitorLog;

@Path("/")
public interface IPowerMonitoringCapability extends ICapability {

	/**
	 * 
	 * @return return current MeasuredLoad.
	 * @throws Exception
	 */
	@Path("/powermetrics")
	@GET
	public MeasuredLoad getCurrentPowerMetrics() throws Exception;

	/**
	 * 
	 * @param from
	 * @param to
	 * @return A PowerMonitorLog including all read @code{MesuredLoad}s from @code{from} to @code{to}, both included.
	 * @throws Exception
	 */
	@Path("/powerlog")
	@GET
	public PowerMonitorLog getPowerMetricsByTimeRange(Date from, Date to) throws Exception;

}
