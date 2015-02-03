package org.opennaas.extensions.ryu.client.monitoringmodule;

/*
 * #%L
 * OpenNaaS :: Ryu Resource
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
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
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.opennaas.extensions.ryu.alarm.AlarmInformation;

@Path("/")
public interface IMonitoringModuleclient {

	@Path("/register_alarm/{dpid}/{port_no}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void registerAlarm(@PathParam("dpid") String dpid, @PathParam("port_no") String portNumber, AlarmInformation alarmInformation);

	@Path("/delete_alarm/{dpid}/{port_no}")
	@DELETE
	public void unregisterAlarm(@PathParam("dpid") String dpid, @PathParam("port_no") String portNumber);

}
