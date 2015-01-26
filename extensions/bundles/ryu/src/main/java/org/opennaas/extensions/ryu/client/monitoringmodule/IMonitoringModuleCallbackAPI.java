package org.opennaas.extensions.ryu.client.monitoringmodule;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@Path("/xifi")
public interface IMonitoringModuleCallbackAPI {

	@POST
	@Path("/{url_prefix}/{dpid}/{port_no}")
	public void alarmRegistrationCallback(@PathParam("url_prefix") String urlPrefix, @PathParam("dpid") String dpid,
			@PathParam("port_no") String portNumber);

}
