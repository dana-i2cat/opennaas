package org.opennaas.extensions.genericnetwork.capability.nclmonitoring.portstatistics;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimePeriod;
import org.opennaas.extensions.genericnetwork.model.portstatistics.TimedPortStatistics;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
@Path("/")
public interface IPortStatisticsMonitoringCapability extends ICapability {
	
	
	/**
	 * Retrieves port statistics in given time period
	 * 
	 * @param period
	 * @return
	 */
	@GET
	@Path("/")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public TimedPortStatistics getPortStatistics(TimePeriod period);
	
	/**
	 * Retrieves port statistics of given switch in given time period
	 * 
	 * @param period
	 * @param switchId
	 * @return
	 */
	@GET
	@Path("/{switchId}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public TimedPortStatistics getPortStatistics(TimePeriod period, @PathParam("switchId") String switchId);
	
	

}
