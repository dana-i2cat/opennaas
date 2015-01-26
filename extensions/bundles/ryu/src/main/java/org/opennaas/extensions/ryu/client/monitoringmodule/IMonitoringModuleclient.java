package org.opennaas.extensions.ryu.client.monitoringmodule;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface IMonitoringModuleclient {

	@Path("/register_alarm/{dpid}/{port_no}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public void registerAlarm(@PathParam("dpid") String dpid, @PathParam("port_no") String portNumber, AlarmInformation alarmInformation);

	@Path("/delete_alarm/{dpid}/{port_no}")
	@DELETE
	public void unregisterAlarm(@PathParam("dpid") String dpid, @PathParam("port_no") String portNumber);

}
